package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    public static final String TAG = "TweetsAdapter";

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetsList) {
        Log.i(TAG, "addAll: Adding tweetsList of length " + String.format("%d", tweetsList.size()));
        tweets.addAll(tweetsList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTweetName;
        TextView tvScreenName;
        TextView tvTweetBody;
        TextView tvTweetCreatedAt;
        TextView tvTweetRetweetCount;
        TextView tvTweetLikeCount;
        ConstraintLayout containerTweetItem;
        ImageView ivTweetProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTweetName = itemView.findViewById(R.id.tvTweetName);
            tvScreenName = itemView.findViewById(R.id.tvTweetScreenName);
            tvTweetBody = itemView.findViewById(R.id.tvTweetBody);
            ivTweetProfilePic = itemView.findViewById(R.id.ivTweetProfilePic);
            tvTweetCreatedAt = itemView.findViewById(R.id.tvTweetCreatedAt);
            tvTweetRetweetCount = itemView.findViewById(R.id.tvTweetRetweetCount);
            tvTweetLikeCount = itemView.findViewById(R.id.tvTweetLikeCount);
            containerTweetItem = itemView.findViewById(R.id.containerTweetItem);
        }

        public void bind(Tweet tweet) {
            tvTweetName.setText(tweet.getUser().getName());
            tvScreenName.setText(tweet.getUser().getScreenName());
            tvTweetBody.setText(tweet.getBody());
            tvTweetCreatedAt.setText("• " + tweet.getCreatedAt());
            tvTweetRetweetCount.setText(tweet.getRetweetCount());
            tvTweetLikeCount.setText(tweet.getLikeCount());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).circleCrop().into(ivTweetProfilePic);

            containerTweetItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Switching to tweet details activity");
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(intent);
                }
            });
        }
    }
}
