FROM amazoncorretto:17
ADD target/owner-0.0.1-SNAPSHOT.jar owner.jar
ENTRYPOINT [ "sh", "-c", "java -jar /owner.jar" ]