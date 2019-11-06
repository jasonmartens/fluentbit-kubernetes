# Build base, needs SBT
FROM mozilla/sbt:latest
ADD src /build/src
ADD build.sbt /build/build.sbt
ADD project /build/project
WORKDIR /build
RUN sbt docker:stage

# Run base
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=0 /build/target/docker/stage/opt/docker/bin ./bin
COPY --from=0 /build/target/docker/stage/opt/docker/lib ./lib
ENV KAFKA_BOOTSTRAP_HOST=my-cluster-kafka-bootstrap.kafka.svc.cluster.local
ENV KAFKA_BOOTSTRAP_PORT=9092
CMD ["/app/bin/datadog-log-dumper", "-jvm-debug", "*:8091", "-v", \
  "-Ddd.service.name=datadog-log-dumper", \
  "-Ddatadog.slf4j.simpleLogger.defaultLogLevel=debug", \
  "-Dcom.sun.management.jmxremote=true", \
  "-Dcom.sun.management.jmxremote.port=8092", \
  "-Dcom.sun.management.jmxremote.authenticate=false", \
  "-Dcom.sun.management.jmxremote.ssl=false", \
  "-Djava.rmi.server.hostname=localhost"]

