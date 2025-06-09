FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -pl tracking-service -am -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/tracking-service/target/tracking-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]