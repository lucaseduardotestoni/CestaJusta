# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY core/pom.xml core/pom.xml
COPY app/pom.xml app/pom.xml
COPY worker/pom.xml worker/pom.xml
RUN mvn -q -B -pl worker -am -DskipTests dependency:go-offline
COPY core/src core/src
COPY worker/src worker/src
RUN mvn -q -B -pl worker -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/worker/target/*.jar worker.jar
ENTRYPOINT ["java", "-jar", "/app/worker.jar"]