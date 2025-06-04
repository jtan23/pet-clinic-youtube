REM Make sure aws sso login has been done

call mvn clean package
aws s3 cp target\dbinit-1.0.0.jar s3://jtan23-petclinic
