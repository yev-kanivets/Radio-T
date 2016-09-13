package com.challenge.android.radio_t.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Channel {
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
}
