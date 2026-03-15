# Use Java 21 image as base
FROM eclipse-temurin:21-jdk-jammy as builder

# Set working directory
WORKDIR /build

# Copy Maven project files
COPY pom.xml .
COPY src/ ./src/

# Build the application
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && rm -rf /root/.m2

# Use lightweight JRE image for runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /build/target/restaurant-booking-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
