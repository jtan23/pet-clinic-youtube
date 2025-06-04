REM Set MYSQL_ENDPOINT
for /f %%i in (' ^
aws ssm get-parameters ^
    --names pet-clinic-mysql-endpoint ^
    --query Parameters[0].Value ^
    --output text ^
') do set MYSQL_ENDPOINT=%%i

sed -e "s/TARGET_PORT/8080/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/app/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -
sed -e "s/TARGET_PORT/8081/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/owner/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -
sed -e "s/TARGET_PORT/8082/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN/_VALUEpetclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/pet/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -
sed -e "s/TARGET_PORT/8083/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/visit/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -
sed -e "s/TARGET_PORT/8084/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/test/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/vet/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -
sed -e "s/TARGET_PORT/8085/g" ^
-e "s/DB_ENDPOINT/%MYSQL_ENDPOINT%/g" ^
-e "s/NAME_SPACE/default/g" ^
-e "s/PETCLINIC_DOMAIN_VALUE/petclinic.test.jtan23.net/g" ^
-e "s/OAUTH2_DOMAIN_VALUE/oauth2server.jtan23.net/g" ^
-e "s/CLIENT_ID_VALUE/petclinic-client-test/g" ^
-e "s/APP_NAME/oauth2server/g" ^
    16-k8s-service-deployment.yaml | kubectl delete -f -

sed "s/NAME_SPACE/default/g; s/INGRESS_NAME/oauth2server/g; s/SERVICE_NAME/oauth2server/g; s/SUB_DOMAIN/oauth2server/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/INGRESS_NAME/petclinic-test/g; s/SERVICE_NAME/app/g; s/SUB_DOMAIN/petclinic.test/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/INGRESS_NAME/petclinic-prod/g; s/SERVICE_NAME/app/g; s/SUB_DOMAIN/petclinic/g" ^
    06-k8s-petclinic-ingress-service.yaml | kubectl delete -f -

kubectl delete namespace prod
kubectl delete namespace test

helm uninstall --wait -n kube-system aws-load-balancer-controller
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=kube-system --name=aws-load-balancer-controller
aws cloudformation delete-stack --stack-name aws-load-balancer-controller-policy
aws cloudformation wait stack-delete-complete --stack-name aws-load-balancer-controller-policy

helm uninstall --wait external-dns
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=default --name=external-dns
aws cloudformation delete-stack --stack-name external-dns-policy
aws cloudformation wait stack-delete-complete --stack-name external-dns-policy

for /f %i in (' ^
aws iam list-open-id-connect-providers ^
    --query OpenIDConnectProviderList[0].Arn ^
    --output text ^
') do set OIDCArn=%i
aws iam delete-open-id-connect-provider --open-id-connect-provider-arn %OIDCArn%

aws cloudformation delete-stack --stack-name mysql-database
aws cloudformation wait stack-delete-complete --stack-name mysql-database

eksctl delete cluster --disable-nodegroup-eviction -f 01-eksctl-cluster.yaml
