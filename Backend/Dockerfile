# Етап 1: Збірка .jar файлу
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Збираємо проєкт без запуску тестів, щоб пришвидшити процес
RUN mvn clean package -DskipTests

# Етап 2: Легкий образ для запуску
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Оптимізація пам'яті для контейнера
ENV JAVA_OPTS="-XX:+UseSerialGC -Xss512k -XX:MaxRAMPercentage=75.0"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8080}"]