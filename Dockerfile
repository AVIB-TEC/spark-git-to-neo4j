FROM openjdk:17-jdk-alpine3.13

RUN mkdir /usr/share/man/man1/

# Install maven
RUN apk update
RUN apk add maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

EXPOSE 4567
#CMD ["java", "--version"]
CMD ["java", "-jar", "target/sparkexample-jar-with-dependencies.jar"]