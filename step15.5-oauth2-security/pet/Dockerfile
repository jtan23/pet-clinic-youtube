FROM amazoncorretto:17
ADD target/pet-0.0.1-SNAPSHOT.jar pet.jar
ENTRYPOINT [ "sh", "-c", "java -jar /pet.jar" ]