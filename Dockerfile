FROM maven:3.9.3-eclipse-temurin-17-alpine AS builder
LABEL authors="oku6er"
WORKDIR build
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN mvn install -DskipTests

FROM eclipse-temurin:17-jre-alpine AS layers
LABEL authors="oku6er"
WORKDIR layer
COPY --from=builder /build/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app

ARG DATABASE_HOST
ARG DATABASE_TABLE
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG DATABASE_FLYWAY_PASSWORD
ENV DATABASE_HOST=$DATABASE_HOST
ENV DATABASE_TABLE=$DATABASE_TABLE
ENV DATABASE_USERNAME=$DATABASE_USERNAME
ENV DATABASE_PASSWORD=$DATABASE_PASSWORD
ENV DATABASE_FLYWAY_PASSWORD=$DATABASE_FLYWAY_PASSWORD

RUN addgroup --system appuser && adduser -S -s /usr/sbin/nologin -G appuser appuser
COPY --from=layers /layer/dependencies/ ./
COPY --from=layers /layer/spring-boot-loader/ ./
COPY --from=layers /layer/snapshot-dependencies/ ./
COPY --from=layers /layer/application/ ./
RUN chown -R appuser:appuser /opt/app
USER appuser
HEALTHCHECK --interval=30s --retries=5 CMD wget -qO- http://localhost:8080/actuator/health/ | grep UP || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher", "-- DATABASE_HOST=$DATABASE_HOST", "--DATABASE_TABLE=$DATABASE_TABLE", "--DATABASE_USERNAME=$DATABASE_USERNAME", "--DATABASE_PASSWORD=$DATABASE_PASSWORD", "--DATABASE_FLYWAY_PASSWORD=$DATABASE_FLYWAY_PASSWORD"]