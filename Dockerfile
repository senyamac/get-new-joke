
FROM adoptopenjdk:11-jre-hotspot

EXPOSE 8095

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} GetNewJoke-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/GetNewJoke-0.0.1-SNAPSHOT.jar"]