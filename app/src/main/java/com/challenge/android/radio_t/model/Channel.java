package com.challenge.android.radio_t.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Channel implements Parcelable {
    @Nullable
    private final String title;
    @Nullable
    private final String link;
    @Nullable
    private final String thumbnail;
    @Nullable
    private final String keywords;
    @NonNull
    private final List<PodcastItem> podcastItemList;

    public Channel(@Nullable String title, @Nullable String link, @Nullable String thumbnail,
                   @Nullable String keywords, @Nullable List<PodcastItem> podcastItemList) {
        this.title = title;
        this.link = link;
        this.thumbnail = thumbnail;
        this.keywords = keywords;
        this.podcastItemList = new ArrayList<>();
        if (podcastItemList != null) this.podcastItemList.addAll(podcastItemList);
    }

    protected Channel(Parcel in) {
        title = in.readString();
        link = in.readString();
        thumbnail = in.readString();
        keywords = in.readString();
        podcastItemList = in.createTypedArrayList(PodcastItem.CREATOR);
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }

    @Nullable
    public String getKeywords() {
        return keywords;
    }

    @NonNull
    public List<PodcastItem> getPodcastItemList() {
        return podcastItemList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(thumbnail);
        dest.writeString(keywords);
        dest.writeTypedList(podcastItemList);
    }
}
