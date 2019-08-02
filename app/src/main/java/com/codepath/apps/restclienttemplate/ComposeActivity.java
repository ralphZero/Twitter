package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    ImageView ivProfileThumbnail;
    Button btnCompose;
    TextInputEditText etCompose;
    private TwitterClient client;
    TextInputLayout layout;

    private static int MAX_TWEET_LIMIT = 280;
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

        layout = (TextInputLayout) findViewById(R.id.tiLay);
        layout.setCounterMaxLength(MAX_TWEET_LIMIT);

        etCompose = (TextInputEditText) findViewById(R.id.etCompose);
        btnCompose = (Button) findViewById(R.id.btnCompose);

        final String tweetContent = etCompose.getEditableText().toString();

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tweetContent.isEmpty()){
                    Snackbar.make(v,"No tweet",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LIMIT){
                    Snackbar.make(v,"You exceeded the character limit!",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(),tweetContent,Toast.LENGTH_SHORT).show();

                //Snackbar.make(v,"tweet :" + tweetContent,Snackbar.LENGTH_LONG).show();

                /*client.composeTweet(tweetContent,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("RepTweet","Success" +response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("RepTweet","failed "+responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("RepTweet","failed "+errorResponse.toString());
                    }
                });*/
            }
        });
    }
    private void getMyUserInfo(){
        client.getMyUserInfo(new JsonHttpResponseHandler(){
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
