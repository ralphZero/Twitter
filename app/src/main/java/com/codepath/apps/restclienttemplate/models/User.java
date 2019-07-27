package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private long uId;
    private String name;
    private String username;
    private String imgPath;

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

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.username = jsonObject.getString("screen_name");
        user.uId = jsonObject.getLong("id");
        user.imgPath = jsonObject.getString("profile_image_url");
        return user;
    }
}
