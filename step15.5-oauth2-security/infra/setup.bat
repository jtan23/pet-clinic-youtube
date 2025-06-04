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

REM Create OAuth2 Server ingress services
sed "s/NAME_SPACE/default/g; s/INGRESS_NAME/oauth2server/g; s/SERVICE_NAME/oauth2server/g; s/SUB_DOMAIN/oauth2server/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl apply -f -
REM Create test ingress services
sed "s/NAME_SPACE/test/g; s/INGRESS_NAME/petclinic-test/g; s/SERVICE_NAME/app/g; s/SUB_DOMAIN/petclinic.test/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl apply -f -
REM Create prod ingress services
sed "s/NAME_SPACE/prod/g; s/INGRESS_NAME/petclinic-prod/g; s/SERVICE_NAME/app/g; s/SUB_DOMAIN/petclinic/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl apply -f -

REM (Optional) Check service accounts created
eksctl get iamserviceaccount --cluster=petclinic-cluster

REM (Optional) If not already done:
REM Run 'upload-to-aws-ecr.bat' in all services, so that the below deployment can pick up the images from AWS ECR

REM Set MYSQL_ENDPOINT
for /f %%i in (' ^
aws ssm get-parameters ^
    --names pet-clinic-mysql-endpoint ^
    --query Parameters[0].Value ^
    --output text ^
') do set MYSQL_ENDPOINT=%%i

REM Deploy applications to test namespace, may need to wait for the AWS Load Balancers to be active.
sed -e "s/TARGET_PORT/8080/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/app/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -
sed -e "s/TARGET_PORT/8081/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/owner/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -
sed -e "s/TARGET_PORT/8082/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN/_VALUEpetclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/pet/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -
sed -e "s/TARGET_PORT/8083/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/visit/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -
sed -e "s/TARGET_PORT/8084/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/vet/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -
sed -e "s/TARGET_PORT/8085/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/default/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/oauth2server/g" ^
    16-k8s-service-deployment.yaml | kubectl apply -f -

REM (Optional) Check the pods are running
kubectl get pod -n test
kubectl logs -n test app-service-xxxx
kubectl describe pod -n test app-service-xxxx
