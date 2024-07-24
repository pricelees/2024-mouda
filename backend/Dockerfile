FROM amazoncorretto:17.0.12

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
