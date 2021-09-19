package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        @ForeignKey(entity = TweetMedia.class, parentColumns = "id", childColumns = "mediaId")
})
public class Tweet {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public boolean hasMedia;

    @ColumnInfo
    public long retweetCount;

    @ColumnInfo
    public long likeCount;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    // Mappings to other tables or objects
    @ColumnInfo
    public long mediaId;

    @Ignore
    public TweetMedia media;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    public Tweet() {}

    public Tweet(long id, long retweetCount, long likeCount, String body, String createdAt, User user, boolean hasMedia, TweetMedia media, long userId, long mediaId) {
        this.id = id;
        this.retweetCount = retweetCount;
        this.likeCount = likeCount;
        this.body = body;
        this.createdAt = createdAt;
        this.user = user;
        this.hasMedia = hasMedia;
        this.media = media;
        this.userId = userId;
        this.mediaId = mediaId;
    }

    public long getId() {
        return id;
    }

    public long getMediaId() {
        return mediaId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean hasMedia() {
        return hasMedia;
    }

    public String getRetweetCount() {
        return retweetCount == 0 ? "" : String.format("%d", retweetCount);
    }

    public String getDetailsRetweetCount() {
        return retweetCount == 0 ? "" : String.format("%d Retweets", retweetCount);
    }

    public String getDetailsLikeCount() {
        return likeCount == 0 ? "" : String.format("%d Likes", likeCount);
    }

    public String getLikeCount() {
        return likeCount == 0 ? "" : String.format("%d", likeCount);
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public String getDetailsCreatedAt() { return TimeFormatter.getTimeStamp(createdAt); }

    public User getUser() {
        return user;
    }

    public TweetMedia getMedia() {
        return media;
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        long id = jsonObject.getLong("id");
        long retweetCount = jsonObject.getLong("retweet_count");
        long likeCount = jsonObject.getLong("favorite_count");
        String body = jsonObject.getString("text");
        String createdAt = jsonObject.getString("created_at");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        long userId = user.getId();

        boolean hasMedia;
        TweetMedia media;
        long mediaId = id;
        try {
            try {
                media = TweetMedia.fromJson(jsonObject.getJSONObject("extended_entities"), mediaId);
            } catch (JSONException e) {
                media = TweetMedia.fromJson(jsonObject.getJSONObject("entities"), mediaId);
            }
            hasMedia = true;
            mediaId = media.getId();
        } catch (JSONException e) {
            Log.i("Tweet", "fromJson: No media found", e);
            hasMedia = false;
            media = new TweetMedia("No media url", "No media type", mediaId);
        }


        return new Tweet(id, retweetCount, likeCount, body, createdAt, user, hasMedia, media, userId, mediaId);
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
