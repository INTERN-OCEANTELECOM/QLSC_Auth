FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/qlsc.jar demo.jar
# Chạy ứng dụng khi khởi động container
CMD ["java", "-jar", "demo.jar"]