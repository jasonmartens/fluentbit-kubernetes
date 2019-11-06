README.md

** SSH Jumpbox **
This directory has what is needed to build & run a jumpbox inside of kubernetes which allows port forwarding over SSH to access internal kubernetes services. 

 * Copy your SSH public key to this directory and name it `id_rsa.pub`
 * Run `docker build -f Dockerfile . -t jumpbox`
 * Run `kubectl apply -n <namespace> -f kubernetes.yaml`
 * The jumpbox uses a kubernetes NodePort to expose the SSH server. You can find the port by running `kubectl get services -n <namespace>` and looking at the PORT(s) column. In this example it's listening on 31417:
 ```
$ kubectl get services -n logging
NAME      TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
jumpbox   NodePort   10.100.249.122   <none>        2222:31417/TCP   4h11m
```

You can now SSH to the jumpbox with `ssh root@localhost -p 31417`.