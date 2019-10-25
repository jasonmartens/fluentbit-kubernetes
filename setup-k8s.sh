# Not 100% sure if this is needed
kubectl label node docker-desktop zone=myzones

# Set up kafka using https://strimzi.io/quickstarts/minikube/
kubectl create namespace kafka
kubectl -n kafka apply -f kafka/strimzi-cluster-operator-0.14.0.yaml 
kubectl -n kafka apply -f kafka/kafka-ephemeral.yaml
echo "Waiting for kafka to be ready..."
kubectl -n kafka wait kafka/my-cluster --for=condition=Ready --timeout=300s
echo "To access kafka outside of kubernetes use the address:"
kubectl -n kafka get service my-cluster-kafka-external-bootstrap -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}{"\n"}'


kubectl create namespace logging
kubectl -n logging create -f fluentbit/fluent-bit-service-account.yaml
kubectl -n logging create -f fluentbit/fluent-bit-role.yaml
kubectl -n logging create -f fluentbit/fluent-bit-role-binding.yaml
kubectl -n logging create -f fluentbit/fluent-bit-configmap.yaml
kubectl -n logging create -f fluentbit/fluent-bit-ds-minikube.yaml
