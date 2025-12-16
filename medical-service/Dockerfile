# -------- BUILD STAGE --------
# Use Maven with Java 17 to build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only pom.xml first (helps Docker cache dependencies)
COPY pom.xml .

# Download all Maven dependencies so they are cached
RUN mvn dependency:go-offline

# Copy the application source code
COPY src ./src

# Build the Spring Boot application and skip tests
RUN mvn clean package -DskipTests


# -------- RUNTIME STAGE --------
# Use a lightweight Java 17 runtime image (no Maven here)
FROM eclipse-temurin:17-jre-jammy

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/medical-service-0.0.1-SNAPSHOT.jar .

# Expose port 8080 so Docker can map it
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/medical-service-0.0.1-SNAPSHOT.jar"]
