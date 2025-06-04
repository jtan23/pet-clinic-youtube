REM 04 Create External DNS Policy and attach the policy to the IAM Service Role instead of NodeInstanceRole

REM Create policy
aws cloudformation deploy ^
    --stack-name external-dns-policy ^
    --template-file 04-cfn-external-dns-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM

REM Set NodeInstanceRoleName
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-nodegroup-petclinic-node-group ^
    --logical-resource-id NodeInstanceRole ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set NodeInstanceRoleName=%%i

REM Attach the policy to the NodeInstanceRole temporarily, so that the external-dns pod can be created
aws iam attach-role-policy ^
    --policy-arn arn:aws:iam::291148202449:policy/ExternalDNSPolicy ^
    --role-name %NodeInstanceRoleName%

REM (Optional) update helm repo, in case it's out of date
helm repo update

REM Install external-dns
helm upgrade --install --wait external-dns external-dns/external-dns

REM (Optional) Check external dns has been installed, make sure the pod is running
kubectl get all
kubectl logs external-dns-xxxx

REM Create IAM service account external-dns
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=default ^
    --name=external-dns ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/ExternalDNSPolicy ^
    --override-existing-serviceaccounts ^
    --region=ap-southeast-2 ^
    --approve

REM (Optional) Check external-dns service account
kubectl describe serviceaccount external-dns

REM Detach the policy from NodeInstanceRole, since we are using the above created service account going forward
aws iam detach-role-policy ^
    --policy-arn arn:aws:iam::291148202449:policy/ExternalDNSPolicy ^
    --role-name %NodeInstanceRoleName%

REM Refresh external-dns pods, otherwise the existing pod will keep failing
for /f "tokens=1" %%a in ('kubectl get pods ^| find "external-dns"') do kubectl delete pods %%a
