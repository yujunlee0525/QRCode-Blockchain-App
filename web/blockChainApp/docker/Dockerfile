## base image
#FROM maven:3.8.3-openjdk-17
#
#
#ENV PRIVATE_KEY="343710770439"
#ENV PUBLIC_KEY="1097844002039"
#ENV AWS_ACCOUNT_ID="3904-0256-2809"
#ENV TEAM_NAME="TheCloudCrew"
#
#
## Open port 8888
#EXPOSE 8888
#
#ADD . /
#
#RUN mvn clean package
## Define the command which runs when the container starts
#CMD ["mvn clean compile exec:java"]
# Use Bash as the container's entry point.
#ENTRYPOINT ["/bin/bash", "-c"]
FROM maven:3.9.6-amazoncorretto-21-al2023
#FROM maven:3.9.6-amazoncorretto-21-al2023
EXPOSE 8888
WORKDIR /app
ADD . /app

ENV PRIVATE_KEY="343710770439"
#ARG PRIVATE_KEY
ENV PUBLIC_KEY="1097844002039"
ENV AWS_ACCOUNT_ID="390402562809"
ENV TEAM_NAME="TheCloudCrew"

RUN mvn clean package -Dmaven.test.skip=true
ENTRYPOINT ["java", "-jar", "/app/target/blockChainApp-1.0.0-SNAPSHOT-fat.jar"]
