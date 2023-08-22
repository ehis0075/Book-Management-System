#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package


#
# Package stage
#
FROM openjdk:11-jdk-alpine

WORKDIR /app

COPY --from=build /home/app/target/ book-mgt-service.jar


ENTRYPOINT ["java", "-jar","book-mgt-service.jar"]
