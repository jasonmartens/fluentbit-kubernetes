import zio.kafka.client.ConsumerSettings
import zio.duration._

object ServiceConfig {

  final case class Config(
    kafkaConfig: KafkaConfig,
    datadogConfig: DatadogConfig
  )

  final case class KafkaConfig(
    port: Int,
    bootstrapHost: String,
  )

  final case class DatadogConfig(
    apiKey: String
  )

  def kafkaConsumerSettings(c: KafkaConfig): ConsumerSettings = {
    ConsumerSettings(
      bootstrapServers          = List(s"${c.bootstrapHost}:${c.port}"),
      groupId                   = "group",
      clientId                  = "client",
      closeTimeout              = 30.seconds,
      extraDriverSettings       = Map(),
      pollInterval              = 250.millis,
      pollTimeout               = 50.millis,
      perPartitionChunkPrefetch = 2
    )
  }
}
