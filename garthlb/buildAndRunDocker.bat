@ECHO OFF
rem mvn package 
rem docker build -t garthlb . && docker run --name GarthLB -it -p 9000:9000 -p 9001:9001 -e ENV_VARIABLE_VERSION=1.1.1 --rm garthlb
echo Building Java
mvn package

#echo Building Docker image
#docker build -t garthlb .

echo Executing docker-compose up
docker-compose up --build 
