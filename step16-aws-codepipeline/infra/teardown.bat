for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-app ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceAppName=%i
eksctl delete iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceAppName%

for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-owner ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceOwnerName=%i
eksctl delete iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceOwnerName%

for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-pet ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServicePetName=%i
eksctl delete iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServicePetName%

for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-vet ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceVetName=%i
eksctl delete iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceVetName%

for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-visit ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceVisitName=%i
eksctl delete iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceVisitName%

aws cloudformation delete-stack --stack-name cicd-app
aws cloudformation delete-stack --stack-name cicd-owner
aws cloudformation delete-stack --stack-name cicd-pet
aws cloudformation delete-stack --stack-name cicd-vet
aws cloudformation delete-stack --stack-name cicd-visit

for /f "tokens=3" %a in ('aws s3 ls ^| find "codepipeline"') do aws s3 rb --force s3://%a

sed "s/NAME_SPACE/prod/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 16-k8s-service-deployment.yaml | kubectl delete -f -

sed "s/NAME_SPACE/test/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 16-k8s-service-deployment.yaml | kubectl delete -f -

eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=prod --name=app-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=prod --name=owner-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=prod --name=pet-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=prod --name=vet-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=prod --name=visit-iam-service-account

eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=test --name=app-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=test --name=owner-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=test --name=pet-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=test --name=vet-iam-service-account
eksctl delete iamserviceaccount --cluster=petclinic-cluster --namespace=test --name=visit-iam-service-account

aws cloudformation delete-stack --stack-name service-policy
aws cloudformation wait stack-delete-complete --stack-name service-policy

sed "s/NAME_SPACE/test/g; s/HOST_NAME/petclinic.test.jtan23.net/g" 06-k8s-petclinic-ingress-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/prod/g; s/HOST_NAME/petclinic.jtan23.net/g"      06-k8s-petclinic-ingress-service.yaml | kubectl delete -f -

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

eksctl delete cluster -f 01-eksctl-cluster.yaml
