package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Entities;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView rvTweet;
    List<Tweet> list;
    TweetsAdapter adapter;
    ProgressBar progressBar;
    FloatingActionButton fab;
    ImageView ivLogo;
    public static String myUsername = null;
    private static int REQUESTCODE = 10;
    SwipeRefreshLayout refreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    private long lowestId;
    private TwitterClient client;
    DatabaseTweet database = new DatabaseTweet(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        View imgLogo = getLayoutInflater().inflate(R.layout.custom_toolbar,null);
        toolbar.addView(imgLogo,new Toolbar.LayoutParams(Gravity.START));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ivLogo = findViewById(R.id.ivLogo);

        client = TwitterApplication.getRestClient(this);
        getMyUsernameFromTwitter();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        rvTweet = (RecyclerView) findViewById(R.id.rvTweet);
        rvTweet.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        adapter = new TweetsAdapter(this,list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweet.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(lowestId);
            }
        };
        rvTweet.setOnScrollListener(scrollListener);
        rvTweet.setAdapter(adapter);

        populateHomeTimeline();
        //getDataFromDatabase();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                populateHomeTimeline();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this,ComposeActivity.class);
                startActivityForResult(intent,REQUESTCODE);
            }
        });
    }

    private void getMyUsernameFromTwitter() {
        client.getMyUserSettings(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    myUsername = response.getString("screen_name");
                    getMyUserInfo(myUsername);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    private void getDataFromDatabase() {
        progressBar.setVisibility(View.INVISIBLE);
        Cursor cursor = database.GetTweets();
        if(cursor.getCount() != 0){
            List<Tweet> tweetsToAdd = new ArrayList<>();
            while (cursor.moveToNext()){
                Tweet tweet = new Tweet();
                tweet.settId(cursor.getLong(cursor.getColumnIndex("idTweet")));
                tweet.setBody(cursor.getString(cursor.getColumnIndex("body")));
                tweet.setCreateAt(cursor.getString(cursor.getColumnIndex("createAt")));
                tweet.setFavorite_count(cursor.getInt(cursor.getColumnIndex("favoriteCount")));
                tweet.setRetweet_count(cursor.getInt(cursor.getColumnIndex("retweetCount")));
                if (cursor.getInt(cursor.getColumnIndex("favorited"))==1){
                    tweet.setFavorited(true);
                }else{
                    tweet.setFavorited(false);
                }

                if(cursor.getInt(cursor.getColumnIndex("retweeted"))==1){
                    tweet.setRetweeted(true);
                }else {
                    tweet.setRetweeted(false);
                }
                Entities entities = new Entities();
                entities.setType(cursor.getString(cursor.getColumnIndex("mediaType")));
                entities.setMedia_url(cursor.getString(cursor.getColumnIndex("mediaUrl")));
                tweet.setEntities(entities);

                User user = new User();
                user.setuId(cursor.getLong(cursor.getColumnIndex("userId")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                user.setImgPath(cursor.getString(cursor.getColumnIndex("imgPath")));
                if(cursor.getInt(cursor.getColumnIndex("verified"))==1)
                    user.setVerified(true);
                else
                    user.setVerified(false);
                tweet.setUser(user);
                tweetsToAdd.add(tweet);
            }
            //clear existing data
            adapter.clear();
            //show the data we just received
            adapter.addAllTweets(tweetsToAdd);
        }else{
            Log.d("mCursor","Egale a zero");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUESTCODE && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            list.add(0,tweet);
            adapter.notifyItemInserted(0);
            rvTweet.smoothScrollToPosition(0);
        }
    }

    private void getMyUserInfo(String my_username){
        client.getMyUserInfo(my_username ,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("Reponse",response.toString());
                try {
                    String profileImg = response.getString("profile_image_url");
                    Glide.with(TimelineActivity.this)
                            .load(profileImg)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                            .into(ivLogo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    private void loadNextDataFromApi(long page) {
        client.getNextPageOfTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("Scroll",response.toString());
                List<Tweet> tweetList = new ArrayList<>();
                try {
                    lowestId = response.getJSONObject(0).getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i=0;i < response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (jsonObject.getLong("id") < lowestId){
                            lowestId = jsonObject.getLong("id");
                        }
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //add tweet to data source
                        //tweetList.add(tweet);
                        adapter.addToList(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //tweetList.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetState();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        },page);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient",response.toString());
                List<Tweet> tweetsToAdd = new ArrayList<>();
                try {
                    lowestId = response.getJSONObject(0).getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.INVISIBLE);

                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (jsonObject.getLong("id") < lowestId){
                            lowestId = jsonObject.getLong("id");
                        }
                        //Log.d("Arrays",String.valueOf(jsonObject.getInt("retweet_count")));
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //add tweet to data source
                        tweetsToAdd.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //clear existing data
                adapter.clear();
                //show the data we just received
                adapter.addAllTweets(tweetsToAdd);
                //put data to database
                adapter.saveTweetsToDatabase();
                //stop refreshing
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient",responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient",errorResponse.toString());
            }
        });
    }
}
