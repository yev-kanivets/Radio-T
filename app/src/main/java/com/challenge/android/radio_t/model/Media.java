package com.challenge.android.radio_t.model;

import android.support.annotation.Nullable;

public class Media {
    @Nullable
    private final String url;
    private final long fileSize;
    @Nullable
    private final String type;

    public Media(String url, long fileSize, String type) {
        this.url = url;
        this.fileSize = fileSize;
        this.type = type;
    }

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
}
