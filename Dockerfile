FROM eclipse-temurin:17 AS build

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-jammy

COPY --from=build /app/build/libs/nubim-0.0.1-SNAPSHOT.jar ./app.jar

COPY ./docker-entrypoint.sh .

EXPOSE 8080

CMD ["/bin/sh", "docker-entrypoint.sh"]