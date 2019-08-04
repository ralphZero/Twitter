package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    ImageView ivProfileThumbnail;
    Button btnCompose;
    EditText etCompose;
    private TwitterClient client;
    TextInputLayout layout;
    ProgressBar progressBar;

    private static int MAX_TWEET_LIMIT = 140;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient(this);
        ivProfileThumbnail = (ImageView) findViewById(R.id.ivProfileThumbnail);
        getMyUserInfo();
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        layout = (TextInputLayout) findViewById(R.id.tiLay);
        layout.setCounterMaxLength(MAX_TWEET_LIMIT);

        etCompose = (EditText) findViewById(R.id.etCompose);
        etCompose.requestFocus();

        btnCompose = (Button) findViewById(R.id.btnCompose);

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = layout.getEditText().getText().toString();
                if(tweetContent.contentEquals("")){
                    Snackbar.make(v,"No tweet",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LIMIT){
                    Snackbar.make(v,"You exceeded the character limit!",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Snackbar.make(v,"tweet :" + tweetContent,Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
                client.composeTweet(tweetContent,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Tweet tweet = Tweet.fromJson(response);
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(ComposeActivity.this,TimelineActivity.class);
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("RepTweet","failed "+responseString);
                        if (!responseString.contentEquals("")){
                            Toast.makeText(getApplicationContext(),responseString,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("RepTweet","failed "+errorResponse.toString());
                        if(errorResponse.has("errors")){
                            try {
                                JSONObject jsonObject = errorResponse.getJSONObject("errors");
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
    private void getMyUserInfo(){
        client.getMyUserInfo(TimelineActivity.myUsername,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("Reponse",response.toString());
                try {
                    String profileImg = response.getString("profile_image_url");
                    Glide.with(getApplicationContext())
                            .load(profileImg)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                            .into(ivProfileThumbnail);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
