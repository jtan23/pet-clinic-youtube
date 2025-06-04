REM Create service policy
aws cloudformation deploy ^
    --stack-name service-policy ^
    --template-file 10-cfn-service-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM

REM IAM Service Accounts for test namespace
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=app-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=owner-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=pet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=visit-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=vet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve

REM IAM Service Accounts for prod namespace
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=app-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=owner-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=pet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=visit-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=vet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ServicePolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
