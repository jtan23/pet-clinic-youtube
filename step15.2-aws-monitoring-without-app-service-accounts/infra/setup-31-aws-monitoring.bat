REM Install Amazon CloudWatch Observability EKS add-on
sed "s/NAME_SPACE/test/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 16-k8s-service-deployment.yaml | kubectl delete -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 16-k8s-service-deployment.yaml | kubectl delete -f -

REM Create Amazon CloudWatch Agent addon
aws eks create-addon --cluster-name petclinic-cluster --addon-name amazon-cloudwatch-observability

REM (Optional) Check CloudWatch has been installed
kubectl get amazoncloudwatchagent -A
kubectl get all -n amazon-cloudwatch

REM Attach CloudWatchAgentServerPolicy, AWSCloudMapFullAccess, AWSAppMeshFullAccess to NodeInstanceRole
for /f %i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-nodegroup-petclinic-node-group ^
    --logical-resource-id NodeInstanceRole ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set NodeInstanceRoleName=%i
aws iam attach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy ^
    --role-name %NodeInstanceRoleName%
aws iam attach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/AWSCloudMapFullAccess ^
    --role-name %NodeInstanceRoleName%
aws iam attach-role-policy ^
    --policy-arn arn:aws:iam::aws:policy/AWSAppMeshFullAccess ^
    --role-name %NodeInstanceRoleName%

REM Create ConfigMap for Fluent Bit
kubectl create configmap fluent-bit-cluster-info ^
    --from-literal=cluster.name=petclinic-cluster ^
    --from-literal=http.server=on ^
    --from-literal=http.port=2020 ^
    --from-literal=read.head=on ^
    --from-literal=read.tail=on ^
    --from-literal=logs.region=ap-southeast-2 ^
    -n amazon-cloudwatch

REM (Optional) Check ConfigMap 
kubectl describe configmap -n amazon-cloudwatch fluent-bit-cluster-info

REM Create Fluent Bit DaemonSet
kubectl apply -f 31-k8s-fluent-bit.yaml
kubectl delete -f 31-k8s-fluent-bit.yaml
kubectl apply -f 31-k8s-fluent-bit.yaml

REM Install X-Ray DaemonSet
kubectl apply -f 32-k8s-x-ray.yaml

REM (Optional) Add eks to helm repo, if not added alreay 
helm repo add eks https://aws.github.io/eks-charts

REM Create Custom Resource Definitions for appmesh-controller
kubectl apply -k "https://github.com/aws/eks-charts/stable/appmesh-controller/crds?ref=master"

REM Create appmesh-system namespace
kubectl create ns appmesh-system

REM Install appmesh-controller
helm upgrade -i --wait appmesh-controller eks/appmesh-controller ^
    --namespace appmesh-system ^
    --set clusterName=petclinic-cluster ^
    --set tracing.enabled=true ^
    --set tracing.provider=x-ray

REM (Optional) Checking newly created appmesh-system namespace
kubectl get all -n appmesh-system

REM Create test-mesh and prod-mesh
kubectl apply -f 33-k8s-create-test-mesh.yaml
kubectl apply -f 34-k8s-create-prod-mesh.yaml

REM We need to label the namespace so that k8s can find our appmesh 
kubectl label namespace test mesh=test-mesh
kubectl label namespace test "appmesh.k8s.aws/sidecarInjectorWebhook"=enabled
kubectl label namespace prod mesh=prod-mesh
kubectl label namespace prod "appmesh.k8s.aws/sidecarInjectorWebhook"=enabled

REM (Optional) Check the newly created appmeshes
aws appmesh list-meshes
kubectl describe namespace test

REM Deploy our services onto appmesh
sed "s/NAME_SPACE/test/g; s/APP_NAME/app/g;   s/TARGET_PORT/8080/g" 36-k8s-virtual-node-router-service.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/owner/g; s/TARGET_PORT/8081/g" 36-k8s-virtual-node-router-service.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/pet/g;   s/TARGET_PORT/8082/g" 36-k8s-virtual-node-router-service.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/vet/g;   s/TARGET_PORT/8084/g" 36-k8s-virtual-node-router-service.yaml | kubectl apply -f -
sed "s/NAME_SPACE/test/g; s/APP_NAME/visit/g; s/TARGET_PORT/8083/g" 36-k8s-virtual-node-router-service.yaml | kubectl apply -f -

REM (Optional) Check started services
REM Make sure every pod have 3 Containers running: our service, envoy and xray-daemon 

