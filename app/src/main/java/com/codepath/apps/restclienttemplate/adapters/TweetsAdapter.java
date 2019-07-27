package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private Context context;
    private List<Tweet> list;

    public TweetsAdapter(Context context, List<Tweet> list) {
        this.context = context;
        this.list = list;
    }

    //inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_tweet,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //bind
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Tweet tweet = list.get(i);
        viewHolder.tvUsername.setText(tweet.getUser().getUsername());
        viewHolder.tvName.setText(tweet.getUser().getName());
        //viewHolder.tvCreateAt.setText(tweet.getCreateAt());
        viewHolder.tvBody.setText(tweet.getBody());
        Glide.with(context)
                .load(tweet.getUser().getImgPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .apply(new RequestOptions().centerInside().transform(new RoundedCorners(30)))
                .into(viewHolder.ivProfileImg);
        Log.d("img",tweet.getUser().getImgPath());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvBody;
        public TextView tvCreateAt;
        public TextView tvName;
        public TextView tvUsername;
        public ImageView ivProfileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAt);
            ivProfileImg = itemView.findViewById(R.id.ivProfileImg);
            tvName = itemView.findViewById(R.id.tvScreenName);
            tvUsername = itemView.findViewById(R.id.tvScreenUsername);
        }
    }
}
