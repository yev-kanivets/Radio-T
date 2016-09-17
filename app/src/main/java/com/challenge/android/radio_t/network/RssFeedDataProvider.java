package com.challenge.android.radio_t.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.parser.RssParser;

public class RssFeedDataProvider {
    private static final String TAG = "RssFeedDataProvider";
    private static final String RSS_FEED_URL = "http://feeds.rucast.net/radio-t";

    private boolean fetchingRssFeed;
    @Nullable
    private OnRssFeedDataListener listener;

    public void fetchRssFeedData(@Nullable OnRssFeedDataListener listener) {
        this.listener = listener;
        Log.d(TAG, "fetchRssFeed() called " + fetchingRssFeed);
        if (fetchingRssFeed) return;
        fetchingRssFeed = true;
        RssFetcher fetcher = new RssFetcher();
        fetcher.fetch(RSS_FEED_URL, onRssFetchedListener);
    }

    private RssFetcher.OnRssFetchedListener onRssFetchedListener = new RssFetcher.OnRssFetchedListener() {
        @Override
        public void onRssFetched(@Nullable String rssData) {
            Log.d(TAG, "onRssFetched() called with: rssData = [" + rssData + "]");
            RssParser parser = new RssParser();
            if (rssData != null) parser.parse(rssData, onRssParsedListener);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            fetchingRssFeed = false;
            if (listener != null) listener.onFailed(null);
        }
    };

    private RssParser.OnRssParsedListener onRssParsedListener = new RssParser.OnRssParsedListener() {
        @Override
        public void onRssParsed(@NonNull Channel channel) {
            Log.d(TAG, "onRssParsed() called with: channel = [" + channel + "]");
            fetchingRssFeed = false;
            if (listener != null) listener.onDataFetched(channel);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            fetchingRssFeed = false;
            if (listener != null) listener.onFailed(null);
        }
    };

    public interface OnRssFeedDataListener {
        void onDataFetched(@NonNull Channel channel);

        void onFailed(@Nullable String reason);
    }
}
