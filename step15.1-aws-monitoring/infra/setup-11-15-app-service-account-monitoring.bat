REM Create service policy
start aws cloudformation deploy ^
    --stack-name app-policy ^
    --template-file 11-cfn-app-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM
start aws cloudformation deploy ^
    --stack-name owner-policy ^
    --template-file 12-cfn-owner-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM
start aws cloudformation deploy ^
    --stack-name pet-policy ^
    --template-file 13-cfn-pet-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM
start aws cloudformation deploy ^
    --stack-name vet-policy ^
    --template-file 14-cfn-vet-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM
start aws cloudformation deploy ^
    --stack-name visit-policy ^
    --template-file 15-cfn-visit-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM

REM IAM Service Accounts for test namespace
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=app-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/AppPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=owner-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/OwnerPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=pet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/PetPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=visit-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/VisitPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=test ^
    --name=vet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/VetPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve

REM IAM Service Accounts for prod namespace
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=app-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/AppPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=owner-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/OwnerPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=pet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/PetPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=visit-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/VisitPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
start eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=prod ^
    --name=vet-iam-service-account ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/VetPolicy ^
    --region=ap-southeast-2 ^
    --override-existing-serviceaccounts ^
    --approve
