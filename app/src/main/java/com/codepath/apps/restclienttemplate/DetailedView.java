package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailedView extends AppCompatActivity {

    Tweet tweet;
    TextView tvName;
    TextView tvUsername;
    TextView tvBody;
    ImageView ivProfileImg;
    ImageView ivImg;
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

        tvName = findViewById(R.id.tvDetName);
        tvUsername = findViewById(R.id.tvDetUsername);
        tvBody = findViewById(R.id.tvDetBody);
        ivProfileImg = findViewById(R.id.ivDetProfileImg);
        ivImg = findViewById(R.id.ivImg);
        rlp = findViewById(R.id.rlp);


        if(tweet.getUser().isVerified()){
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.correct,0);
            tvName.setCompoundDrawablePadding(3);
        }

        if(tweet.getEntities().getMedia_url().contentEquals("")){
            RelativeLayout rll = (RelativeLayout) findViewById(R.id.rll);
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}