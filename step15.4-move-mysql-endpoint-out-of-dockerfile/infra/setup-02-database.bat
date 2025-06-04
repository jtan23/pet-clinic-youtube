REM 02 Create MySQL database cluster

REM Set VpcId
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-cluster ^
    --logical-resource-id VPC ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set VpcId=%%i

REM Set PublicSubnet1Id
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-cluster ^
    --logical-resource-id SubnetPublicAPSOUTHEAST2A ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set PublicSubnet1Id=%%i

REM Set PublicSubnet2Id
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-cluster ^
    --logical-resource-id SubnetPublicAPSOUTHEAST2B ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set PublicSubnet2Id=%%i

REM Set PublicSubnet3Id
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-cluster ^
    --logical-resource-id SubnetPublicAPSOUTHEAST2C ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set PublicSubnet3Id=%%i

REM Set PublicRouteTableId
for /f %%i in (' ^
aws cloudformation describe-stack-resource ^
    --stack-name eksctl-petclinic-cluster-cluster ^
    --logical-resource-id PublicRouteTable ^
    --query StackResourceDetail.PhysicalResourceId ^
    --output text ^
') do set PublicRouteTableId=%%i

REM Create MySQL database cluster by calling template
aws cloudformation deploy ^
    --stack-name mysql-database ^
    --template-file 02-cfn-mysql-database.yaml  ^
    --parameter-overrides VpcId=%VpcId% DBSubnet1=%PublicSubnet1Id% DBSubnet2=%PublicSubnet2Id% DBSubnet3=%PublicSubnet3Id% DBRouteTable=%PublicRouteTableId% ^
    --capabilities CAPABILITY_IAM

REM set MYSQL_ENDPOINT
for /f %%i in (' ^
aws ssm get-parameters ^
    --names pet-clinic-mysql-endpoint ^
    --query Parameters[0].Value ^
    --output text ^
') do set MYSQL_ENDPOINT=%%i

REM Check created database cluster
REM Launch an EC2 instance (mysql-check-server), 
REM user your favourite Key pair, or create one (ec2kp),
REM use the same VPC with one of the Public Subnets, and enable auto-assign public IP,
REM and create new security group (mysql-check-sg) with SSH access, 
REM and User Data:
REM     #!/bin/bash
REM     dnf update -y
REM     dnf install mariadb105 -y
REM Once launched, open an SSH session with:
REM     ssh -i "ec2kp.pem" ec2-user@PUBLIC_IP
REM     mysql -h MYSQL_ENDPOINT -P 3306 -u petclinic -p
REM     show databases;
REM     use petclinic;
REM     show tables;
REM     select * from owners;
REM Terminate the above server and newly created Security group
