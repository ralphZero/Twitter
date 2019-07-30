package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {
    private String body;
    private String createAt;
    private long tId;
    private int favorite_count;
    private int retweet_count;
    private User user;
    private Entities entities;

    public Tweet() {
    }

    public String getBody() {
        return body;
    }

    public String getCreateAt() {
        return " • "+TimeFormatter.getTimeDifference(createAt);
    }

    public long gettId() {
        return tId;
    }

    public String getRetweet_count() {
        return String.valueOf(retweet_count);
    }

    public String getFavorite_count() {
        return String.valueOf(favorite_count);
    }

    public User getUser() {
        return user;
    }

    public Entities getEntities() {
        return entities;
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.tId = jsonObject.getLong("id");
        tweet.createAt = jsonObject.getString("created_at");
        tweet.favorite_count = jsonObject.getInt("favorite_count");
        tweet.retweet_count = jsonObject.getInt("retweet_count");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.entities = Entities.fromJson(jsonObject.getJSONObject("entities"));
        return tweet;
    }
}
