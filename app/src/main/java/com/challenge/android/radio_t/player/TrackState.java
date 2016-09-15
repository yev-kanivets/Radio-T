package com.challenge.android.radio_t.player;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class TrackState implements Parcelable {
    private final int position;
    private final int duration;
    private final boolean playing;

    public TrackState(int position, int duration, boolean playing) {
        this.position = position;
        this.duration = duration;
        this.playing = playing;
    }

    protected TrackState(Parcel in) {
        position = in.readInt();
        duration = in.readInt();
        playing = in.readByte() != 0;
    }

    public static final Creator<TrackState> CREATOR = new Creator<TrackState>() {
        @Override
        public TrackState createFromParcel(Parcel in) {
            return new TrackState(in);
        }

        @Override
        public TrackState[] newArray(int size) {
            return new TrackState[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getPrettyPosition() {
        return getPrettyTime(position);
    }

    public String getPrettyDuration() {
        return getPrettyTime(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(duration);
        dest.writeByte((byte) (playing ? 1 : 0));
    }

    @NonNull
    private String getPrettyTime(int timeInMilliseconds) {
        timeInMilliseconds /= 1000;
        int minutes = timeInMilliseconds / 60;
        int seconds = timeInMilliseconds % 60;

        StringBuilder sb = new StringBuilder();
        if (minutes < 10) sb.append("0");
        sb.append(minutes);
        sb.append(":");
        if (seconds < 10) sb.append("0");
        sb.append(seconds);

        return sb.toString();
    }
}
