package com.challenge.android.radio_t.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.network.RssFetcher;
import com.challenge.android.radio_t.parser.RssParser;

public class PodcastService extends Service {
    public static final String ACTION_FETCH_RSS_FEED = "action_fetch_rss_feed";
    public static final String ACTION_SET_CURRENT_PODCAST_ITEM = "action_set_current_podcast_item";
    public static final String ACTION_PREV_PODCAST_ITEM = "action_prev_podcast_item";
    public static final String ACTION_NEXT_PODCAST_ITEM = "action_next_podcast_item";

    public static final String BROADCAST_RSS_CHANNEL_FETCHED = "broadcast_rss_channel_fetched";
    public static final String BROADCAST_PODCAST_ITEM_SET = "broadcast_podcast_item_set";

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PODCAST_ITEM = "extra_podcast_item";

    private static final String TAG = "PodcastService";
    private static final String RSS_FEED_URL = "http://feeds.rucast.net/radio-t";

    @Nullable
    private Channel channel;
    @Nullable
    private PodcastItem currentPodcastItem;
    private boolean fetchingRssFeed;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = ["
                + flags + "], startId = [" + startId + "]");
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_FETCH_RSS_FEED:
                    fetchRssFeed();
                    break;

                case ACTION_SET_CURRENT_PODCAST_ITEM:
                    PodcastItem item = intent.getParcelableExtra(EXTRA_PODCAST_ITEM);
                    if (item != null) setCurrentPodcastItem(item);
                    break;

                case ACTION_PREV_PODCAST_ITEM:
                    prevPodcastItem();
                    break;

                case ACTION_NEXT_PODCAST_ITEM:
                    nextPodcastItem();
                    break;

                default:
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy() called");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchRssFeed() {
        Log.d(TAG, "fetchRssFeed() called " + fetchingRssFeed);
        if (fetchingRssFeed) return;
        fetchingRssFeed = true;
        RssFetcher fetcher = new RssFetcher();
        fetcher.fetch(RSS_FEED_URL, onRssFetchedListener);
    }

    private void onRssChannelFetched(@NonNull Channel channel) {
        Log.d(TAG, "onRssChannelFetched() called with: channel = [" + channel + "]");
        this.channel = channel;

        Intent intent = new Intent(BROADCAST_RSS_CHANNEL_FETCHED);
        intent.putExtra(EXTRA_CHANNEL, channel);
        sendBroadcast(intent);
    }

    private void setCurrentPodcastItem(@NonNull PodcastItem currentPodcastItem) {
        Log.d(TAG, "setCurrentPodcastItem() called with: currentPodcastItem = [" + currentPodcastItem + "]");
        this.currentPodcastItem = currentPodcastItem;

        Intent intent = new Intent(BROADCAST_PODCAST_ITEM_SET);
        intent.putExtra(EXTRA_PODCAST_ITEM, currentPodcastItem);
        sendBroadcast(intent);
    }

    private void prevPodcastItem() {
        Log.d(TAG, "prevPodcastItem() called " + channel + " " + currentPodcastItem);
        if (channel == null || currentPodcastItem == null) return;

        int currentIndex = getPodcastItemIndex(currentPodcastItem);
        if (currentIndex == -1) return;

        int listSize = channel.getPodcastItemList().size();
        setCurrentPodcastItem(channel.getPodcastItemList().get((listSize + currentIndex - 1) % listSize));
    }

    private void nextPodcastItem() {
        Log.d(TAG, "nextPodcastItem() called " + channel + " " + currentPodcastItem);
        if (channel == null || currentPodcastItem == null) return;

        int currentIndex = getPodcastItemIndex(currentPodcastItem);
        if (currentIndex == -1) return;

        int listSize = channel.getPodcastItemList().size();
        setCurrentPodcastItem(channel.getPodcastItemList().get((currentIndex + 1) % listSize));
    }

    private int getPodcastItemIndex(@NonNull PodcastItem podcastItem) {
        if (channel == null) return -1;

        int index = -1;
        for (int i = 0; i < channel.getPodcastItemList().size(); i++) {
            if (podcastItem.equals(channel.getPodcastItemList().get(i))) index = i;
        }

        return index;
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
        }
    };

    private RssParser.OnRssParsedListener onRssParsedListener = new RssParser.OnRssParsedListener() {
        @Override
        public void onRssParsed(@NonNull Channel channel) {
            Log.d(TAG, "onRssParsed() called with: channel = [" + channel + "]");
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
