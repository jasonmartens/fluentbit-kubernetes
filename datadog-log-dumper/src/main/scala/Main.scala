import zio._
import zio.duration._
import zio.kafka.client._
import zio.ZManaged
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.client.{Consumer, ConsumerSettings}
import zio.kafka.client.serde._
import zio.console.putStrLn
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import ServiceConfig.Config

object Main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program = for {
      config <- ZIO.fromEither(ConfigSource.default.load[ServiceConfig.Config])
      consumerSettings = ServiceConfig.kafkaConsumerSettings(config.kafkaConfig)
      consumer = Consumer.make(consumerSettings)
      p <- consumer.use { c =>
        c.subscribeAnd(Subscription.Topics(Set("logs")))
          .plainStream(Serde.string, Serde.string)
          .flattenChunks
          .tap(cr => putStrLn(s"key: ${cr.record.key}, value: ${cr.record.value}"))
          .map(_.offset)
          .aggregate(Consumer.offsetBatches)
          .mapM(_.commit)
          .runDrain
      }
    } yield p

    program.foldM(
      failure = err => putStrLn(s"execution failed with: $err") *> ZIO.succeed(1),
      success = _ => ZIO.succeed(0)
    )
  }
}
