package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.DetailedView;
import com.codepath.apps.restclienttemplate.ImageViewer;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Tweet> list;

    public TweetsAdapter(Context context, List<Tweet> list) {
        this.context = context;
        this.list = list;
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

    private void configViewHolderWithImage(ViewHolderWithImage holder, int position) {
        final Tweet tweet = list.get(position);
        holder.tvName.setText(tweet.getUser().getName());
        holder.tvUsername.setText(tweet.getUser().getUsername());
        holder.tvCreateAt.setText(tweet.getCreateAt());
        holder.tvLike.setText(tweet.getFavorite_count());
        holder.tvRetweet.setText(tweet.getRetweet_count());

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
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                .into(holder.ivTweetMedia);
        //Log.d("mylist",tweet.getEntities().getMedia_url());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedView.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(intent);
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
    }

    private void configViewHolder(ViewHolderNormal holder, int position) {
        final Tweet tweet = list.get(position);
        holder.tvUsername.setText(tweet.getUser().getUsername());
        holder.tvName.setText(tweet.getUser().getName());
        holder.tvLike.setText(tweet.getFavorite_count());
        holder.tvRetweet.setText(tweet.getRetweet_count());
        if(tweet.getUser().isVerified()){
            holder.tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.correct,0);
            holder.tvName.setCompoundDrawablePadding(3);
        }
        holder.tvCreateAt.setText(tweet.getCreateAt());
        holder.tvBody.setText(tweet.getBody());

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
                context.startActivity(intent);
            }
        });
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
        private RelativeLayout container;

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
        }
    }

    public class ViewHolderWithImage extends RecyclerView.ViewHolder{

        public TextView tvBody;
        public TextView tvCreateAt;
        public TextView tvName;
        public TextView tvLike;
        public TextView tvRetweet;
        public TextView tvUsername;
        public ImageView ivProfileImg;
        public ImageView ivTweetMedia;
        public RelativeLayout container;


        public ViewHolderWithImage(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBodyIm);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAtIm);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImgIm);
            tvName = itemView.findViewById(R.id.tvNameIm);
            tvLike = itemView.findViewById(R.id.tvLikeCountIm);
            tvRetweet = itemView.findViewById(R.id.tvRetweetCountIm);
            tvUsername = itemView.findViewById(R.id.tvUsernameIm);
            ivTweetMedia = itemView.findViewById(R.id.imgViewer);
            container = itemView.findViewById(R.id.cnt_img);
        }
    }
}
