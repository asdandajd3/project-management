# Sử dụng image có Maven + JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy toàn bộ project
COPY . .

# Tải dependencies
RUN mvn dependency:go-offline -B

# Build project
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file jar từ build stage
COPY --from=build /app/target/*.jar app.jar

# Mở port
EXPOSE 8082

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
