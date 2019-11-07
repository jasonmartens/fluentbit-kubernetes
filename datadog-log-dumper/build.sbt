lazy val distProject = project
    .in(file("."))
    .enablePlugins(JavaAgent, JavaAppPackaging)
    .settings(
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio-streams" % "1.0.0-RC16",
        "dev.zio" %% "zio-kafka"   % "0.3.2",
        "com.github.pureconfig" %% "pureconfig" % "0.12.1",
        "com.softwaremill.sttp.client" %% "core" % "2.0.0-M7",
        "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % "2.0.0-M7",
          // For json logging. https://docs.datadoghq.com/logs/log_collection/java/?tab=slf4j#raw-format
        "net.logstash.logback" % "logstash-logback-encoder" % "5.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "org.json4s" %% "json4s-native" % "3.6.7",
      ),
      scalaVersion := "2.12.10",
      name := "datadog-log-dumper",
      version := "0.1"
    )
