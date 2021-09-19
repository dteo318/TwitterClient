package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetMedia;

import org.parceler.Parcels;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    TextView tvDetailsTweetScreenName;
    TextView tvDetailsTweetName;
    TextView tvDetailsTweetBody;
    TextView tvDetailsTweetCreatedAt;
    TextView tvDetailsTweetRetweetCount;
    TextView tvDetailsTweetLikesCount;
    ImageView ivDetailsTweetProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvDetailsTweetScreenName = findViewById(R.id.tvDetailsTweetScreenName);
        tvDetailsTweetName = findViewById(R.id.tvDetailsTweetName);
        tvDetailsTweetBody = findViewById(R.id.tvDetailsTweetBody);
        tvDetailsTweetCreatedAt = findViewById(R.id.tvDetailsTweetCreatedAt);
        tvDetailsTweetRetweetCount = findViewById(R.id.tvDetailsTweetRetweetCount);
        tvDetailsTweetLikesCount = findViewById(R.id.tvDetailsTweetLikesCount);
        ivDetailsTweetProfilePic = findViewById(R.id.ivDetailsTweetProfilePic);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvDetailsTweetScreenName.setText(tweet.getUser().getScreenName());
        tvDetailsTweetName.setText(tweet.getUser().getName());
        tvDetailsTweetBody.setText(tweet.getBody());
        tvDetailsTweetCreatedAt.setText(tweet.getDetailsCreatedAt());
        tvDetailsTweetRetweetCount.setText(tweet.getDetailsRetweetCount());
        tvDetailsTweetLikesCount.setText(tweet.getDetailsLikeCount());
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivDetailsTweetProfilePic);
        // Loading embedded media
        if (tweet.hasMedia()) {
            loadEmbeddedMedia(tweet.getMedia());
        }
    }

    private void loadEmbeddedMedia(TweetMedia media) {
        LinearLayout containerEmbeddedMedia = findViewById(R.id.containerEmbeddedMedia);
        Log.i("LoadingEmbeddedMedia", "Media type: " + media.getMediaType() + " Media url: " + media.getMediaUrl());
        switch (media.getMediaType()) {
            case "photo":
                ImageView embeddedImage = new ImageView(this);
                embeddedImage.setLayoutParams(new android.view.ViewGroup.LayoutParams(1024,768));
                Glide.with(this).load(media.getMediaUrl()).into(embeddedImage);
                containerEmbeddedMedia.addView(embeddedImage);
                break;
            case "video":
                VideoView embeddedVideo = new VideoView(this);
                MediaController mediaController = new MediaController(this);
                Uri uri = Uri.parse(media.getMediaUrl());

                mediaController.setAnchorView(embeddedVideo);
                embeddedVideo.setMediaController(mediaController);
                embeddedVideo.setVideoURI(uri);
                embeddedVideo.setLayoutParams(new FrameLayout.LayoutParams(1200,682));

                embeddedVideo.requestFocus();
                    embeddedVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            embeddedVideo.start();
                        }
                    });
                containerEmbeddedMedia.addView(embeddedVideo);
                break;
        }
    }
}