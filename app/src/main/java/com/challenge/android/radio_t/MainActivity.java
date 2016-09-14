package com.challenge.android.radio_t;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.network.RssFetcher;
import com.challenge.android.radio_t.parser.RssParser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RssFetcher fetcher = new RssFetcher();
        fetcher.fetch("http://feeds.rucast.net/radio-t", new RssFetcher.OnRssFetchedListener() {
            @Override
            public void onRssFetched(@Nullable String rssData) {
                Log.d(TAG, "onRssFetched() called with: " + "rssData = [" + rssData + "]");
                RssParser parser = new RssParser();
                if (rssData != null) parser.parse(rssData, new RssParser.OnRssParsedListener() {
                    @Override
                    public void onRssParsed(@NonNull Channel channel) {
                        Log.d(TAG, "onRssParsed() called with: " + "channel = [" + channel + "]");
                    }

                    @Override
                    public void onFailed() {
                        Log.d(TAG, "onFailed() called with: " + "");
                    }
                });
            }

            @Override
            public void onFailed(@Nullable String reason) {
                Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            }
        });
    }
}
