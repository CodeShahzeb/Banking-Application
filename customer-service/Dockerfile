FROM openjdk:17
EXPOSE 8084
ADD target/customer-service.jar customer-service.jar
ENTRYPOINT ["java","-jar","/customer-service.jar"]