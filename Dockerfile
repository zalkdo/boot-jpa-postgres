FROM openjdk:8-jdk-alpine

# RUN apk --update add bash curl jq

ENV TZ=Asia/Seoul

# author
MAINTAINER "EMRO"

# volume
VOLUME /tmp

# port
EXPOSE 8080

# version
ARG JAR_VERSION=0.0.1

# args
ARG JAR_FILE=./build/libs/boot-jpa-postgres-${JAR_VERSION}-SNAPSHOT.jar

# docker image 에 jar 추가
ADD ${JAR_FILE} boot-jpa-postgres.jar

# spring profiles active
ARG SPRING_PROFILES_ACTIVE=k3s

RUN echo $SPRING_PROFILES_ACTIVE

ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

# run jar file
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/boot-jpa-postgres.jar" ]
#ENTRYPOINT ["java", "-Dspring.profiles.active=k3s", "-jar", "boot-jpa-postgres.jar"]