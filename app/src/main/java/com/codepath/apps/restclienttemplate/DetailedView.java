package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class DetailedView extends AppCompatActivity {

    Tweet tweet;
    TextView tvName;
    TextView tvUsername;
    TextView tvBody;
    TextView tvTime;
    TextView tvLikeCount;
    TextView tvRetweetCount;
    TextView source;
    ImageView ivProfileImg;
    ImageView ivImg;

    ImageButton ibLike;
    ImageButton ibRetweet;
    ImageButton ibReply;

    Tweet mTweet;

    TwitterClient client;
    Boolean hasLiked;
    int position;

    RelativeLayout rlp;
    private long mId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        position = getIntent().getIntExtra("position",-1);

        mTweet = null;
        hasLiked = false;

        client = new TwitterClient(this);
        tvName = findViewById(R.id.tvDetName);
        tvUsername = findViewById(R.id.tvDetUsername);
        tvBody = findViewById(R.id.tvDetBody);
        ivProfileImg = findViewById(R.id.ivDetProfileImg);
        tvLikeCount = findViewById(R.id.tvLikeCount_view);
        tvRetweetCount = findViewById(R.id.tvRetweetCounter_view);
        ivImg = findViewById(R.id.ivImg);
        tvTime = findViewById(R.id.tvTime_view);
        source = findViewById(R.id.tvSource_view);
        ibLike = findViewById(R.id.ib_like_view);
        ibReply = findViewById(R.id.ib_reply_view);
        ibRetweet = findViewById(R.id.ib_retweet_view);
        rlp = findViewById(R.id.rlp);


        if(tweet.getUser().isVerified()){
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.correct,0);
            tvName.setCompoundDrawablePadding(3);
        }

        if(tweet.getEntities().getMedia_url().contentEquals("")){
            RelativeLayout rll = (RelativeLayout) findViewById(R.id.rlll);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rll.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.tvDetBody);
            rll.setLayoutParams(params);
            ivImg.setVisibility(View.INVISIBLE);
        }else{
            Glide.with(this)
                    .load(tweet.getEntities().getMedia_url())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivImg);
        }

        tvName.setText(tweet.getUser().getName());
        tvUsername.setText(tweet.getUser().getUsername());
        tvBody.setText(tweet.getBody());
        mId = tweet.gettId();

        Glide.with(getApplicationContext())
                .load(tweet.getUser().getImgPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                .into(ivProfileImg);

        tvTime.setText(TimeFormatter.getTimeStamp(tweet.getCreateAt()));
        source.setText(Html.fromHtml(tweet.getSource()));
        source.setTextColor(Color.parseColor("#1DA1F2"));
        //source.setMovementMethod(LinkMovementMethod.getInstance());
        tvLikeCount.setText(tweet.getFavorite_count());
        tvRetweetCount.setText(tweet.getRetweet_count());

        if(tweet.getFavorited()){
            ibLike.setImageResource(R.drawable.ic_vector_heart);
        }else {
            ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        if(tweet.getRetweeted()){
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
        }else{
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyToPost(tweet,v);
            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.getFavorited()){
                    //clicked to unlike
                    client.dislikeThistweet(tweet.gettId(),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            try {
                                hasLiked = true;
                                Tweet tweet2 = Tweet.fromJson(response);
                                mTweet = tweet2;
                                //list.set(position,tweet2);
                                //notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            if(!responseString.isEmpty()){
                                Toast.makeText(DetailedView.this,"Error "+responseString+". Please try again.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (errorResponse!= null){
                                if(errorResponse.has("errors")){
                                    try {
                                        Toast.makeText(DetailedView.this,"Error "+errorResponse.getInt("code")+", "+errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Toast.makeText(DetailedView.this,"Can't connect with server.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    //clicked to like
                    client.likeThisTweet(tweet.gettId(),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //Log.d("Liked",response.toString());
                            try {
                                hasLiked = true;
                                ibLike.setImageResource(R.drawable.ic_vector_heart);
                                Tweet tweet1 = Tweet.fromJson(response);
                                mTweet = tweet1;
                                //list.set(position,tweet1);
                                //notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            if(!responseString.isEmpty()){
                                Toast.makeText(DetailedView.this,"Error "+responseString+". Please try again.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (errorResponse!= null){
                                if(errorResponse.has("errors")){
                                    try {
                                        Toast.makeText(DetailedView.this,"Error "+errorResponse.getInt("code")+", "+errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Toast.makeText(DetailedView.this,"Can't connect with server.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.getRetweeted()) {
                    //click to unretweet
                    client.unretweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("retweet",response.toString());
                            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            try {
                                hasLiked = false;
                                Tweet tweet = Tweet.fromJson(response);
                                mTweet = tweet;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            if(!responseString.isEmpty()){
                                Toast.makeText(DetailedView.this,"Error "+responseString+". Please try again.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (errorResponse!= null){
                                if(errorResponse.has("errors")){
                                    try {
                                        Toast.makeText(DetailedView.this,"Error "+errorResponse.getInt("code")+", "+errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Toast.makeText(DetailedView.this,"Can't connect with server.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    //click to retweet
                    client.retweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //Log.d("retweet",response.toString());
                            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            try {
                                hasLiked = false;
                                mTweet = Tweet.fromJson(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            if(!responseString.isEmpty()){
                                Toast.makeText(DetailedView.this,"Error "+responseString+". Please try again.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if (errorResponse!= null){
                                if(errorResponse.has("errors")){
                                    try {
                                        Toast.makeText(DetailedView.this,"Error "+errorResponse.getInt("code")+", "+errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                Toast.makeText(DetailedView.this,"Can't connect with server.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void onLeavingActivity(){
        if(mTweet == null){
            Intent intent = new Intent(DetailedView.this,TimelineActivity.class);
            setResult(RESULT_CANCELED,intent);
        }else{
            Intent intent = new Intent(DetailedView.this,TimelineActivity.class);
            intent.putExtra("tweet",Parcels.wrap(mTweet));
            intent.putExtra("hasLiked",hasLiked.toString());
            intent.putExtra("position",position);
            setResult(RESULT_OK,intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLeavingActivity();
        //Log.d("Navigate","DoneBack");
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Log.d("Navigate","DoneNav");
        onLeavingActivity();
        finish();
        return true;
    }

    private void getMyUserImg(final ImageView imageView){
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
                            .into(imageView);
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

    private void ReplyToPost(final Tweet tweet, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedView.this );
        final AlertDialog alertDialog = builder.create();

        //LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = getLayoutInflater().inflate(R.layout.reply_dialog_2,null);

        //definition des vues
        //---------------------
        ImageView imgProfile = view.findViewById(R.id.ivProfileReply1);
        //set the clicked user img
        getMyUserImg(imgProfile);
        //set user's
        TextView screename = view.findViewById(R.id.tvReplyTo1);
        screename.setText("@"+tweet.getUser().getUsername());

        final TextInputLayout layout = view.findViewById(R.id.tiLay3);
        layout.setCounterMaxLength(140);
        EditText etReplyText = view.findViewById(R.id.etComposeReply1);
        etReplyText.setText("@"+tweet.getUser().getUsername());
        Button btnReply = view.findViewById(R.id.btnReply1);
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String replyContent = layout.getEditText().getText().toString();
                if(replyContent.contentEquals("")){
                    Toast.makeText(getApplicationContext(),"Please compose a reply to send.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(replyContent.length() > 140){
                    Toast.makeText(getApplicationContext(),"You exceeded the character limit!",Toast.LENGTH_SHORT).show();
                    return;
                }
                client.replyTweet(tweet.gettId(),replyContent,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("ReplyResponse",response.toString());
                        try {
                            hasLiked = false;
                            Tweet tweet1 = Tweet.fromJson(response);
                            mTweet = tweet1;
                            alertDialog.dismiss();
                            //Intent
                            //list.add(0,tweet1);
                            //notifyItemInserted(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        if(!responseString.isEmpty()){
                            Toast.makeText(DetailedView.this,"Error "+responseString+". Please try again.",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse!= null){
                            if(errorResponse.has("errors")){
                                try {
                                    Toast.makeText(DetailedView.this,"Error "+errorResponse.getInt("code")+", "+errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            Toast.makeText(DetailedView.this,"Can't connect with server.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        //implement closing
        ImageButton ibClose = view.findViewById(R.id.ibClose1);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //fin definition

        alertDialog.setView(view);
        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

    }

}