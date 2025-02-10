FROM maven:3.9.6-amazoncorretto-21-al2023
EXPOSE 8888
WORKDIR /app
ADD . /app

ENV AWS_ACCOUNT_ID="390402562809"
ENV TEAM_NAME="TheCloudCrew"
ENV DB_HOST=test-rds-cluster.cluster-ro-c7wi2eakcpxp.us-east-1.rds.amazonaws.com
ENV DB_PORT=3306
ENV DB_NAME=twitter_db
ENV DB_USER=admin
ENV DB_PASSWORD=theCloudCrew580

RUN mvn clean package -Dmaven.test.skip=true
ENTRYPOINT ["java", "-jar", "/app/target/recommendApp-1.0.0-SNAPSHOT-fat.jar"]
