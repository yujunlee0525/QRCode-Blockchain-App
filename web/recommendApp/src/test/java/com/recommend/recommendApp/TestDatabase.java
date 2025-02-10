package com.recommend.recommendApp;
import com.recommend.recommendApp.Services.Twitter;
import io.vertx.core.Future;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.awaitility.Awaitility.*;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.*;


public class TestDatabase {
//  @Test
//  public void testGetUser() {
//    Twitter twitter = new Twitter(MainVerticle.createMySQLPool("twitter_db"));
//    JSONObject latestTweetObject = new JSONObject();
//    latestTweetObject.put("text", "temporaryText");
//    latestTweetObject.put("created_at", "2021-10-01 00:00:00");
//    Future<String> userResponseString = twitter.getUserInfo("1000118682", latestTweetObject);
//
//    // Create a holder for the result
////    final String[] resultHolder = new String[1];
////    final Throwable[] errorHolder = new Throwable[1];
//
//    userResponseString.onComplete(ar -> {
//      if (ar.succeeded()) {
////        resultHolder[0] = ar.result();
//        System.out.println("User response: " + ar.result());
//      } else {
////        errorHolder[0] = ar.cause();
//        System.out.println("Failed to get user info");
//      }
//    });
//
//    // Wait for up to 5 seconds for the Future to complete
//    await().until(() -> userResponseString.isComplete());
//  }
  @Test
  public void testKeywordMatch(){
    assertEquals(2, Twitter.countPhraseMatches("hahaha", "haha"));
    assertEquals(0, Twitter.countPhraseMatches("haHaha", "haha"));
    assertEquals(1, Twitter.countPhraseMatches("Haha bahaha", "haha"));
  }
  @Test
  public void testHashCount(){
    String hashtagjson = "[\"hug\",\"iran\"]";
    Twitter.countHashtagMatches(hashtagjson, "hug");
  }
}
