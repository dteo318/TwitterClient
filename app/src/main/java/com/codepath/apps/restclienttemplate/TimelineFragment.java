package com.codepath.apps.restclienttemplate;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetMedia;
import com.codepath.apps.restclienttemplate.models.TweetWithUserAndTweetMedia;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineFragment extends Fragment {

    public static final String TAG = "TimelineFragment";

    TweetDao tweetDao;

    TwitterClient client;
    SwipeRefreshLayout swipeRefreshContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;

    public TimelineFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient(getActivity());
        swipeRefreshContainer = view.findViewById(R.id.swipeRefreshContainer);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: Fetching new timeline data");
                populateHomeTimeline();
            }
        });
        swipeRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweets = view.findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(getActivity(), tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetsAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        tweetDao = ((TwitterApp) getActivity().getApplicationContext()).getMyDatabase().tweetDao();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: Retrieving tweets from DB");
                List<TweetWithUserAndTweetMedia> tweetsWithUserAndTweetMedia = tweetDao.getTweets();
                List<Tweet> tweetsFromDB = TweetWithUserAndTweetMedia.getTweets(tweetsWithUserAndTweetMedia);
                tweetsAdapter.clear();
                tweetsAdapter.addAll(tweetsFromDB);
                Log.i(TAG, "run: Tweets added from DB");
            }
        });
        populateHomeTimeline();
    }

    private void loadMoreData() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: Loading more tweets " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    Log.i(TAG, "onSuccess: " + json.toString());
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    Log.i(TAG, "JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure: Failed getting more tweets", throwable);
            }
        }, tweets.get(tweets.size() - 1).getId());
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(tweetsFromNetwork);
                    swipeRefreshContainer.setRefreshing(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: Inserting tweets to DB");
                            List<User> usersFromNetwork = User.fromJsonTweetsArray(tweetsFromNetwork);
                            List<TweetMedia> mediaFromNetwork = TweetMedia.fromJsonTweetsArray(tweetsFromNetwork);
                            tweetDao.insertUsers(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertTweetMedia(mediaFromNetwork.toArray(new TweetMedia[0]));
                            tweetDao.insertTweets(tweetsFromNetwork.toArray(new Tweet[0]));
                            Log.i(TAG, "run: Tweets inserted to DB");
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure: " + response, throwable);
            }
        });
    }

    public void addPublishedTweetToTimeline(Tweet tweet) {
        tweets.add(0, tweet);
        tweetsAdapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);
    }
}