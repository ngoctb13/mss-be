# Stage 1: Build the application
FROM maven:3.8.1-openjdk-17-slim AS build

# Copy source code
COPY src /home/app/src
COPY pom.xml /home/app

COPY src/main/resources/fonts /home/app/src/main/resources/fonts

# Build the application
RUN mvn -f /home/app/pom.xml clean package

# Stage 2: Create the Docker final image
FROM openjdk:17-slim

# Copy the JAR file from the build stage
COPY --from=build /home/app/target/*.jar /usr/local/lib/mss-be.jar

COPY --from=build /home/app/src/main/resources/fonts /usr/local/lib/fonts

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/usr/local/lib/mss-be.jar"]
