package com.codepath.apps.restclienttemplate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DatabaseTweet extends SQLiteOpenHelper {
    private static String create = "CREATE TABLE IF NOT EXISTS tweet(idTweet INTEGER PRIMARY KEY NOT NULL,body TEXT NOT NULL, createAt TEXT NOT NULL, favoriteCount INTEGER, retweetCount INTEGER, mediaType TEXT NOT NULL, mediaUrl TEXT, userId INTEGER NOT NULL, name TEXT NOT NULL, username TEXT NOT NULL, imgPath TEXT, verified INTEGER NOT NULL)";


    public DatabaseTweet(Context context) {
        super(context, "persistence.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long SaveTweetData(String idTweet,String body,String createAt,int favoriteCount,int retweetCount,String mediaType,String mediaUrl,int userId,String name,String username, String imgPath,Boolean check){
        SQLiteDatabase database = this.getWritableDatabase();
        int verified = check ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put("idTweet",idTweet);
        values.put("body",body);
        values.put("createAt",createAt);
        values.put("favoriteCount",favoriteCount);
        values.put("retweetCount",retweetCount);
        values.put("mediaType",mediaType);
        values.put("mediaUrl",mediaUrl);
        values.put("userId",userId);
        values.put("name",name);
        values.put("username",username);
        values.put("imgPath",imgPath);
        values.put("verified",verified);
        long result = database.insert("tweet", null, values);
        return result;
    }
}
