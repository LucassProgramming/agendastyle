FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

#Copiar el backend dentro de /app
COPY . .

RUN chmod +x mvnw
#Construye el .jar dentor del Docker
RUN ./mvnw clean package -DskipTests

#Segunda imagen para ejecutar java

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]