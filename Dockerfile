FROM eclipse-temurin:17-focal
WORKDIR /customer-service
COPY target/*.jar customer-service-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "customer-service-0.0.1-SNAPSHOT.jar"]
