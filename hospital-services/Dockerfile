# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]