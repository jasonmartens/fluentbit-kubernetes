# Set up kafka using https://strimzi.io/quickstarts/minikube/
kubectl create namespace kafka
kubectl apply -f kafka/strimzi-cluster-operator-0.14.0.yaml -n kafka 
kubectl apply -f kafka/kafka-persistent-single.yaml -n kafka

kubectl create namespace logging
kubectl create -f fluentbit/fluent-bit-service-account.yaml
kubectl create -f fluentbit/fluent-bit-role.yaml
kubectl create -f fluentbit/fluent-bit-role-binding.yaml
kubectl create -f fluentbit/fluent-bit-configmap.yaml
kubectl create -f fluentbit/fluent-bit-ds-minikube.yaml

# Read logs from kafka
# kubectl -n kafka run kafka-consumer -ti --image=strimzi/kafka:0.14.0-kafka-2.3.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic logs --from-beginning