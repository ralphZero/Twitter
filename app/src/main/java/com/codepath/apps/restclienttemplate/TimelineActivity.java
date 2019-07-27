package com.codepath.apps.restclienttemplate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient(this);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        rvTweet = (RecyclerView) findViewById(R.id.rvTweet);
        list = new ArrayList<>();
        adapter = new TweetsAdapter(this,list);
        rvTweet.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rvTweet.setAdapter(adapter);

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient",response.toString());
                for (int i=0;i < response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        list.add(tweet);
                        adapter.notifyItemInserted(list.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
