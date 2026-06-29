FROM maven:3.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
