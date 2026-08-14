# Stage 1: Build the application using Maven and Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application jar using JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/glowgraph-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
