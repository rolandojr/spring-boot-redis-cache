#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /home/app
COPY pom.xml .
RUN mvn clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip

#
#
# Package stage
#
FROM eclipse-temurin:11-jdk
LABEL authors="Rolando Ramos"
COPY --from=build /home/app/target/spring-boot-redis-cache-1.0.0-SNAPSHOT.jar app-1.0.0.jar
ENTRYPOINT ["java","-jar","/app-1.0.0.jar"]
