package com.challenge.android.radio_t.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.network.RssFeedDataProvider;
import com.challenge.android.radio_t.player.PodcastChannelPlayer;

public class PodcastService extends Service {
    public static final String ACTION_FETCH_RSS_FEED = "action_fetch_rss_feed";
    public static final String ACTION_SET_CURRENT_PODCAST_ITEM = "action_set_current_podcast_item";
    public static final String ACTION_PREV_PODCAST_ITEM = "action_prev_podcast_item";
    public static final String ACTION_NEXT_PODCAST_ITEM = "action_next_podcast_item";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_SET_POSITION = "action_set_position";

    public static final String BROADCAST_RSS_CHANNEL_FETCHED = "broadcast_rss_channel_fetched";
    public static final String BROADCAST_PODCAST_ITEM_SET = "broadcast_podcast_item_set";
    public static final String BROADCAST_TRACK_STATE_CHANGED = "broadcast_track_state_changed";

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PODCAST_ITEM = "extra_podcast_item";
    public static final String EXTRA_TRACK_STATE = "extra_track_state";
    public static final String EXTRA_TRACK_POSITION = "extra_track_position";

    private static final String TAG = "PodcastService";

    private RssFeedDataProvider feedDataProvider;
    private PodcastChannelPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");

        feedDataProvider = new RssFeedDataProvider();
        player = new PodcastChannelPlayer(PodcastService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = ["
                + flags + "], startId = [" + startId + "]");
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_FETCH_RSS_FEED:
                    feedDataProvider.fetchRssFeedData(feedDataListener);
                    break;

                case ACTION_SET_CURRENT_PODCAST_ITEM:
                    PodcastItem item = intent.getParcelableExtra(EXTRA_PODCAST_ITEM);
                    if (item != null) player.setCurrentPodcastItem(item);
                    break;

                case ACTION_PREV_PODCAST_ITEM:
                    player.prevPodcastItem();
                    break;

                case ACTION_NEXT_PODCAST_ITEM:
                    player.nextPodcastItem();
                    break;

                case ACTION_PLAY:
                    player.play();
                    break;

                case ACTION_PAUSE:
                    player.pause();
                    break;

                case ACTION_SET_POSITION:
                    int position = intent.getIntExtra(EXTRA_TRACK_POSITION, -1);
                    if (position != -1) player.setPosition(position);
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
        Log.d(TAG, "onDestroy() called");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private RssFeedDataProvider.OnRssFeedDataListener feedDataListener = new RssFeedDataProvider.OnRssFeedDataListener() {
        @Override
        public void onDataFetched(@NonNull Channel channel) {
            Log.d(TAG, "onDataFetched() called with: channel = [" + channel + "]");
            player.setChannel(channel);

            Intent intent = new Intent(BROADCAST_RSS_CHANNEL_FETCHED);
            intent.putExtra(EXTRA_CHANNEL, channel);
            sendBroadcast(intent);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: reason = [" + reason + "]");
        }
    };
}
