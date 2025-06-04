aws codecommit create-repository --repository-name app

for /f %%i in (' ^
aws codecommit get-repository ^
    --repository-name app ^
    --query repositoryMetadata.cloneUrlHttp ^
    --output text ^
') do set REPO=%%i

git init
git remote add origin %REPO%
git branch -m master main
git add .
git commit -m "Initial commit"
git push --set-upstream origin main
