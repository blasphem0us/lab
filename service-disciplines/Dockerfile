FROM openjdk:17-alpine

WORKDIR /opt/server
COPY ./target/service-disciplines-0.0.1-SNAPSHOT.jar server.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "server.jar"]