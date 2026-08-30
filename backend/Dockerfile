FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy the backend into /app
COPY . .

RUN chmod +x mvnw
# Build the JAR inside Docker
RUN ./mvnw clean package -DskipTests

# Runtime image

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk upgrade --no-cache

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
