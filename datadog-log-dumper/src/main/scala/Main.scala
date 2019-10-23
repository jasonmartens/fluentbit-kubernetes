import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl._
import akka.stream.{ActorMaterializer, Materializer}
import akka.Done
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher
  val config = system.settings.config.getConfig("logging-kafka-consumer")
  val consumerSettings = ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")
    .withGroupId("group1")

  println(s"Starting kafka consumer from ${config.toString}...")
  Consumer
    .committableSource(consumerSettings, Subscriptions.topics("logs"))
    .runWith(Sink.foreach{ msg => println(s"received: ${msg.toString()}")})

  while (true) {
    Thread.sleep(100)
  }
  println("Server exiting...")

}
