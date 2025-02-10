package com.recommend.recommendApp.Models;

import org.json.JSONObject;

import java.util.Map;

public class ContactTweetRes {
  public Map<String, Integer> matchCount;
  public Map<String, JSONObject> userLastTweet;
  public ContactTweetRes(Map<String, Integer> matchCount, Map<String, JSONObject> userLastTweet) {
    this.matchCount = matchCount;
    this.userLastTweet = userLastTweet;
  }
}
