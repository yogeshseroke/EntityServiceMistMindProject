FROM gcr.io/distroless/java:11
EXPOSE 8080
ADD target/entities.jar entities.jar
ENTRYPOINT ["java","-jar","/entities.jar"]