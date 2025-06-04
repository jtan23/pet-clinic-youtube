REM CI/CD

REM Create CICD stacks for all services
aws cloudformation create-stack --stack-name cicd-app   --template-body file://21-cfn-cicd.yaml ^
    --parameters ParameterKey=CodeCommitRepositoryName,ParameterValue=app   --capabilities CAPABILITY_IAM
aws cloudformation create-stack --stack-name cicd-owner --template-body file://21-cfn-cicd.yaml ^
    --parameters ParameterKey=CodeCommitRepositoryName,ParameterValue=owner --capabilities CAPABILITY_IAM
aws cloudformation create-stack --stack-name cicd-pet   --template-body file://21-cfn-cicd.yaml ^
    --parameters ParameterKey=CodeCommitRepositoryName,ParameterValue=pet   --capabilities CAPABILITY_IAM
aws cloudformation create-stack --stack-name cicd-vet   --template-body file://21-cfn-cicd.yaml ^
    --parameters ParameterKey=CodeCommitRepositoryName,ParameterValue=vet   --capabilities CAPABILITY_IAM
aws cloudformation create-stack --stack-name cicd-visit --template-body file://21-cfn-cicd.yaml ^
    --parameters ParameterKey=CodeCommitRepositoryName,ParameterValue=visit --capabilities CAPABILITY_IAM

REM Wait for the stacks to be fully created
aws cloudformation wait stack-create-complete --stack-name cicd-app
aws cloudformation wait stack-create-complete --stack-name cicd-owner
aws cloudformation wait stack-create-complete --stack-name cicd-pet
aws cloudformation wait stack-create-complete --stack-name cicd-vet
aws cloudformation wait stack-create-complete --stack-name cicd-visit

REM We need to map IAM service role to the cluster, so that kubernetes can access AWS resources.

REM Map service role for pipeline 'app'
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-app ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceAppName=%%i
eksctl create iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --username app-RoleCodeBuildService ^
    --group system:masters ^
    --no-duplicate-arns ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceAppName%

REM Map service role for pipeline 'owner'
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-owner ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceOwnerName=%%i
eksctl create iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --username owner-RoleCodeBuildService ^
    --group system:masters ^
    --no-duplicate-arns ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceOwnerName%

REM Map service role for pipeline 'pet'
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-pet ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServicePetName=%%i
eksctl create iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --username pet-RoleCodeBuildService ^
    --group system:masters ^
    --no-duplicate-arns ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServicePetName%

REM Map service role for pipeline 'vet'
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-vet ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceVetName=%%i
eksctl create iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --username vet-RoleCodeBuildService ^
    --group system:masters ^
    --no-duplicate-arns ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceVetName%

REM Map service role for pipeline 'visit'
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name cicd-visit ^
    --logical-resource-id RoleCodeBuildService ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set RoleCodeBuildServiceVisitName=%%i
eksctl create iamidentitymapping ^
    --cluster petclinic-cluster ^
    --region=ap-southeast-2 ^
    --username visit-RoleCodeBuildService ^
    --group system:masters ^
    --no-duplicate-arns ^
    --arn arn:aws:iam::291148202449:role/%RoleCodeBuildServiceVisitName%

REM (Optional) Check created IAM identity mappings
eksctl get iamidentitymapping -c petclinic-cluster
kubectl get -n kube-system configmap/aws-auth -o yaml
