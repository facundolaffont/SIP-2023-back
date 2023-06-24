FROM eclipse-temurin:19-jre-alpine
COPY build/libs/helloworld-1.0.0.jar /app/app.jar
WORKDIR /app/
EXPOSE 6060/tcp
ENTRYPOINT ["/bin/sh", "-c", "java -jar /app/app.jar"]