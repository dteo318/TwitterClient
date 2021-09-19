package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ComposeDialogFragment.ComposeDialogListener {

    private static final int REQUEST_CODE = 20;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabCompose;

    TimelineFragment timelineFragment = new TimelineFragment();
    SearchFragment searchFragment = new SearchFragment();
    NotificationsFragment notificationsFragment = new NotificationsFragment();
    MessagesFragment messagesFragment = new MessagesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabCompose = findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeTweetDialog();
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.itemTimeline);
    }

    private void showComposeTweetDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance("Tweet");
        composeDialogFragment.show(fragmentManager, "fragment_compose_tweet");
    }

    @Override
    public void onFinishComposeTweet(Tweet publishedTweet) {
        timelineFragment.addPublishedTweetToTimeline(publishedTweet);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemTimeline:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, timelineFragment).commit();
                return true;
            case R.id.itemSearch:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, searchFragment).commit();
                return true;
            case R.id.itemNotifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, notificationsFragment).commit();
                return true;
            case R.id.itemMessages:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, messagesFragment).commit();
                return true;
        }
        return false;
    }
}