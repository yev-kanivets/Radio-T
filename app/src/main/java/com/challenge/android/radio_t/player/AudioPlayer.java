package com.challenge.android.radio_t.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    public AudioPlayer() {
    }

    private MediaPlayer mediaPlayer;
    private String url;
    private boolean prepared;

    public boolean isPrepared() {
        return prepared;
    }

    public void prepareStream(String url, final MediaPlayer.OnPreparedListener onPreparedListener) throws Exception {
        this.url = url;

        destroy();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                prepared = true;
                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared(mediaPlayer);
                }
            }
        });

        mediaPlayer.prepareAsync();
    }

    public void playStream(String path) {
        Log.d(TAG, "playStream() called with: " + "path = [" + path + "]");
        if (mediaPlayer != null && this.url.equals(path)) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        Log.d(TAG, "pause() called");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void destroy() {
        Log.d(TAG, "destroy() called");
        prepared = false;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "MediaPlayer is destroyed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        else return 0;
    }

    public void setPosition(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else return 0;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) return mediaPlayer.isPlaying();
        else return false;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }
}
