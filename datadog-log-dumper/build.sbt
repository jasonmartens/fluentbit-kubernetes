lazy val distProject = project
    .in(file("."))
    .enablePlugins(JavaAgent, JavaAppPackaging)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0",
          // For json logging. https://docs.datadoghq.com/logs/log_collection/java/?tab=slf4j#raw-format
        "net.logstash.logback" % "logstash-logback-encoder" % "5.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
      ),
      scalaVersion := "2.13.1",
      name := "datadog-log-dumper",
      version := "0.1"
    )






