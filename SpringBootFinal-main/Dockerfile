FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port
EXPOSE 8082

# Run the application
CMD ["java", "-jar", "target/projectmanagement-1.0.0.jar"] 