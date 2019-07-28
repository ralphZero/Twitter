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
    private ArrayList<Media> media;
    //private String media_url;

    public Entities() {
    }

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        JSONArray array = jsonObject.getJSONArray("media");
        ArrayList<Media> arrayList = new ArrayList<>();
        for (int i=0;i<array.length();i++){
            Media media = new Media(array.getJSONObject(i).getLong("id"),array.getJSONObject(i).getString("type"),array.getJSONObject(i).getString("media_url"));
            arrayList.add(media);
        }
        entities.media = arrayList;
        //entities.media_type = jsonObject.getJSONArray("media").getJSONObject(0).getString("type");
        //entities.media_url = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url");
        return entities;
    }
}
