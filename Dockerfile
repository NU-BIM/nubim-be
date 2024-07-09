FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN ./gradlew clean build

RUN cp ./build/libs/nubim-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD ["/bin/sh", "docker-entrypoint.sh"]