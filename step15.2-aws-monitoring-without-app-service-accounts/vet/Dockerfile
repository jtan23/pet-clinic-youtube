FROM amazoncorretto:17
ADD target/vet-0.0.1-SNAPSHOT.jar vet.jar

ARG DB_ENDPOINT
ENV DB_URL=jdbc:mysql://$DB_ENDPOINT:3306/petclinic
ENV USER_NAME=petclinic
ENV USER_PWD=petclinic

ENTRYPOINT [ "sh", "-c", "java -jar /vet.jar" ]