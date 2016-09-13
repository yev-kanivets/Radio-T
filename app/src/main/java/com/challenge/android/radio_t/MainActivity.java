package com.challenge.android.radio_t;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.challenge.android.radio_t.network.RssFetcher;

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
            }

            @Override
            public void onFailed(@Nullable String reason) {
                Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            }
        });
    }
}
