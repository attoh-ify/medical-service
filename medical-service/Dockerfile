# -------- RUNTIME STAGE --------
# Use a lightweight Java 17 runtime image (no Maven here)
FROM eclipse-temurin:17-jre-jammy

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY /target/*.jar app.jar

# Expose port 8080 so Docker can map it
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]