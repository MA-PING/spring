# 빌드 단계
FROM ubuntu:20.04 AS builder

# 필수 패키지 설치 및 OpenJDK 23 설치
RUN apt-get update && \
    apt-get install -y wget gnupg lsb-release && \
    wget https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.deb && \
    dpkg -i jdk-24_linux-x64_bin.deb && \
    apt-get install -f && \
    apt-get clean

# Gradle 설치
RUN apt-get update && \
    apt-get install -y gradle && \
    apt-get clean

WORKDIR /app

# 소스 코드 복사
COPY . .

# Gradle 빌드
RUN ./gradlew clean build -x test

# 실행 단계
FROM openjdk:24-jdk

ENV TZ=Asia/Seoul

WORKDIR /app

# 빌드한 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 38587

ENTRYPOINT ["java", "-jar", "app.jar"]
