FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=target/mycmdb.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=test", "-jar","app.jar"]
