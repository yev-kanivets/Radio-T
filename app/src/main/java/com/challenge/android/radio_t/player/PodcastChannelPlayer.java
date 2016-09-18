package com.challenge.android.radio_t.player;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.notification.PodcastNotificationManager;
import com.challenge.android.radio_t.service.PodcastService;

import java.util.Timer;
import java.util.TimerTask;

public class PodcastChannelPlayer {
    private static final String TAG = "PodcastChannelPlayer";

    @Nullable
    private Channel channel;
    @Nullable
    private PodcastItem currentPodcastItem;

    private PodcastNotificationManager notificationManager;
    private AudioPlayer audioPlayer;
    private Timer timer;
    @Nullable
    private Context context;

    public PodcastChannelPlayer(@Nullable Context context) {
        this.context = context;
        audioPlayer = new AudioPlayer();
        notificationManager = new PodcastNotificationManager(context);
    }

    public void setChannel(@Nullable Channel channel) {
        this.channel = channel;
    }

    public void setCurrentPodcastItem(@NonNull PodcastItem currentPodcastItem) {
        Log.d(TAG, "setCurrentPodcastItem() called with: currentPodcastItem = [" + currentPodcastItem + "]");
        this.currentPodcastItem = currentPodcastItem;
        stop();

        trackStateUpdated();
    }

    public void prevPodcastItem() {
        Log.d(TAG, "prevPodcastItem() called " + channel + " " + currentPodcastItem);
        if (channel == null || currentPodcastItem == null) return;

        int currentIndex = getPodcastItemIndex(currentPodcastItem);
        if (currentIndex == -1) return;

        int listSize = channel.getPodcastItemList().size();
        setCurrentPodcastItem(channel.getPodcastItemList().get((currentIndex + 1) % listSize));
        stop();
    }

    public void nextPodcastItem() {
        Log.d(TAG, "nextPodcastItem() called " + channel + " " + currentPodcastItem);
        if (channel == null || currentPodcastItem == null) return;

        int currentIndex = getPodcastItemIndex(currentPodcastItem);
        if (currentIndex == -1) return;

        int listSize = channel.getPodcastItemList().size();
        setCurrentPodcastItem(channel.getPodcastItemList().get((listSize + currentIndex - 1) % listSize));
        stop();
    }

    public void play() {
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

    public void pause() {
        Log.d(TAG, "pause() called");
        if (currentPodcastItem == null) return;
        if (audioPlayer.isPlaying()) audioPlayer.pause();
        trackStateUpdated();
        stopTimer();
    }

    public void setPosition(int position) {
        Log.d(TAG, "setPosition() called with: position = [" + position + "]");
        if (currentPodcastItem == null) return;
        if (audioPlayer.isPrepared()) audioPlayer.setPosition(position);
    }

    public void destroy() {
        notificationManager.hideNotification();
    }

    private void stop() {
        Log.d(TAG, "stop() called");
        if (currentPodcastItem == null) return;
        pause();
        audioPlayer.destroy();
    }

    private int getPodcastItemIndex(@NonNull PodcastItem podcastItem) {
        if (channel == null) return -1;

        int index = -1;
        for (int i = 0; i < channel.getPodcastItemList().size(); i++) {
            if (podcastItem.equals(channel.getPodcastItemList().get(i))) index = i;
        }

        return index;
    }

    private void trackStateUpdated() {
        Log.d(TAG, "trackStateUpdated() called " + audioPlayer.isPlaying());
        TrackState trackState = new TrackState(audioPlayer.getCurrentPosition(),
                audioPlayer.getDuration(), audioPlayer.isPlaying(), currentPodcastItem);

        Intent intent = new Intent(PodcastService.BROADCAST_TRACK_STATE_CHANGED);
        intent.putExtra(PodcastService.EXTRA_TRACK_STATE, trackState);
        sendBroadcast(intent);

        notificationManager.showNotification(trackState);
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

    private void sendBroadcast(@NonNull Intent intent) {
        if (context != null) context.sendBroadcast(intent);
    }
}
