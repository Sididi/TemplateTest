FROM openjdk:8-jdk-alpine as builder
WORKDIR /tmp/builder
COPY ./gradle ./gradle
COPY ./gradlew ./
RUN ./gradlew
COPY ./ ./
RUN ./gradlew build > logs

FROM openjdk:8-jre-alpine as launcher
WORKDIR /tmp/launcher
COPY --from=builder /tmp/builder/build/libs/fatjar*.jar ./template.jar
CMD ["java", "-jar", "template.jar"]