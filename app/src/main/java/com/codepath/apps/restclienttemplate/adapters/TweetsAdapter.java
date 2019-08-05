package com.codepath.apps.restclienttemplate.adapters;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.codepath.apps.restclienttemplate.DatabaseTweet;
import com.codepath.apps.restclienttemplate.DetailedView;
import com.codepath.apps.restclienttemplate.ImageViewer;
import com.codepath.apps.restclienttemplate.PatternEditableBuilder;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimeFormatter;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Tweet> list;
    private TwitterClient client;
    static int REQUEST_CODE = 99;

    public TweetsAdapter(Context context, List<Tweet> list) {
        this.context = context;
        this.list = list;
        client = new TwitterClient(context);
    }

    //inflate the layout
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (i){
            case 1:
                View v1 = inflater.inflate(R.layout.item_tweet_image,viewGroup,false);
                viewHolder = new ViewHolderWithImage(v1);
                break;
            case 0:
                View v2 = inflater.inflate(R.layout.item_tweet,viewGroup,false);
                viewHolder = new ViewHolderNormal(v2);
                break;
        }
        return viewHolder;

    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getEntities().getType() != null){
            if(list.get(position).getEntities().getType().contentEquals("photo")){
                return 1;
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 1:
                ViewHolderWithImage holder1 = (ViewHolderWithImage) holder;
                configViewHolderWithImage(holder1,position);
                break;
            case 0:
                ViewHolderNormal holder2 = (ViewHolderNormal) holder;
                configViewHolder(holder2,position);
                break;
        }
    }

    private void configViewHolderWithImage(final ViewHolderWithImage holder, final int position) {
        final Tweet tweet = list.get(position);
        holder.tvName.setText(tweet.getUser().getName());
        holder.tvUsername.setText("@"+tweet.getUser().getUsername());
        holder.tvCreateAt.setText(" • "+TimeFormatter.getTimeDifference(tweet.getCreateAt()));
        holder.tvLike.setText(tweet.getFavorite_count());
        holder.tvRetweet.setText(tweet.getRetweet_count());

        if (tweet.getRetweeted()){
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            holder.tvRetweet.setTextColor(Color.parseColor("#ff17bf63"));
        }else{
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            holder.tvRetweet.setTextColor(Color.BLACK);
        }

        if(tweet.getFavorited()){
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart);
            holder.tvLike.setTextColor(Color.parseColor("#ffe0245e"));
        }else{
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            holder.tvLike.setTextColor(Color.BLACK);
        }

        if(tweet.getUser().isVerified()){
            holder.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.correct,0);
            holder.tvName.setCompoundDrawablePadding(3);
        }
        holder.tvBody.setText(tweet.getBody());

        Glide.with(context)
                .load(tweet.getUser().getImgPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                .into(holder.ivProfileImg);

        Glide.with(context)
                .load(tweet.getEntities().getMedia_url())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(15)))
                .into(holder.ivTweetMedia);
        //Log.d("mylist",tweet.getEntities().getMedia_url());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedView.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                intent.putExtra("position",position);
                ((TimelineActivity) context).startActivityForResult(intent,REQUEST_CODE);
            }
        });

        holder.ivTweetMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra("tweet",Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tweet.getFavorited()){
                    //clicked to unlike
                    client.dislikeThistweet(tweet.gettId(),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            holder.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            holder.tvLike.setTextColor(Color.parseColor("#ffe0245e"));
                            try {
                                Tweet tweet2 = Tweet.fromJson(response);
                                list.set(position,tweet2);
                                notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }else {
                    //clicked to like
                    client.likeThisTweet(tweet.gettId(),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //Log.d("Liked",response.toString());
                            try {
                                holder.ibLike.setImageResource(R.drawable.ic_vector_heart);
                                Tweet tweet1 = Tweet.fromJson(response);
                                list.set(position,tweet1);
                                notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }
            }
        });

        holder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.getRetweeted()) {
                    //click to unretweet
                    client.unretweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("retweet",response.toString());
                            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }else{
                    //click to retweet
                    client.retweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("retweet",response.toString());
                            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.e("retweetErr",errorResponse.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }
            }
        });

        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyToPost(tweet,v);
            }
        });
    }

    private void configViewHolder(final ViewHolderNormal holder, final int position) {
        final Tweet tweet = list.get(position);
        holder.tvUsername.setText("@"+tweet.getUser().getUsername());
        holder.tvName.setText(tweet.getUser().getName());
        holder.tvLike.setText(tweet.getFavorite_count());
        holder.tvRetweet.setText(tweet.getRetweet_count());

        if (tweet.getRetweeted()){
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            holder.tvRetweet.setTextColor(Color.parseColor("#ff17bf63"));
        }else{
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            holder.tvRetweet.setTextColor(Color.BLACK);
        }

        if(tweet.getFavorited()){
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart);
            holder.tvLike.setTextColor(Color.parseColor("#ffe0245e"));
        }else{
            holder.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            holder.tvLike.setTextColor(Color.BLACK);
        }

        if(tweet.getUser().isVerified()){
            holder.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.correct,0);
            holder.tvName.setCompoundDrawablePadding(3);
        }
        holder.tvCreateAt.setText(" • "+TimeFormatter.getTimeDifference(tweet.getCreateAt()));
        holder.tvBody.setText(tweet.getBody());
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.parseColor("#5EBBB2"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                //Toast.makeText(MainActivity.this, "Clicked username: " + text, Toast.LENGTH_SHORT).show();
                            }
                        }).into(holder.tvBody);

        Glide.with(context)
                .load(tweet.getUser().getImgPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                .into(holder.ivProfileImg);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedView.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                intent.putExtra("position",position);
                ((TimelineActivity) context).startActivityForResult(intent,REQUEST_CODE);
            }
        });
        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(tweet.getFavorited()){
                //clicked to unlike
                client.dislikeThistweet(tweet.gettId(),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        holder.ibLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                        holder.tvLike.setTextColor(Color.parseColor("#ffe0245e"));
                        try {
                            Tweet tweet2 = Tweet.fromJson(response);
                            list.set(position,tweet2);
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }else {
                //clicked to like
                client.likeThisTweet(tweet.gettId(),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //Log.d("Liked",response.toString());
                        try {
                            holder.ibLike.setImageResource(R.drawable.ic_vector_heart);
                            Tweet tweet1 = Tweet.fromJson(response);
                            list.set(position,tweet1);
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
            }
        });

        holder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (tweet.getRetweeted()) {
                //click to unretweet
                client.unretweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("retweet",response.toString());
                        holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }else{
                //click to retweet
                client.retweetThisPost(String.valueOf(tweet.gettId()),new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("retweet",response.toString());
                        holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e("retweetErr",errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }
            }
        });

        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyToPost(tweet,v);
            }
        });
    }

    private void getMyUserImg(final ImageView imageView){
        client.getMyUserInfo(TimelineActivity.myUsername,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("Reponse",response.toString());
                try {
                    String profileImg = response.getString("profile_image_url");
                    Glide.with(context)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.reply_dialog,null);

        //definition des vues
        //---------------------
        ImageView imgProfile = view.findViewById(R.id.ivProfileReply);
        //set the clicked user img
        getMyUserImg(imgProfile);
        //set user's
        TextView screename = view.findViewById(R.id.tvReplyTo);
        screename.setText("@"+tweet.getUser().getUsername());

        final TextInputLayout layout = view.findViewById(R.id.tiLay2);
        layout.setCounterMaxLength(140);
        EditText etReplyText = view.findViewById(R.id.etComposeReply);
        etReplyText.setText("@"+tweet.getUser().getUsername());
        Button btnReply = view.findViewById(R.id.btnReply);
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String replyContent = layout.getEditText().getText().toString();
                if(replyContent.contentEquals("")){
                    Toast.makeText(context,"Please compose a reply to send.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(replyContent.length() > 140){
                    Toast.makeText(context,"You exceeded the character limit!",Toast.LENGTH_SHORT).show();
                    return;
                }
                client.replyTweet(tweet.gettId(),replyContent,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("ReplyResponse",response.toString());
                        try {
                            Tweet tweet1 = Tweet.fromJson(response);
                            list.add(0,tweet1);
                            notifyItemInserted(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ReplyErr",errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("ReplyErr",responseString);
                    }
                });
            }
        });
        //implement closing
        ImageButton ibClose = view.findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ProgressBar progressBar = view.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAllTweets(List<Tweet> tweetList) {
        list.addAll(tweetList);
        notifyDataSetChanged();
    }

    public void saveTweetsToDatabase(){
        //saving all list tweet to database
        //this method must be called asynchronously
        new DatabaseAsyncTask().execute();
    }

    public void addToList(Tweet tweet) {
        list.add(tweet);
        notifyDataSetChanged();
    }

    //define viewholder
    public class ViewHolderNormal extends RecyclerView.ViewHolder{

        public TextView tvBody;
        public TextView tvCreateAt;
        public TextView tvName;
        public TextView tvUsername;
        public TextView tvLike;
        public TextView tvRetweet;
        public ImageView ivProfileImg;
        public ImageButton ibLike;
        public ImageButton ibRetweet;
        public ImageButton ibReply;
        public RelativeLayout container;

        public ViewHolderNormal(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAt);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImg);
            tvName = itemView.findViewById(R.id.tvScreenName);
            tvUsername = itemView.findViewById(R.id.tvScreenUsername);
            container = itemView.findViewById(R.id.container);
            tvLike = itemView.findViewById(R.id.tvLikeCount);
            tvRetweet = itemView.findViewById(R.id.tvRetweetCount);
            ibLike = itemView.findViewById(R.id.ib_like);
            ibRetweet = itemView.findViewById(R.id.ib_retweet);
            ibReply = itemView.findViewById(R.id.ib_reply);
        }
    }

    public class ViewHolderWithImage extends RecyclerView.ViewHolder{

        public TextView tvBody;
        public TextView tvCreateAt;
        public TextView tvName;
        public TextView tvLike;
        public TextView tvRetweet;
        public TextView tvReply;
        public TextView tvUsername;
        public ImageView ivProfileImg;
        public ImageView ivTweetMedia;

        public ImageButton ibReply;
        public ImageButton ibRetweet;
        public ImageButton ibLike;

        public RelativeLayout container;


        public ViewHolderWithImage(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBodyIm);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAtIm);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImgIm);
            tvName = itemView.findViewById(R.id.tvNameIm);
            tvLike = itemView.findViewById(R.id.tvLikeCountIm);
            tvRetweet = itemView.findViewById(R.id.tvRetweetCountIm);
            tvReply = itemView.findViewById(R.id.tvReplyCountIm);
            tvUsername = itemView.findViewById(R.id.tvUsernameIm);
            ivTweetMedia = itemView.findViewById(R.id.imgViewer);
            container = itemView.findViewById(R.id.cnt_img);
            ibReply = itemView.findViewById(R.id.ib_reply_im);
            ibLike = itemView.findViewById(R.id.ib_like_im);
            ibRetweet = itemView.findViewById(R.id.ib_retweet_im);
        }
    }

    public class DatabaseAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseTweet databaseTweet = new DatabaseTweet(context);
            databaseTweet.RemoveEverything();
            //later before saving we probably should remove everthing from the table then insert the new data
            for (int i=0;i < list.size();i++){
                long idTweet = list.get(i).gettId();
                String body = list.get(i).getBody();
                String createAt = list.get(i).getCreateAt();
                int favorityCount = Integer.valueOf(list.get(i).getFavorite_count());
                int retweetCount = Integer.valueOf(list.get(i).getRetweet_count());
                String mediaType = list.get(i).getEntities().getType();
                String mediaUrl = list.get(i).getEntities().getMedia_url();
                String source = list.get(i).getSource();
                long idUser = list.get(i).getUser().getuId();
                String name = list.get(i).getUser().getName();
                String username = list.get(i).getUser().getUsername();
                String imagePath = list.get(i).getUser().getImgPath();
                Boolean verified = list.get(i).getUser().isVerified();
                Boolean favorited = list.get(i).getFavorited();
                Boolean retweeted = list.get(i).getRetweeted();
                databaseTweet.SaveTweetData(idTweet,body,createAt,favorityCount,retweetCount,favorited,retweeted,mediaType,mediaUrl,source,idUser,name,username,imagePath,verified);
            }
            return null;
        }
    }
}
