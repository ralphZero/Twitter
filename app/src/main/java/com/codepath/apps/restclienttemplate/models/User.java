package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    private long uId;
    private String name;
    private String username;
    private String imgPath;
    private Boolean verified;


    public User() {
    }

    public long getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return "@"+username;
    }

    public String getImgPath() {
        return imgPath;
    }

    public Boolean isVerified() {
        return verified;
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.username = jsonObject.getString("screen_name");
        user.uId = jsonObject.getLong("id");
        user.imgPath = jsonObject.getString("profile_image_url");
        user.verified = jsonObject.getBoolean("verified");
        return user;
    }
}
