REM 05 Create AWS Load Balancer Controller and it's IAM service account

REM Create policy
aws cloudformation deploy ^
    --stack-name aws-load-balancer-controller-policy ^
    --template-file 05-cfn-aws-load-balancer-controller-policy.yaml ^
    --capabilities CAPABILITY_NAMED_IAM

REM Create IAM service account aws-load-balancer-controller
eksctl create iamserviceaccount ^
    --cluster=petclinic-cluster ^
    --namespace=kube-system ^
    --name=aws-load-balancer-controller ^
    --attach-policy-arn=arn:aws:iam::291148202449:policy/AWSLoadBalancerControllerIAMPolicy ^
    --override-existing-serviceaccounts ^
    --region=ap-southeast-2 ^
    --approve

REM (Optional) Check aws-load-balancer-controller service account
kubectl describe serviceaccount aws-load-balancer-controller -n kube-system

REM (Optional) update helm repo, in case it's out of date
helm repo update

REM Install aws-load-balancer-controller
helm install --wait aws-load-balancer-controller eks/aws-load-balancer-controller ^
    -n kube-system ^
    --set clusterName=petclinic-cluster ^
    --set serviceAccount.create=false ^
    --set serviceAccount.name=aws-load-balancer-controller

REM (Optional) Check aws-load-balancer-controller has been installed, make sure the pod is running
kubectl get all -n kube-system
kubectl logs -n kube-system aws-load-balancer-controller-xxxx
kubectl describe deployment aws-load-balancer-controller -n kube-system
