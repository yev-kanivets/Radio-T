package com.challenge.android.radio_t.model;

import android.support.annotation.Nullable;

public class PodcastItem {
    @Nullable
    private final String title;
    @Nullable
    private final String link;
    @Nullable
    private final String pubDate;
    @Nullable
    private final String description;
    @Nullable
    private final String author;
    @Nullable
    private final Content media;
    @Nullable
    private final String subtitle;
    @Nullable
    private final String keywords;

    public PodcastItem(@Nullable String title, @Nullable String link, @Nullable String pubDate,
                       @Nullable String description, @Nullable String author, @Nullable Content media,
                       @Nullable String subtitle, @Nullable String keywords) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.author = author;
        this.media = media;
        this.subtitle = subtitle;
        this.keywords = keywords;
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
    public String getPubDate() {
        return pubDate;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    @Nullable
    public Content getMedia() {
        return media;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    @Nullable
    public String getKeywords() {
        return keywords;
    }
}
