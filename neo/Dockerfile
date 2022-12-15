FROM openjdk:8-alpine

COPY target/uberjar/neo.jar /neo/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/neo/app.jar"]
