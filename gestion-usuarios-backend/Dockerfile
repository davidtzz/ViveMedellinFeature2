# Etapa 1: Build con Maven + Java 24 (Amazon Corretto)
FROM amazoncorretto:24 as build

# Instalamos Maven manualmente
RUN yum update -y && \
    yum install -y maven && \
    yum clean all

WORKDIR /app

# Copiamos el código fuente y pom.xml
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (sin tests)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final para ejecución
FROM amazoncorretto:24

WORKDIR /app

# Copiamos el .jar desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto que usará la app
EXPOSE 8080

# Render usa la variable ${PORT}, así que la respetamos
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
