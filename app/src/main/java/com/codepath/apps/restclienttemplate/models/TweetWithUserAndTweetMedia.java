package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUserAndTweetMedia {
    @Embedded
    Tweet tweet;

    @Relation(entity = User.class, parentColumn = "userId",entityColumn = "id")
    User user;

    @Relation(entity = TweetMedia.class, parentColumn = "mediaId",entityColumn = "id")
    TweetMedia media;

    public static List<Tweet> getTweets(List<TweetWithUserAndTweetMedia> tweetsWithUserAndTweetMedia) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < tweetsWithUserAndTweetMedia.size(); i++) {
            Tweet tweet =  tweetsWithUserAndTweetMedia.get(i).tweet;
            tweet.user = tweetsWithUserAndTweetMedia.get(i).user;
            tweet.media = tweetsWithUserAndTweetMedia.get(i).media;
            tweets.add(tweet);
        }
        return tweets;
    }
}
