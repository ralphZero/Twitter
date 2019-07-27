package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
    private String body;
    private String createAt;
    private long tId;
    private User user;

    public String getBody() {
        return body;
    }

    public String getCreateAt() {
        return createAt;
    }

    public long gettId() {
        return tId;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.tId = jsonObject.getLong("id");
        tweet.createAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }
}
