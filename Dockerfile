# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder

# Set working directory inside the container
WORKDIR /app

# Copy project files into the container
COPY . .

# Build the application using Maven (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the generated JAR file from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application's default port
EXPOSE 8080

# Default environment profile (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod

# Command to run the application
ENTRYPOINT ["java", "XX:+UseG1GC -Xms256m -Xmx512m", "-jar", "app.jar"]
