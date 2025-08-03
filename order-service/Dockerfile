FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .
COPY settings.xml /root/.m2/settings.xml
RUN mvn clean package -pl order-service -am -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/order-service/target/order-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]