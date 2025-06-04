REM (Optional) Sign in to AWS cli if not yet signed in
aws sso login
aws sts get-caller-identity

REM (Optional) Download eksctl ClusterConfig schema if not yet downloaded
eksctl utils schema > eksctl.yaml.json

REM Create EKS cluster and node group, along with essential VPC and security. About 13 mins.
eksctl create cluster -f 01-eksctl-cluster.yaml

REM (Optional) Check the created cluster and node group
eksctl get cluster
eksctl get nodegroup --cluster petclinic-cluster
eksctl get iamidentitymapping --cluster petclinic-cluster
eksctl utils describe-stacks --cluster petclinic-cluster
aws eks describe-cluster --name petclinic-cluster

REM Create MySQL database cluster. About 13 mins.
start "Create Database" setup-02-database.bat

REM Associate IAM OIDC provider, new entry under 'IAM - Identity Providers'
eksctl utils associate-iam-oidc-provider --region ap-southeast-2 --cluster petclinic-cluster --approve

REM (Optional) Check OIDC
aws iam list-open-id-connect-providers
for /f %i in (' ^
aws iam list-open-id-connect-providers ^
    --query OpenIDConnectProviderList[0].Arn ^
    --output text ^
') do set OIDCArn=%i
aws iam get-open-id-connect-provider --open-id-connect-provider-arn %OIDCArn%

REM Create External DNS
start "Create External DNS" setup-04-external-dns.bat

REM Create AWS Load Balancer
start "Create AWS Load Balancer" setup-05-aws-load-balancer.bat

REM Create Kubernetes namespaces: prod and test. We will deploy our services to both environments later.
kubectl create namespace test
kubectl create namespace prod

REM (Optional) Check created namespaces
kubectl get namespaces
kubectl describe namespace test

REM Create test and prod ingress services
sed "s/NAME_SPACE/test/g; s/HOST_NAME/petclinic.test.jtan23.net/g" 06-k8s-petclinic-ingress-service.yaml | kubectl apply -f -
sed "s/NAME_SPACE/prod/g; s/HOST_NAME/petclinic.jtan23.net/g"      06-k8s-petclinic-ingress-service.yaml | kubectl apply -f -

REM (Optional) Check service accounts created
eksctl get iamserviceaccount --cluster=petclinic-cluster

REM (Optional) If not already done:
REM Run 'upload-to-aws-ecr.bat' in all services, so that the below deployment can pick up the images from AWS ECR

REM Deploy applications to test namespace, may need to wait for the AWS Load Balancers to be active.
sed "s/NAME_SPACE/test/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 16-k8s-service-deployment.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 16-k8s-service-deployment.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 16-k8s-service-deployment.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 16-k8s-service-deployment.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 16-k8s-service-deployment.yaml | kubectl apply -f -

REM (Optional) Check the pods are running
kubectl get pod -n test
kubectl logs -n test app-service-xxxx
kubectl describe pod -n test app-service-xxxx
