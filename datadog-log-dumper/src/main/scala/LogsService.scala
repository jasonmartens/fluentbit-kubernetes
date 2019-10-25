import zio.ZIO
import LogsService.{LogsServiceEnvironment, Service}
import zio.console.Console

object LogsService {
  type LogsServiceEnvironment = LogsRepository with Console

  trait Service[R] {
    def submitLog(log: String): ZIO[R, Throwable, SubmitResult]
  }
}

object LogsServiceImpl extends Service[LogsServiceEnvironment] {

  override def submitLog(log: String): ZIO[LogsServiceEnvironment with Console, Throwable, SubmitResult] = {
    ZIO.accessM[LogsRepository with Console] { env =>
      env.logsRepository.submitLog(log)
    }
  }
}
