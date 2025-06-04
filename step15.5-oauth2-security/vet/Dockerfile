FROM amazoncorretto:17
ADD target/vet-0.0.1-SNAPSHOT.jar vet.jar
ENTRYPOINT [ "sh", "-c", "java -jar /vet.jar" ]
