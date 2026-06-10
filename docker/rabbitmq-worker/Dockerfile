# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY app/pom.xml app/pom.xml
COPY rabbitmq-worker/pom.xml rabbitmq-worker/pom.xml
RUN mvn -q -B -pl rabbitmq-worker -am -DskipTests dependency:go-offline
COPY core/src core/src
COPY rabbitmq-worker/src rabbitmq-worker/src
RUN mvn -q -B -pl rabbitmq-worker -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/rabbitmq-worker/target/*.jar rabbitmq-worker.jar
ENTRYPOINT ["java", "-jar", "/app/rabbitmq-worker.jar"]