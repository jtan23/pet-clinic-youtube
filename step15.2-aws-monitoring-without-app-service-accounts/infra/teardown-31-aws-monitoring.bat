sed "s/NAME_SPACE/test/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 36-k8s-virtual-node-router-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 36-k8s-virtual-node-router-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 36-k8s-virtual-node-router-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 36-k8s-virtual-node-router-service.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 36-k8s-virtual-node-router-service.yaml | kubectl delete -f -

kubectl delete -f 34-k8s-create-prod-mesh.yaml
kubectl delete -f 33-k8s-create-test-mesh.yaml

helm uninstall --wait --namespace appmesh-system appmesh-controller

kubectl delete ns appmesh-system

kubectl delete -k "https://github.com/aws/eks-charts/stable/appmesh-controller/crds?ref=master"

kubectl delete -f 32-k8s-x-ray.yaml

kubectl delete -f 31-k8s-fluent-bit.yaml

kubectl delete configmap fluent-bit-cluster-info -n amazon-cloudwatch

for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-nodegroup-petclinic-node-group ^
    --logical-resource-id NodeInstanceRole ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set NodeInstanceRoleName=%i
aws iam detach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/AWSCloudMapFullAccess ^
    --role-name %NodeInstanceRoleName%
aws iam detach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/AWSAppMeshFullAccess ^
    --role-name %NodeInstanceRoleName%
aws iam detach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy ^
    --role-name %NodeInstanceRoleName%

aws eks delete-addon --cluster-name petclinic-cluster --addon-name amazon-cloudwatch-observability
kubectl delete ns amazon-cloudwatch
