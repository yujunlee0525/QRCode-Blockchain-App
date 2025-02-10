FROM maven:3.9.6-amazoncorretto-21-al2023
EXPOSE 8888
WORKDIR /app
ADD . /app

ENV PRIVATE_KEY="343710770439"
ENV PUBLIC_KEY="1097844002039"
ENV AWS_ACCOUNT_ID="390402562809"
ENV TEAM_NAME="TheCloudCrew"

RUN mvn clean package
ENTRYPOINT ["java", "-jar", "/app/target/qrCodeApp-1.0.0-SNAPSHOT-fat.jar"]
