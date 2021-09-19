package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT * FROM Tweet ORDER BY createdAt DESC LIMIT 300")
    List<TweetWithUserAndTweetMedia> getTweets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweets(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweetMedia(TweetMedia... media);
}
