## 빌드 단계
#FROM ubuntu:20.04 AS builder
#
## 필수 패키지 설치 및 OpenJDK 23 설치
#RUN apt-get update && \
#    apt-get install -y wget gnupg lsb-release && \
#    wget https://download.oracle.com/java/23/latest/jdk-23_linux-x64_bin.deb && \
#    dpkg -i jdk-23_linux-x64_bin.deb && \
#    apt-get install -f && \
#    apt-get clean
#
## Gradle 설치
#RUN apt-get update && \
#    apt-get install -y gradle && \
#    apt-get clean
#
#WORKDIR /app
# 1단계: 빌드 환경 (Gradle 캐시 활용)
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /app

# Gradle 설정 파일 복사 (캐시 활용을 위해 먼저 복사)
COPY build.gradle settings.gradle ./

# Gradle Wrapper 복사 (캐시 활용을 위해 먼저 복사)
COPY gradle gradle

# 의존성 다운로드 (캐시 활용)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY . .

# Gradle 빌드
RUN ./gradlew clean build -x test

# 실행 단계
FROM openjdk:23-jdk

ENV TZ=Asia/Seoul

WORKDIR /app

# 빌드한 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 38587

ENTRYPOINT ["java", "-jar", "app.jar"]
