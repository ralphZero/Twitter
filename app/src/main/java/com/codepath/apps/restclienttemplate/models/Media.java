package com.codepath.apps.restclienttemplate.models;

import org.parceler.Parcel;

@Parcel
public class Media {
    private long tId;
    private String type;
    private String media_url;

    public Media(long tId, String type, String media_url) {
        this.tId = tId;
        this.type = type;
        this.media_url = media_url;
    }

    public Media(){

    }

    public long gettId() {
        return tId;
    }

    public String getType() {
        return type;
    }

    public String getMedia_url() {
        return media_url;
    }
}
