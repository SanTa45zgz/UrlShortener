FROM openjdk:8-jdk-alpine
MAINTAINER rabbit-worker
WORKDIR /app
COPY /build/libs/UrlShortener.jar .
ENTRYPOINT ["java","-jar","UrlShortener.jar", "--spring.profiles.active=worker"]
