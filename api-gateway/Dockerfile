#FROM maven:3.9-eclipse-temurin-17 AS build
#
#WORKDIR /app
#
#COPY pom.xml ./
#COPY src ./src/
#
#RUN mvn package -DskipTests
#
#FROM openjdk:17-jdk-slim
#
#WORKDIR /app
#
#COPY --from=build /app/target/api-gateway-1.0-SNAPSHOT.jar api-gateway-1.0-SNAPSHOT.jar
#
#EXPOSE 8888
#
#ENTRYPOINT ["java", "-jar", "api-gateway-1.0-SNAPSHOT.jar"]


FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl api-gateway -am -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/api-gateway/target/api-gateway-1.0-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
