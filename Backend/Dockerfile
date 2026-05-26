# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Копіюємо файли збирача Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Робимо скрипт виконуваним та кешуємо залежності
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x bootJar -x test || true

# Копіюємо вихідний код та збираємо фінальний JAR
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Створюємо безпечного користувача
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копіюємо зібраний jar (назва має точно збігатися з результатом білду в build/libs)
COPY --from=builder /app/build/libs/abitandstudhelp-0.0.1-SNAPSHOT.jar app.jar

# Налаштування порту та профілю за замовчуванням
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseSerialGC -Xss512k -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

# Запускаємо з урахуванням оптимізації пам'яті Java
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]