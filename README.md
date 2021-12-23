#Preparations to run on local machine with 16gb ram

## 1. If you use Docker with wsl2:

#### Create file .wslconfig in directory C:/Users/username/ and add settings:
    [wsl2]
    memory=8GB
    swap=0
    localhostForwarding=true

## 2. Deploy Confluent Platform for Kubernetes
#### 1. Set up the Helm Chart:
    helm repo add confluentinc https://packages.confluent.io/helm
#### 2. Install Confluent For Kubernetes using Helm:
    helm upgrade --install operator confluentinc/confluent-for-kubernetes
#### 3. Check that the Confluent For Kubernetes pod comes up and is running:
    kubectl get pods
#### 4. Deploy Confluent Platform with the above configuration:
    kubectl apply -f confluent-platform-singlenode.yaml
#### 5. Set up port forwarding to Control Center web UI from local machine:
    kubectl expose pod controlcenter-0 --type=LoadBalancer
#### 6. Browse to Control Center:
    http://localhost:9021

#How to run microservice

## 1. Build application image
### There are 2 variants how to do it:

####1.1 Build image by Maven
    mvn spring-boot:build-image
####1.2 Build by Docker
    1. Build project by maven:

        mvn install

    2. Build image by docker command:

        docker build . -t get-new-joke
## 2. Create deployment file for application

    kubectl create deployment service1 --image get-new-joke -o yaml --dry-run=client > deployment.yaml

    If you don't push your application to DockerHub, you need to add this line  to generated file:

    imagePullPolicy: Never
## 3. Deploy the application to Kubernates

    kubectl apply -f deployment.yaml