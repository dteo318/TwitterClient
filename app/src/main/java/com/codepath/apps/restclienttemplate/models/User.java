package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String profileImageUrl;

    public User() {}

    public User(String name, String screenName, String profileImageUrl, long id) {
        this.name = name;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
        this.id = id;
    }

    public static List<User> fromJsonTweetsArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return "@" + screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        long id = jsonObject.getLong("id");
        String name = jsonObject.getString("name");
        String screenName = jsonObject.getString("screen_name");
        String profileImageUrl = jsonObject.getString("profile_image_url_https");

        return new User(name, screenName, profileImageUrl, id);
    }
}
