package com.challenge.android.radio_t.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PodcastItem implements Parcelable {
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
    @Nullable
    private String imageUrl;

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

    protected PodcastItem(Parcel in) {
        title = in.readString();
        link = in.readString();
        pubDate = in.readString();
        description = in.readString();
        author = in.readString();
        media = in.readParcelable(Content.class.getClassLoader());
        subtitle = in.readString();
        keywords = in.readString();
    }

    public static final Creator<PodcastItem> CREATOR = new Creator<PodcastItem>() {
        @Override
        public PodcastItem createFromParcel(Parcel in) {
            return new PodcastItem(in);
        }

        @Override
        public PodcastItem[] newArray(int size) {
            return new PodcastItem[size];
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

    @NonNull
    public String getImageUrl() {
        if (imageUrl == null) {
            imageUrl = getThumbnailUrlFromHtml(description);
        }
        return imageUrl;
    }

    @NonNull
    private String getThumbnailUrlFromHtml(String html) {
        if (description == null) return "";
        Pattern pattern = Pattern.compile("http.*((jpg)|(png))");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) return matcher.group(0);
        else return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(pubDate);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeParcelable(media, flags);
        dest.writeString(subtitle);
        dest.writeString(keywords);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PodcastItem) {
            PodcastItem podcastItem = (PodcastItem) o;
            if (getLink() == null) return false;
            else return getLink().equals(podcastItem.getLink());
        } else return false;
    }
}
