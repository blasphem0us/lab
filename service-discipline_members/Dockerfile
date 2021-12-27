FROM openjdk:17-alpine

WORKDIR /opt/server
COPY ./target/service-discipline_members-0.0.1-SNAPSHOT.jar server.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "server.jar"]