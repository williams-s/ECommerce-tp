FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/ms-membership-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]