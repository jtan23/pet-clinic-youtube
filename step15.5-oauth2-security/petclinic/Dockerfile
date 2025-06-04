FROM amazoncorretto:17
ADD target/petclinic-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar" ]