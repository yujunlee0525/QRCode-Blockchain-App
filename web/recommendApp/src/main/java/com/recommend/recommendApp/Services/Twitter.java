package com.recommend.recommendApp.Services;

import com.recommend.recommendApp.Models.ContactTweetRes;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.*;

public class Twitter {
  // https://vertx.io/docs/vertx-mysql-client/java/
  Pool client;

  public Twitter() {
    this.client = createMySQLPool("twitter_db");
  }

  /**
   * Get interaction and hashtag scores for the given user
   *
   * @param userId the user ID
   * @return a map of user IDs to interaction and hashtag scores
   */
  public Future<Map<String, JSONObject>> getInteractionAndHashtagScores(String userId) {
    Promise<Map<String, JSONObject>> promise = Promise.promise();
    String sql = "SELECT usr1, usr2, interaction_score, hashtag_score FROM interactHashtagScore WHERE usr1 = ? OR usr2 = ?";
    client.preparedQuery(sql)
      .execute(Tuple.of(userId, userId), ar -> {
        if (ar.succeeded()) {
          RowSet<Row> rows = ar.result();
          Map<String, JSONObject> scores = new HashMap<>();
          for (Row row : rows) {
            String usr1 = row.getString("usr1");
            String usr2 = row.getString("usr2");
            double interactionScore = row.getDouble("interaction_score");
            double hashtagScore = row.getDouble("hashtag_score");

            String otherUserId = usr1.equals(userId) ? usr2 : usr1;
            JSONObject score = new JSONObject();
            score.put("interaction_score", interactionScore);
            score.put("hashtag_score", hashtagScore);
            scores.put(otherUserId, score);
          }
          promise.complete(scores);
        } else {
          promise.fail(ar.cause());
        }
      });
    return promise.future();
  }

  /**
   * Get contact tweets for the given user order by created_at in descending order
   * and break the tie by tweet_id
   *
   * @param userId the user ID
   * @param interactionType the interaction type
   * @return a list of contact tweets
   */
  public Future<List<Row>> getContactTweets(String userId, String interactionType) {
    Promise<List<Row>> promise = Promise.promise();
    String sql;
    Tuple params;
    if ("both".equalsIgnoreCase(interactionType)) {
// SELECT * FROM contactTweets WHERE sender_id = '2915153885' UNION SELECT * FROM contactTweets WHERE interacted_user_id = '2915153885'
      sql = "SELECT * FROM contactTweets WHERE sender_id = ? UNION SELECT * FROM contactTweets WHERE interacted_user_id = ?";
      params = Tuple.of(userId, userId);
    } else {
      sql = "SELECT * FROM contactTweets WHERE (sender_id = ? OR interacted_user_id = ?) AND type = ?";
      params = Tuple.of(userId, userId, interactionType.toLowerCase());
    }
    client.preparedQuery(sql)
      .execute(params, ar -> {
        if (ar.succeeded()) {
          RowSet<Row> rows = ar.result();
          List<Row> tweets = new ArrayList<>();
          for (Row row : rows) {
            tweets.add(row);
          }
          promise.complete(tweets);
        } else {
          promise.fail(ar.cause());
        }
      });
    return promise.future();
  }
  /**
   * Get latest contact tweets for the given user
   */
  public Future<List<Row>> getLatestContactTweets(String userId, String interactionType) {
    Promise<List<Row>> promise = Promise.promise();
    String sql;
    Tuple params;
    if ("both".equalsIgnoreCase(interactionType)) {
      // order by created_at in descending order and break the tie by tweet_id
      sql = "SELECT * FROM contactTweets WHERE sender_id = ? OR interacted_user_id = ? ORDER BY created_at, tweet_id DESC";
      params = Tuple.of(userId, userId);
    } else {
      sql = "SELECT * FROM contactTweets WHERE (sender_id = ? OR interacted_user_id = ?) AND type = ? ORDER BY created_at, tweet_id DESC";
      params = Tuple.of(userId, userId, interactionType.toLowerCase());
    }
    client.preparedQuery(sql)
      .execute(params, ar -> {
        if (ar.succeeded()) {
          RowSet<Row> rows = ar.result();
          List<Row> tweets = new ArrayList<>();
          for (Row row : rows) {
            tweets.add(row);
          }
          promise.complete(tweets);
        } else {
          promise.fail(ar.cause());
        }
      });
    return promise.future();
  }
  /**
   * Compute keyword scores for the given user
   * @param userId
   * @param tweets
   * @param phrase
   * @param queryHashtag
   * @return a map of user IDs to keyword scores
   */
  public ContactTweetRes computeKeywordScores(
    String userId,
    List<Row> tweets,
    String phrase,
    String queryHashtag
  ) {
    Map<String, Integer> userMatchCounts = new HashMap<>();
    String queryHashtagLower = queryHashtag.toLowerCase();
    Map<String, JSONObject> userLastTweetDate = new HashMap<>();

    for (Row tweet : tweets) {
      String senderId = tweet.getString("sender_id");
      String interactedUserId = tweet.getString("interacted_user_id");
      String otherUserId = senderId.equals(userId) ? interactedUserId : senderId;
      String text = tweet.getString("text");

      String createdAt = tweet.getString("created_at");

      Long tweetDate = null;
      try {
        Instant instant = Instant.parse(createdAt);
        tweetDate = instant.toEpochMilli(); // Milliseconds since epoch
      } catch (Exception e) { // Catching general Exception to include DateTimeParseException
        // Handle the parse exception as needed
//        System.err.println("Failed to parse date: " + createdAt);
        e.printStackTrace();
        continue; // Skip this tweet if date parsing fails
      }
      if(!userLastTweetDate.containsKey(otherUserId) || tweetDate > userLastTweetDate.get(otherUserId).getLong("date")){
        userLastTweetDate.put(otherUserId, new JSONObject().put("date", tweetDate).put("text", text));
      }

      // Count phrase matches
      int phraseMatches = countPhraseMatches(text, phrase);


      //  hashTags is String
      String hashtagsJson = tweet.getString("hashtags"); // Ensure your column name

      // Count hashtag matches
      int hashtagMatches = countHashtagMatches(hashtagsJson, queryHashtagLower);

      int totalMatches = phraseMatches + hashtagMatches;

      userMatchCounts.put(otherUserId, userMatchCounts.getOrDefault(otherUserId, 0) + totalMatches);
    }

    ContactTweetRes contactTweetRes = new ContactTweetRes(userMatchCounts, userLastTweetDate);

    return contactTweetRes;
  }

  public static int countPhraseMatches(String text, String phrase) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(phrase, index)) != -1) {
      count++;
      index++; // Move forward to allow overlapping matches
    }
    return count;
  }

  public static int countHashtagMatches(String hashtags, String queryHashtagLower) {
    try {
      // Parse the JSON array
      String[] hashtagList = hashtags.split("#");
      // Iterate over the hashtags
      int count = 0;
      for (int i = 0; i < hashtagList.length; i++) {
        String hashtag = hashtagList[i]; // Convert to lowercase as in Scala
        if (queryHashtagLower.equals(hashtag)) {
          count++;
        }
      }
      return count;

    } catch (JSONException e) {
      System.err.println("Failed to parse hashtags JSON: " + hashtags);
      e.printStackTrace();
      return 0;
    }
  }
  public Future<String> getUserInfo(
    String userId,
    JSONObject lastTweet
  ) {
    Promise<String> promise = Promise.promise();
    String sql = "SELECT usr_id, screen_name, description FROM userInfo WHERE usr_id = ?";

    client.preparedQuery(sql).execute(Tuple.of(userId), ar -> {
      if (ar.succeeded()) {
        RowSet<Row> rows = ar.result();
        if (rows.size() == 0) {
          promise.complete(null);
        } else {
          Row row = rows.iterator().next();
          String screenName = row.getString("screen_name");
          String description = row.getString("description");
          String text = lastTweet.has("text") ? lastTweet.getString("text") : "";
          StringBuilder responseBuilder = new StringBuilder()
            .append(userId).append("\t")
            .append(screenName).append("\t")
            .append(description).append("\t")
            .append(text);
          String userInfo = responseBuilder.toString();
          promise.complete(userInfo);
        }
      } else {
        promise.fail(ar.cause());
      }
    });
    return promise.future();
  }
  public static Pool createMySQLPool(String databaseName) {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost(System.getenv("DB_HOST"))
      .setDatabase(databaseName)
      .setUser(System.getenv("DB_USER"))
      .setPassword(System.getenv("DB_PASSWORD"));
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    Pool client = Pool.pool(Vertx.vertx(), connectOptions, poolOptions);
    System.out.println("Database connection pool created");
    // Create and return the MySQLPool object
    return client;
  }
}
