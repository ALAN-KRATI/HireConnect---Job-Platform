FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline -DskipTests || true
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["sh", "-c", "sleep 20 && java -jar app.jar"]
