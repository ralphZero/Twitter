package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Parcel
public class Entities {
    //private long tId;
    private String media_url;
    private String type;

    public String getMedia_url() {
        return media_url;
    }

    public String getType() {
        return type;
    }

    public Entities() {
    }

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        if(jsonObject.has("media")){
            entities.type = jsonObject.getJSONArray("media").getJSONObject(0).getString("type");
            entities.media_url = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url");
        }else {
            entities.media_url = "";
            entities.type = "";
            //entities.tId = 0;
        }
        return entities;
    }
}
