# Stage 1: Build
FROM openjdk:24 as builder

WORKDIR /build

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:24

VOLUME /tmp

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]