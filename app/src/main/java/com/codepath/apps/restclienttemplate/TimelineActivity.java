package com.codepath.apps.restclienttemplate;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView rvTweet;
    List<Tweet> list;
    TweetsAdapter adapter;

    SwipeRefreshLayout refreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    private long lowestId;

    private TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient(this);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
    }

    private void loadNextDataFromApi(long page) {
        client.getNextPageOfTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Scroll",response.toString());
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
                Log.d("TwitterClient",response.toString());
                List<Tweet> tweetsToAdd = new ArrayList<>();
                try {
                    lowestId = response.getJSONObject(0).getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i=0; i < response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if (jsonObject.getLong("id") < lowestId){
                            lowestId = jsonObject.getLong("id");
                        }
                        Log.d("Arrays",jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).toString());
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
