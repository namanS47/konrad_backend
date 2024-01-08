FROM openjdk:17-jdk-slim

#For Generating jar file inside container
#FROM maven as maven_build
#
#ARG konrad-folder-path=konrad-backend
#WORKDIR "/konrad-backend"
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#COPY src src
##RUN ./mvnw dependency:resolve
#
#RUN mvn clean package
#
#CMD ["java", "-jar", "target/konrad-docker.jar"]


#Generate jar file here and copy inside container
COPY target/konrad-docker.jar /konrad-docker.jar
CMD ["java", "-jar", "/konrad-docker.jar"]