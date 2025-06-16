# Etapa 1: construcción usando Maven + JDK
FROM maven:3.9.6-amazoncorretto AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen final con solo el JAR en Amazon Corretto 24 Alpine
FROM amazoncorretto:24-alpine-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
