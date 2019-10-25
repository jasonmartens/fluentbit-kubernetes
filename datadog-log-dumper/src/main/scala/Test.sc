import org.json4s._
import org.json4s.native.JsonMethods._
val log = """{"@timestamp":1572026149.0,"log":"\tdelegation.token.master.key = null\n","stream":"stdout","time":"2019-10-24T20:17:57.091027Z","kubernetes":{"pod_name":"my-cluster-kafka-0","namespace_name":"kafka","pod_id":"503de7fd-cd7d-40bd-ab56-cb619a063af0","labels":{"controller-revision-hash":"my-cluster-kafka-7bd6f8457b","statefulset.kubernetes.io/pod-name":"my-cluster-kafka-0","strimzi.io/cluster":"my-cluster","strimzi.io/kind":"Kafka","strimzi.io/name":"my-cluster-kafka"},"annotations":{"strimzi.io/clients-ca-cert-generation":"0","strimzi.io/cluster-ca-cert-generation":"0","strimzi.io/generation":"0"},"host":"docker-desktop","container_name":"kafka","docker_id":"22c6f4a7bfee53e4aebbf180cf4d349b951fa5dbbcffc30c7691137b1f50f391"}}"""
val json = parse(log)
val updated = json.transformField {
  case JField("@timestamp", JDouble(num)) => ("@timestamp", JLong((num * 1000).toLong))
}
val timestamp = json \\ "@timestamp" match {
  case JDouble(num) => (num * 1000).toLong
  case _ => throw new RuntimeException()
}
val instant = java.time.Instant.ofEpochMilli(timestamp.toLong)

val jsonString = pretty(render(updated))
