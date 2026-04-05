# Spring Boot API (Java 17). Build JAR with Maven, run on JRE.
# If you change <version> in pom.xml, update the COPY line below to match the built JAR name.
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/esprit-market-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p uploads && chown -R spring:spring /app

USER spring
EXPOSE 8081

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
