import org.json4s._
import org.json4s.native.JsonMethods._
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.model.StatusCode
import zio.{Task, ZIO}
import zio.console.{putStrLn, Console}

trait DatadogLogsRepository extends LogsRepository {

  protected val apiKey: String
  protected implicit val backend: SttpBackend[Task, Nothing, WebSocketHandler]

  override val logsRepository: LogsRepository.Service[Any] = new LogsRepository.Service[Any] {
    override def submitLog(log: String): ZIO[Console, Throwable, SubmitResult] = {
      val request = try {
        val json = parse(log).transformField {
          case JField("@timestamp", JDouble(num)) => ("@timestamp", JLong((num * 1000).toLong))
        }
        val msg = compact(render(json))
        basicRequest
          .body(msg)
          .contentType("application/json")
          .post(uri"https://http-intake.logs.datadoghq.com/v1/input/${apiKey}?ddtags=env:development")
      } catch {
        case _: RuntimeException =>
          basicRequest
            .body(log)
            .post(uri"https://http-intake.logs.datadoghq.com/v1/input/${apiKey}?ddtags=env:development")
        case _: ParserUtil.ParseException =>
          basicRequest
            .body(log)
            .post(uri"https://http-intake.logs.datadoghq.com/v1/input/${apiKey}?ddtags=env:development")
      }
      request.send().map {
        case response if response.code == StatusCode.Ok =>
          SubmitSuccess
        case response =>
          SubmitFailure(s"Failed with status code: ${response.code}. Body: ${response.body}")
      }
    }
  }
}
