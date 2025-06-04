FROM amazoncorretto:17
ADD target/oauth2server-0.0.1-SNAPSHOT.jar oauth2server.jar
ENTRYPOINT [ "sh", "-c", "java -jar /oauth2server.jar" ]