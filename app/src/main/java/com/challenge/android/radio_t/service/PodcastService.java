package com.challenge.android.radio_t.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.network.RssFetcher;
import com.challenge.android.radio_t.parser.RssParser;

public class PodcastService extends Service {
    public static final String ACTION_FETCH_RSS_FEED = "action_fetch_rss_feed";

    public static final String BROADCAST_RSS_CHANNEL_FETCHED = "broadcast_rss_channel_fetched";
    public static final String EXTRA_CHANNEL = "extra_channel";

    private static final String TAG = "PodcastService";
    private static final String RSS_FEED_URL = "http://feeds.rucast.net/radio-t";

    @Nullable
    private Channel channel;
    private boolean fetchingRssFeed;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_FETCH_RSS_FEED:
                    fetchRssFeed();
                    break;

                default:
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchRssFeed() {
        if (fetchingRssFeed) return;
        fetchingRssFeed = true;
        RssFetcher fetcher = new RssFetcher();
        fetcher.fetch(RSS_FEED_URL, onRssFetchedListener);
    }

    private void onRssChannelFetched(@NonNull Channel channel) {
        this.channel = channel;

        Intent intent = new Intent(BROADCAST_RSS_CHANNEL_FETCHED);
        intent.putExtra(EXTRA_CHANNEL, channel);
        sendBroadcast(intent);
    }

    private RssFetcher.OnRssFetchedListener onRssFetchedListener = new RssFetcher.OnRssFetchedListener() {
        @Override
        public void onRssFetched(@Nullable String rssData) {
            RssParser parser = new RssParser();
            if (rssData != null) parser.parse(rssData, onRssParsedListener);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            fetchingRssFeed = false;
        }
    };

    private RssParser.OnRssParsedListener onRssParsedListener = new RssParser.OnRssParsedListener() {
        @Override
        public void onRssParsed(@NonNull Channel channel) {
            onRssChannelFetched(channel);
            fetchingRssFeed = false;
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            fetchingRssFeed = false;
        }
    };
}
