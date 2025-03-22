FROM --platform=linux/amd64 eclipse-temurin:17-jdk-jammy
EXPOSE 8080
ARG JAR_FILE=template-api-0.0.1-SNAPSHOT.jar
COPY ../BackEnd/${JAR_FILE} app.jar
COPY ../BackEnd/config/application.yml ./config/
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=file:/app/config/"]