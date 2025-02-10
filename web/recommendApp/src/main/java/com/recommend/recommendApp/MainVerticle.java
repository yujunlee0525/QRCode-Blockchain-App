package com.recommend.recommendApp;

import com.recommend.recommendApp.Models.ContactTweetRes;
import com.recommend.recommendApp.Services.Twitter;
import io.vertx.core.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class MainVerticle extends AbstractVerticle {

  private static final String TEAM_NAME = System.getenv("TEAM_NAME");
  private static final String TEAM_AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");
  private Twitter twitter = new Twitter();

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.get("/twitter").handler(this::handleTwitterRequest);
    router.get("/").handler(this::handleRootRequest);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8888)
      .onSuccess(server -> {
        System.out.println("HTTP server started on port " + server.actualPort());
        startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void handleTwitterRequest(RoutingContext context) {
    String user_id = context.request().getParam("user_id");
    String phrase = context.request().getParam("phrase");
    String hashtag = context.request().getParam("hashtag");
    String interactionType = context.request().getParam("type");
    String invalidResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID";
    if (user_id == null || interactionType == null || phrase == null || hashtag == null) {
      sendResponse(context, 200, "Missing required parameter");
      return;
    }
    // turn hashtag into lowercase
    hashtag = hashtag.toLowerCase();

    try {
      Future<Map<String, JSONObject>> scoresFuture = twitter.getInteractionAndHashtagScores(user_id);
      Future<List<Row>> tweetsFuture = twitter.getContactTweets(user_id, interactionType);

      String finalHashtag = hashtag;
      Future.all(scoresFuture, tweetsFuture).onComplete(ar -> {
        if (ar.succeeded()) {
          Map<String, JSONObject> scoresMap = scoresFuture.result();
          List<Row> tweets = tweetsFuture.result();

          // Compute keyword scores
          ContactTweetRes contactTweetRes = twitter.computeKeywordScores(user_id, tweets, phrase, finalHashtag);
          Map<String, Integer> userMatchCounts = contactTweetRes.matchCount;
          Map<String, JSONObject> userLastTweet = contactTweetRes.userLastTweet;

          // Compute final scores
          List<Future<String>> userInfoFutures = new ArrayList<>();
          List<JSONObject> finalScores = new ArrayList<>();
          for (Map.Entry<String, Integer> entry : userMatchCounts.entrySet()) {
            String otherUserId = entry.getKey();
            int numberOfMatches = entry.getValue();

            // Compute keyword_score
            double keywordScore = 1 + Math.log(numberOfMatches + 1);

            // Get interaction_score and hashtag_score, set defaults if missing
            JSONObject scores = scoresMap.getOrDefault(otherUserId, new JSONObject()
              .put("interaction_score", 0.0)
              .put("hashtag_score", 1.0));

            double interactionScore = scores.getDouble("interaction_score");
            double hashtagScore = scores.getDouble("hashtag_score");

            // Compute final_score and round to 5 decimal places
            double finalScore = interactionScore * hashtagScore * keywordScore;
            BigDecimal bd = BigDecimal.valueOf(finalScore).setScale(5, RoundingMode.HALF_UP);
            finalScore = bd.doubleValue();

            // Exclude users with final_score = 0
            if (finalScore > 0) {
              // Get user info
              double finalScore1 = finalScore;
              Future<String> userResponseFuture = twitter.getUserInfo(otherUserId, userLastTweet.get(otherUserId))
                .compose(userInfo -> {
                  JSONObject finalScoreObj = new JSONObject()
                    .put("final_score", finalScore1)
                    .put("user_id", otherUserId)
                    .put("user_info", userInfo);
                  finalScores.add(finalScoreObj);
                  return Future.succeededFuture();
                });
              userInfoFutures.add(userResponseFuture);
            }
          }
          if (userInfoFutures.isEmpty()) {
            // No users with final_score > 0
            String validResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID + "\n";
            sendResponse(context, 200, validResponse);
            return;
          }
          // Wait for all getUserInfo futures to complete
          Future.all(userInfoFutures).onComplete(userAr -> {
            if (userAr.succeeded()) {
              // Sort finalScores by final_score in descending order
              finalScores.sort((a, b) -> {
                int cmp = Double.compare(b.getDouble("final_score"), a.getDouble("final_score"));
                if (cmp == 0) {
                  return Long.compare(b.getLong("user_id"), a.getLong("user_id"));
                } else {
                  return cmp;
                }
              });
              StringBuilder responseBuilder = new StringBuilder();
              for (int i = 0; i < finalScores.size(); i++) {
                JSONObject res = finalScores.get(i);
                responseBuilder.append(res.getString("user_info"));
                if (i < finalScores.size() - 1) {
                  responseBuilder.append("\n");
                }
              }
              String validResponse = TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID +
                "\n" + responseBuilder.toString();
              sendResponse(context, 200, validResponse);
            } else {
              // Handle failure in getUserInfo
              System.out.println("Error fetching user info: " + userAr.cause().getMessage());
              sendResponse(context, 200, invalidResponse);
            }
          });

        } else {
          // throw exception
          System.out.println("Error fetching scores and tweets: " + ar.cause().getMessage());
          sendResponse(context, 200, invalidResponse);
        }
      });
    } catch (Exception e) {
//      System.out.println("Error processing request: " + e.getMessage());
      System.out.println(e.getMessage());
      sendResponse(context, 200, invalidResponse);
    }
  }

  private void handleRootRequest(RoutingContext context) {
    sendResponse(context, 200, TEAM_NAME + "," + TEAM_AWS_ACCOUNT_ID);
  }

  private void sendResponse(RoutingContext context, int statusCode, String response) {
    context.response()
      .setStatusCode(statusCode)
      .putHeader("Content-Type", "text/plain; charset=UTF-8")
      .end(response, "UTF-8");
  }

}
