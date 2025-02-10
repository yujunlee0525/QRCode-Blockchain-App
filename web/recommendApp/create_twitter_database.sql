-- MySQL version: 5.7.11+
-- Usage:
-- mysql --user=$DB_USER -h $DB_HOST --port $DB_PORT --password=$DB_PASSWORD --local-infile=1 < create_twitter_database.sql


-- create the database
-- drop database if exists twitter_db;
-- create database twitter_db;
use twitter_db;

-- create the checkins table
-- drop table if exists `userInfo`;
-- create table `userInfo` (
--                           `usr_id` varchar(22) not null,
--                           `screen_name` LONGTEXT default null,
--                           `description` LONGTEXT default null,
--                           primary key (`usr_id`)
-- );
--
-- -- load data to the checkins table
-- load data local infile './miniRes/userInfo.csv/part-00000-bc84189a-c844-4972-8053-a13fd0086086-c000.csv'
--      into table `userInfo`
--      CHARACTER SET utf8mb4
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr_id, screen_name, description);
-- load data local infile './miniRes/userInfo.csv/part-00001-bc84189a-c844-4972-8053-a13fd0086086-c000.csv'
--      into table `userInfo`
--      CHARACTER SET utf8mb4
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--     (usr_id, screen_name, description);
-- load data local infile './miniRes/userInfo.csv/part-00002-bc84189a-c844-4972-8053-a13fd0086086-c000.csv'
--      into table `userInfo`
--      CHARACTER SET utf8mb4
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--     (usr_id, screen_name, description);
-- load data local infile './miniRes/userInfo.csv/part-00003-bc84189a-c844-4972-8053-a13fd0086086-c000.csv'
--      into table `userInfo`
--      CHARACTER SET utf8mb4
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--     (usr_id, screen_name, description);
-- load data local infile './miniRes/userInfo.csv/part-00004-bc84189a-c844-4972-8053-a13fd0086086-c000.csv'
--      into table `userInfo`
--      CHARACTER SET utf8mb4
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--     (usr_id, screen_name, description);


-- create the businesses table
-- drop table if exists `interactHashtagScore`;
-- create table `interactHashtagScore` (
-- 	`usr1` varchar(22) not null,
-- 	`usr2` varchar(22) not null,
--   `hashtag_score` DOUBLE DEFAULT 0.0 NOT NULL,
--   `interaction_score` DOUBLE DEFAULT 0.0 NOT NULL,
--   PRIMARY KEY (`usr1`, `usr2`),
--   FOREIGN KEY (`usr1`) REFERENCES `userInfo`(`usr_id`),
--   FOREIGN KEY (`usr2`) REFERENCES `userInfo`(`usr_id`)
-- );
--
-- -- load data to the businesses table
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00000-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00001-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00002-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00003-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00004-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00005-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00006-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00007-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00008-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00009-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00010-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
-- LOAD DATA LOCAL INFILE './miniRes/interactionHashtagScore.csv/part-00011-ec7604a0-36af-4053-86df-3e49cf98c438-c000.csv'
--     INTO TABLE `interactHashtagScore`
--      FIELDS TERMINATED BY ','
--      ENCLOSED BY '"'
--      ESCAPED BY '\\'
--      LINES TERMINATED BY '\r'
--      IGNORE 1 LINES
--      (usr1, usr2, hashtag_score, interaction_score);
     -- create the users table

drop table if exists `contactTweets`;
CREATE TABLE `contactTweets` (
   `sender_id` VARCHAR(22) NOT NULL,
   `interacted_user_id` VARCHAR(22) NOT NULL,
   `type` VARCHAR(10) DEFAULT NULL,
   `tweet_id` BIGINT NOT NULL,
   `created_at` VARCHAR(30) DEFAULT NULL,
   `text` TEXT DEFAULT NULL,
   `hashtags` TEXT DEFAULT NULL,
   PRIMARY KEY (`sender_id`, `tweet_id`),
   FOREIGN KEY (`sender_id`) REFERENCES `userInfo`(`usr_id`),
   FOREIGN KEY (`interacted_user_id`) REFERENCES `userInfo`(`usr_id`)
);

-- load data to the contactTweets table
load data local infile './miniRes/contact_tweets.csv/part-00000-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00001-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00002-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00003-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00004-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00005-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00006-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00007-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00008-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00009-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00010-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00011-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00012-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00013-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00014-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00015-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);
load data local infile './miniRes/contact_tweets.csv/part-00016-9f1e7238-122e-49cb-913f-d2b8b6ffa5d9-c000.csv'
     into table `contactTweets`
     CHARACTER SET utf8mb4
     FIELDS TERMINATED BY ','
     ENCLOSED BY '"'
     ESCAPED BY '\\'
     LINES TERMINATED BY '\r'
     IGNORE 1 LINES
     (sender_id, interacted_user_id, type, tweet_id, created_at, text, hashtags);

