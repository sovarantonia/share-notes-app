# Use OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy your built JAR file
COPY target/shares-notes-app-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (default Spring Boot port)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]