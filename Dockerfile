FROM openjdk:8-jdk-alpine as builder
WORKDIR /home/romain/eip/tmp/builder
COPY ./gradle ./gradle
COPY ./gradlew ./
RUN ./gradlew
COPY ./ ./
RUN ./gradlew build

FROM openjdk:8-jre-alpine as launcher
ENV DOCKER 1
WORKDIR /home/romain/eip/tmp/launcher
COPY --from=builder /home/romain/eip/tmp/builder/build/libs/*.jar ./template.jar
CMD ["java", "-jar", "template.jar"]