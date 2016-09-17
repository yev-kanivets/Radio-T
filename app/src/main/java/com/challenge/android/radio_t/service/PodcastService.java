package com.challenge.android.radio_t.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.network.RssFeedDataProvider;
import com.challenge.android.radio_t.player.AudioPlayer;
import com.challenge.android.radio_t.player.TrackState;

import java.util.Timer;
import java.util.TimerTask;

public class PodcastService extends Service {
    public static final String ACTION_FETCH_RSS_FEED = "action_fetch_rss_feed";
    public static final String ACTION_SET_CURRENT_PODCAST_ITEM = "action_set_current_podcast_item";
    public static final String ACTION_PREV_PODCAST_ITEM = "action_prev_podcast_item";
    public static final String ACTION_NEXT_PODCAST_ITEM = "action_next_podcast_item";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";

    public static final String BROADCAST_RSS_CHANNEL_FETCHED = "broadcast_rss_channel_fetched";
    public static final String BROADCAST_PODCAST_ITEM_SET = "broadcast_podcast_item_set";
    public static final String BROADCAST_TRACK_STATE_CHANGED = "broadcast_track_state_changed";

    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_PODCAST_ITEM = "extra_podcast_item";
    public static final String EXTRA_TRACK_STATE = "extra_track_state";

    private static final String TAG = "PodcastService";

    @Nullable
    private Channel channel;
    @Nullable
    private PodcastItem currentPodcastItem;

    private RssFeedDataProvider feedDataProvider;
    private AudioPlayer audioPlayer;
    private Timer timer;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        super.onCreate();
        feedDataProvider = new RssFeedDataProvider();
        audioPlayer = new AudioPlayer();
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
                    if (item != null) setCurrentPodcastItem(item);
                    break;

                case ACTION_PREV_PODCAST_ITEM:
                    prevPodcastItem();
                    break;

                case ACTION_NEXT_PODCAST_ITEM:
                    nextPodcastItem();
                    break;

                case ACTION_PLAY:
                    play();
                    break;

                case ACTION_PAUSE:
                    pause();
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

    private void setCurrentPodcastItem(@NonNull PodcastItem currentPodcastItem) {
        Log.d(TAG, "setCurrentPodcastItem() called with: currentPodcastItem = [" + currentPodcastItem + "]");
        this.currentPodcastItem = currentPodcastItem;
        stop();

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
        setCurrentPodcastItem(channel.getPodcastItemList().get((listSize + currentIndex + 1) % listSize));
        stop();
    }

    private void nextPodcastItem() {
        Log.d(TAG, "nextPodcastItem() called " + channel + " " + currentPodcastItem);
        if (channel == null || currentPodcastItem == null) return;

        int currentIndex = getPodcastItemIndex(currentPodcastItem);
        if (currentIndex == -1) return;

        int listSize = channel.getPodcastItemList().size();
        setCurrentPodcastItem(channel.getPodcastItemList().get((currentIndex - 1) % listSize));
        stop();
    }

    private int getPodcastItemIndex(@NonNull PodcastItem podcastItem) {
        if (channel == null) return -1;

        int index = -1;
        for (int i = 0; i < channel.getPodcastItemList().size(); i++) {
            if (podcastItem.equals(channel.getPodcastItemList().get(i))) index = i;
        }

        return index;
    }

    private void play() {
        Log.d(TAG, "play() called");
        if (currentPodcastItem == null || currentPodcastItem.getMedia() == null) return;
        final String url = currentPodcastItem.getMedia().getUrl();

        if (audioPlayer.isPrepared()) {
            audioPlayer.playStream(url);
            trackStateUpdated();
            startTimer();
        } else {
            try {
                audioPlayer.prepareStream(url, new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        audioPlayer.playStream(url);
                        trackStateUpdated();
                        startTimer();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pause() {
        Log.d(TAG, "pause() called");
        if (currentPodcastItem == null) return;
        if (audioPlayer.isPlaying()) audioPlayer.pause();
        trackStateUpdated();
        stopTimer();
    }

    private void stop() {
        Log.d(TAG, "stop() called");
        if (currentPodcastItem == null) return;
        pause();
        audioPlayer.destroy();
    }

    private void trackStateUpdated() {
        Log.d(TAG, "trackStateUpdated() called " + audioPlayer.isPlaying());
        Intent intent = new Intent(BROADCAST_TRACK_STATE_CHANGED);
        intent.putExtra(EXTRA_TRACK_STATE, new TrackState(audioPlayer.getCurrentPosition(),
                audioPlayer.getDuration(), audioPlayer.isPlaying()));
        sendBroadcast(intent);
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                trackStateUpdated();
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private RssFeedDataProvider.OnRssFeedDataListener feedDataListener = new RssFeedDataProvider.OnRssFeedDataListener() {
        @Override
        public void onDataFetched(@NonNull Channel channel) {
            Log.d(TAG, "onDataFetched() called with: channel = [" + channel + "]");
            PodcastService.this.channel = channel;

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
