FROM maven:3-eclipse-temurin-22-alpine AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app

RUN mvn clean package

FROM openjdk:22 AS final

COPY --from=build /app/target/gestao-estacionamentos.jar /target/app.jar

WORKDIR /target

EXPOSE 3003

CMD ["java", "-jar", "app.jar"]