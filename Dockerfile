# 빌드 스테이지
FROM gradle:8.5.0-jdk21-alpine AS build
USER root
WORKDIR /auth

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-build-cache

# 실행 스테이지
FROM azul/zulu-openjdk:21-jre

# 실행 시 필요한 환경 변수 설정
ENV JWT_SECRET=${JWT_SECRET}
ENV EUREKA_SERVER_HOSTNAME=${EUREKA_SERVER_HOSTNAME}
ENV EUREKA_SERVER_PORT=${EUREKA_SERVER_PORT}


COPY --from=build /auth/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
VOLUME /tmp