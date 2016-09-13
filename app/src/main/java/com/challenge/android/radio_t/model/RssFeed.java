package com.challenge.android.radio_t.model;

import android.support.annotation.Nullable;

public class RssFeed {
    @Nullable
    private final Channel channel;

    public RssFeed(@Nullable Channel channel) {
        this.channel = channel;
    }

    @Nullable
    public Channel getChannel() {
        return channel;
    }
}
