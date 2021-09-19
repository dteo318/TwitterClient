package com.codepath.apps.restclienttemplate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {
    public static final String TAG = "ComposeDialogFragment";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;
    TwitterClient client;

    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    public interface ComposeDialogListener {
        void onFinishComposeTweet(Tweet publishedTweet);
    }

    public ComposeDialogFragment() {}

    public static ComposeDialogFragment newInstance(String title) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);

        client = TwitterApp.getRestClient(getActivity());

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        // Setting saved tweet draft
        String draftTweet = preferences.getString("draftTweet", "");
        etCompose.setText(draftTweet);
        Log.i(TAG, "onViewCreated: Set draft tweet");

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Responding to button click
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(getActivity(), "Tweet is empty", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getActivity(), "Tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                client.postTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess: Successfully published tweet");
                        try {
                            Tweet publishedTweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "onSuccess: Parsing published tweet JSON " + publishedTweet.getBody() );
                            // Passing published tweet back to MainActivity so that it can be added to timeline
                            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                            listener.onFinishComposeTweet(publishedTweet);
                            etCompose.getText().clear();
                            dismiss();
                        } catch (JSONException e) {
                            Log.i(TAG, "Failed parsing published tweet JSON", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(TAG, "onFailure: Failed publishing tweet ", throwable);
                    }
                });
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        String tweetContent = etCompose.getText().toString();
        if (!tweetContent.isEmpty()) {
            // Checking if user wants to save unfinished tweet
            // Setting click results of alert
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            editor.putString("draftTweet", tweetContent);
                            editor.commit();
                            Log.i(TAG, "onClick: Saved tweet to drafts");
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            editor.putString("draftTweet", "");
                            editor.commit();
                            Log.i(TAG, "onClick: Did not save tweet to drafts");
                            break;
                    }
                }
            };

            // Alert to choose if user wants to save unfinished tweet
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Would you like to save your tweet?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }
}
