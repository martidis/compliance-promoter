FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=target/compliance-bar.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=test", "-jar","app.jar"]
