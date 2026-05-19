FROM node:22-alpine AS frontend-build

WORKDIR /app/Frontend
COPY Frontend/package*.json ./
RUN npm ci
COPY Frontend ./

ARG VITE_API_BASE_URL=/
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build

FROM eclipse-temurin:21-jdk AS backend-build

WORKDIR /app
COPY Backend ./Backend
COPY --from=frontend-build /app/Frontend/dist ./Backend/src/main/resources/static

WORKDIR /app/Backend
RUN sh gradlew bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=backend-build /app/Backend/build/libs/*.jar app.jar

ENV PORT=9000
EXPOSE 9000

CMD ["java", "-jar", "app.jar"]
