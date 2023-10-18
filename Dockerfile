FROM eclipse-temurin:17-jdk-alpine AS builder
LABEL authors="oku6er"
WORKDIR build
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw spring-boot:run -DskipTests

#FROM eclipse-temurin:17-jre-alpine AS layers
#LABEL authors="oku6er"
#WORKDIR layer
#COPY --from=builder /build/target/*.jar app.jar
#RUN java -Djarmode=layertools -jar app.jar extract
#
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /opt/app
#RUN addgroup --system appuser && adduser -S -s /usr/sbin/nologin -G appuser appuser
#COPY --from=layers /layer/dependencies/ ./
#COPY --from=layers /layer/spring-boot-loader/ ./
#COPY --from=layers /layer/snapshot-dependencies/ ./
#COPY --from=layers /layer/application/ ./
#RUN chown -R appuser:appuser /opt/app
#USER appuser
#HEALTHCHECK --interval=30s --retries=5 CMD wget -qO- http://localhost:8080/actuator/health/ | grep UP || exit 1
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]