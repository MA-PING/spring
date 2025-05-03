# 빌드 단계
#FROM ubuntu:20.04 AS builder

## 필수 패키지 설치 및 OpenJDK 23 설치
#RUN apt-get update && \
#    apt-get install -y wget gnupg lsb-release && \
#    wget https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.deb && \
#    dpkg -i jdk-24_linux-x64_bin.deb && \
#    apt-get install -f && \
#    apt-get clean
#
## Gradle 설치
#RUN apt-get update && \
#    apt-get install -y gradle && \
#    apt-get clean
#
#WORKDIR /app
#
## 소스 코드 복사
#COPY . .
#
## Gradle 빌드
#RUN ./gradlew clean build -x test
#
## 실행 단계
#FROM openjdk:24-jdk
#
#ENV TZ=Asia/Seoul
#
#WORKDIR /app
#
## 빌드한 JAR 파일 복사
#COPY --from=builder /app/build/libs/*.jar app.jar
#
#EXPOSE 38587
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
# Stage 1: Builder - Gradle 실행 환경 (JDK 21 사용)
# Gradle 8.12.1 실행과 호환되는 JDK 21 베이스 이미지 사용
FROM eclipse-temurin:21-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 전체 파일 복사 (gradlew, 소스코드 등)
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# Gradle 빌드 실행
# 이 단계는 베이스 이미지의 JDK 21 환경에서 실행됩니다.
# 주의: build.gradle 파일에 아래와 같이 Java 23을 타겟으로 하는
#       Gradle Toolchain 설정이 반드시 필요합니다!
#       java {
#           toolchain {
#               languageVersion = JavaLanguageVersion.of(23)
#           }
#       }
RUN ./gradlew clean build -x test

# Stage 2: Runtime - 애플리케이션 실행 환경 (JDK 23 사용)
# 최종 애플리케이션을 실행할 JDK 23 베이스 이미지 사용
FROM eclipse-temurin:23-jdk
# 또는 FROM openjdk:23-jdk 사용 가능

# 타임존 설정
ENV TZ=Asia/Seoul

# 작업 디렉토리 설정
WORKDIR /app

# 빌더 스테이지에서 생성된 JAR 파일만 복사
# JAR 파일 이름 패턴(*.jar)이 정확한지 확인하세요.
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출 (사용하는 포트로 변경)
EXPOSE 38587

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]