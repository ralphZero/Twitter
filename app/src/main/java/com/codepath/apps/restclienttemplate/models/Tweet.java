package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {
    private String body;
    private String createAt;
    private String source;
    private long tId;
    private int favorite_count;
    private int retweet_count;
    private Boolean favorited;
    private Boolean retweeted;
    private User user;
    private Entities entities;

    public Tweet() {
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void settId(long tId) {
        this.tId = tId;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public String getBody() {
        return body;
    }

    public String getCreateAt() {
        return createAt;
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
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.source = jsonObject.getString("source");
        tweet.entities = Entities.fromJson(jsonObject.getJSONObject("entities"));
        return tweet;
    }
}
