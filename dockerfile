FROM openjdk:11-jdk-slim AS build

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .

RUN mvn -B -f pom.xml dependency:resolve

COPY src ./src

COPY WebContent ./WebContent

RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk11-openjdk-slim


#COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war


EXPOSE 8082

CMD ["catalina.sh", "run"]