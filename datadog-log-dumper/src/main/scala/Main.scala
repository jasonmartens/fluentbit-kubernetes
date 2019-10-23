import zio._
import zio.duration._
import zio.kafka.client._
import zio.ZManaged
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.client.{ Consumer, ConsumerSettings }
import zio.kafka.client.serde._
import zio.console.putStrLn

object Main extends App {
  val consumerSettings: ConsumerSettings =
    ConsumerSettings(
      bootstrapServers          = List("localhost:9094"),
      groupId                   = "group",
      clientId                  = "client",
      closeTimeout              = 30.seconds,
      extraDriverSettings       = Map(),
      pollInterval              = 250.millis,
      pollTimeout               = 50.millis,
      perPartitionChunkPrefetch = 2
    )
  val consumer: ZManaged[Clock with Blocking, Throwable, Consumer] =
    Consumer.make(consumerSettings)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    consumer.use { c =>
      c.subscribeAnd(Subscription.Topics(Set("logs")))
        .plainStream(Serde.string, Serde.string)
        .flattenChunks
        .tap(cr => putStrLn(s"key: ${cr.record.key}, value: ${cr.record.value}"))
        .map(_.offset)
        .aggregate(Consumer.offsetBatches)
        .mapM(_.commit)
        .runDrain
      ZIO.unit
    }
  }.foldM (
    failure = err => putStrLn(s"execution failed with: $err") *> ZIO.succeed(1),
    success = _ => ZIO.succeed(0)
  )
}
