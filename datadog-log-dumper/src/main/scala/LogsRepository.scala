import zio.ZIO
import zio.console.Console

sealed trait SubmitResult
case object SubmitSuccess extends SubmitResult
case class SubmitFailure(error: String) extends SubmitResult

trait LogsRepository extends Serializable {
  val logsRepository: LogsRepository.Service[Any]
}

object LogsRepository extends Serializable {

  trait Service[R] extends Serializable {
    def submitLog(log: String): ZIO[Console, Throwable, SubmitResult]
  }
}
