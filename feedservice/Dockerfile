FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src ./src

RUN chmod +x ./mvnw
RUN ./mvnw -B -DskipTests package


FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar

# copy wait script and ensure it's executable
COPY wait-for-db.sh /wait-for-db.sh
RUN chmod +x /wait-for-db.sh

# install netcat (busybox) for the wait script to work
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
ENTRYPOINT ["/bin/sh","-c","/wait-for-db.sh db 3306 && exec java -jar /app/app.jar"]

