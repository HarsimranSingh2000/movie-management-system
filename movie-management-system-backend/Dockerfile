# Use OpenJDK 17 (or whichever version your app uses)
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

ENV TZ=Asia/Kolkata

# Copy the built jar from target
COPY target/movie-management-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port (must match your app's server.port)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
