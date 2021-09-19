package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class TweetMedia {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String mediaUrl;

    @ColumnInfo
    public String mediaType;

    public TweetMedia() {}

    public TweetMedia(String mediaUrl, String mediaType, long id) {
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.id = id;
    }

    public static List<TweetMedia> fromJsonTweetsArray(List<Tweet> tweetsFromNetwork) {
        List<TweetMedia> media = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            media.add(tweetsFromNetwork.get(i).media);
        }
        return media;
    }

    public long getId() {
        return id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }


    public static TweetMedia fromJson(JSONObject jsonObject, long parentId) throws JSONException {
        String mediaUrl;
        String mediaType;

        try {
            mediaUrl = jsonObject.getJSONArray("media").getJSONObject(0).getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            mediaUrl = jsonObject.getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }

        try {
            mediaType = jsonObject.getJSONArray("media").getJSONObject(0).getString("type");
        } catch (JSONException e) {
            mediaType = jsonObject.getJSONArray("media").getJSONObject(0).getString("type");
        }

        return new TweetMedia(mediaUrl, mediaType, parentId);
    }
}
