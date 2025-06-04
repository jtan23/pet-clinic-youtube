REM Make sure Docker is running locally, and aws sso login has been done

call mvn clean package

REM Build image
docker build -t oauth2server:latest .

REM (Optional) Create ECR repository if not already existed
aws ecr create-repository --repository-name oauth2server

REM Tag the image for upload
docker tag oauth2server:latest 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com/oauth2server:latest

REM Login to AWS ECR
aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com

REM Upload image to AWS ECR
docker push 291148202449.dkr.ecr.ap-southeast-2.amazonaws.com/oauth2server:latest
