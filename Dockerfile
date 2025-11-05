FROM openjdk:26-trixie
WORKDIR /app
COPY target/Payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT [ "java", "-jar", "app.jar" ]