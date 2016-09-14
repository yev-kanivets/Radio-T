package com.challenge.android.radio_t.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class Content implements Parcelable {
    @Nullable
    private final String url;
    private final long fileSize;
    @Nullable
    private final String type;

    public Content(@Nullable String url, long fileSize, @Nullable String type) {
        this.url = url;
        this.fileSize = fileSize;
        this.type = type;
    }

    protected Content(Parcel in) {
        url = in.readString();
        fileSize = in.readLong();
        type = in.readString();
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    @Nullable
    public String getUrl() {
        return url;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeLong(fileSize);
        dest.writeString(type);
    }
}
