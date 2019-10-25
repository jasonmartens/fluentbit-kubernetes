import java.io

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.{putStrLn, Console}
import zio.kafka.client.{Consumer, _}
import zio.kafka.client.serde._
import ServiceConfig.Config
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import zio.system.System

object Main extends App {

  type AppEnvironment = Clock with Console with System with Blocking with DatadogLogsRepository

  override def run(args: List[String]) = {
    program.fold(_ => 1, _ => 0)
  }

  val program: ZIO[zio.ZEnv, io.Serializable, Unit] = for {
    config <- ZIO.fromEither(ConfigSource.default.load[Config])
    consumerSettings = ServiceConfig.kafkaConsumerSettings(config.kafkaConfig)
    httpBackend <- AsyncHttpClientZioBackend()
    server <- ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      val consumer = Consumer.make(consumerSettings)
      consumer.use { c =>
        c.subscribeAnd(Subscription.Topics(Set("logs")))
          .plainStream(Serde.string, Serde.string)
          .flattenChunks
          .tap{ cr =>
            LogsServiceImpl.submitLog(cr.record.value()).map {
              case SubmitSuccess =>
                putStrLn("Successfully submitted log")
                cr
              case SubmitFailure(message) =>
                putStrLn(s"Failed to submit log message: $message")
                cr
            }
          }
          .map(_.offset)
          .aggregate(Consumer.offsetBatches)
          .mapM(_.commit)
          .runDrain
      }
    }.provideSome[ZEnv] { base =>
      new Clock with Console with System with Blocking with DatadogLogsRepository {
        override val clock: Clock.Service[Any] = base.clock
        override val console: Console.Service[Any] = base.console
        override val system: System.Service[Any] = base.system
        override val blocking: Blocking.Service[Any] = base.blocking
        override val backend: SttpBackend[Task, Nothing, WebSocketHandler] = httpBackend
        override val apiKey: String = config.datadogConfig.apiKey
      }
    }
  } yield server
}
