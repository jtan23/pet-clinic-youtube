REM Make sure Docker is running locally, and aws sso login has been done

call mvn clean package

REM Set MYSQL_ENDPOINT
for /f %%i in (' ^
aws ssm get-parameters ^
    --names pet-clinic-mysql-endpoint ^
    --query Parameters[0].Value ^
    --output text ^
') do set MYSQL_ENDPOINT=%%i

REM Build image
docker build -t visit:latest --build-arg DB_ENDPOINT=%MYSQL_ENDPOINT% .

REM (Optional) Create ECR repository if not already existed
aws ecr create-repository --repository-name visit

REM Tag the image for upload
docker tag visit:latest 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com/visit:latest

REM Login to AWS ECR
aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com

REM Upload image to AWS ECR
docker push 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com/visit:latest
