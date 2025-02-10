#!/bin/bash

# Define MySQL connection variables
DB_USER="root"
DB_HOST="localhost"
DB_PORT="3306"
DB_PASSWORD="Yjl200189"
DATABASE="twitter_db"

# Folder path containing CSV files
CSV_FOLDER=$1  # Pass the folder path as a script argument

# Function to load a CSV file into MySQL
load_user_info_csv() {
    local file=$1
    mysql --user="$DB_USER" -h "$DB_HOST" --port="$DB_PORT" --password="$DB_PASSWORD" --local-infile=1 -e "
    USE $DATABASE;
    LOAD DATA LOCAL INFILE '$file'
     into table `userInfo`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '"'
     LINES TERMINATED BY '\r\n'
     IGNORE 1 LINES
     (`usr_id`, `screen_name`, `description`);
    "
}
load_interactHashtagScore_csv() {
    local file=$1
    mysql --user="$DB_USER" -h "$DB_HOST" --port="$DB_PORT" --password="$DB_PASSWORD" --local-infile=1 -e "
    USE $DATABASE;
    LOAD DATA LOCAL INFILE '$file'
    INTO TABLE `interactHashtagScore`
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '"'
     LINES TERMINATED BY '\r\n'
     IGNORE 1 LINES
     (`usr1`, `usr2`, `hashtag_score`, `interaction_score`);
    "
}

load_contactTweets_csv() {
    local file="$1"  # Wrap file path in double quotes to handle spaces in paths
    mysql --user="$DB_USER" -h "$DB_HOST" --port="$DB_PORT" --password="$DB_PASSWORD" --local-infile=1 -e "
    USE $DATABASE;
    LOAD DATA LOCAL INFILE '$file'
    INTO TABLE `contactTweets`
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY ','
    ENCLOSED BY '\"'
    ESCAPED BY '\"'
    LINES TERMINATED BY '\r\n'
    IGNORE 1 LINES
    (`sender_id`, `interacted_user_id`, `type`, `tweet_id`, `created_at`, `text`, `hashtags_json`);
    "
}

# Create tables if they do not exist
mysql --user="$DB_USER" -h "$DB_HOST" --port="$DB_PORT" --password="$DB_PASSWORD" --local-infile=1 -e "
DROP DATABASE IF EXISTS $DATABASE;
CREATE DATABASE $DATABASE;
USE $DATABASE;

DROP TABLE IF EXISTS userInfo;
CREATE TABLE userInfo (
    usr_id VARCHAR(22) NOT NULL,
    screen_name LONGTEXT DEFAULT NULL,
    description LONGTEXT DEFAULT NULL,
    PRIMARY KEY (usr_id)
);

DROP TABLE IF EXISTS interactHashtagScore;
CREATE TABLE interactHashtagScore (
    usr1 VARCHAR(22) NOT NULL,
    usr2 VARCHAR(22) NOT NULL,
    hashtag_score DOUBLE DEFAULT 0.0 NOT NULL,
    interaction_score DOUBLE DEFAULT 0.0 NOT NULL,
    PRIMARY KEY (usr1, usr2),
    FOREIGN KEY (usr1) REFERENCES userInfo(usr_id),
    FOREIGN KEY (usr2) REFERENCES userInfo(usr_id)
);

DROP TABLE IF EXISTS contactTweets;
CREATE TABLE contactTweets (
    sender_id VARCHAR(22) NOT NULL,
    interacted_user_id VARCHAR(22) NOT NULL,
    type VARCHAR(10) DEFAULT NULL,
    tweet_id BIGINT NOT NULL,
    created_at VARCHAR(30) DEFAULT NULL,
    text TEXT DEFAULT NULL,
    hashtags_json TEXT DEFAULT NULL,
    PRIMARY KEY (sender_id, tweet_id),
    FOREIGN KEY (sender_id) REFERENCES userInfo(usr_id),
    FOREIGN KEY (interacted_user_id) REFERENCES userInfo(usr_id)
);
"

# Load data from each file in the folder into the appropriate table, only load file start with part-*
#for file in "$CSV_FOLDER/userInfo.csv/"*.csv; do
#    load_csv "userInfo" "$file"
#done
#
#for file in "$CSV_FOLDER/interactionHashtagScore.csv/"*.csv; do
#    load_csv "interactHashtagScore" "$file"
#done

for file in ${CSV_FOLDER}/contact_tweets.csv/*.csv; do
    echo "Loading $file into contactTweets table..."
    load_contactTweets_csv $file
done

echo "All CSV files have been loaded into the MySQL database."
