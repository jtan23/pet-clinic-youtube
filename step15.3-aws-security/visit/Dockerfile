FROM amazoncorretto:17
ADD target/visit-0.0.1-SNAPSHOT.jar visit.jar

ARG DB_ENDPOINT
ENV DB_URL=jdbc:mysql://$DB_ENDPOINT:3306/petclinic
ENV USER_NAME=petclinic
ENV USER_PWD=petclinic

ENTRYPOINT [ "sh", "-c", "java -jar /visit.jar" ]