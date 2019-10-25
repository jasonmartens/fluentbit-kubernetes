** What is this repository?

This is an experiment to get logs out of kubernetes and into Datadog.

The basic flow of data is:
App -> Stdout logs -> kubernetes log files -> Fluent Bit -> Kafka -> datadog-log-dumper -> Datadog

This is designed to run on Kubernetes inside Docker for Mac.

*** Getting Started
1. Install Docker for Mac, and turn on Kubernetes
1. Once `kubectl` is working, run `setup-k8s.sh`. This will install kafka & fluent bit. You should be able to verify that it's working by running the Kafka consumer: ```kubectl -n kafka run kafka-consumer -ti --image=strimzi/kafka:0.14.0-kafka-2.3.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic logs --from-beginning```
1. Build the datadog-log-dumper. Change to the `datadog-log-dumper` directory and run `docker build . -f Dockerfile -t datadog-log-dumper`. You also need to add the datadog API key to kubernetes secrets: `kubectl -n logging create secret generic datadog-api-key --from-literal=secret="<key here>"`
1. Deploy the log dumper with `kubectl apply -f kubernetes.yaml`

At this point you should be able to log into your datadog account and see the logs coming in from kubernetes!