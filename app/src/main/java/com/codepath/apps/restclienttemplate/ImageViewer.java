package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class ImageViewer extends AppCompatActivity {

    ImageView imageView;
    Tweet tweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.tweetImg);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Glide.with(this)
                .load(tweet.getEntities().getMedia_url())
                .into(imageView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
