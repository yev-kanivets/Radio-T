package com.challenge.android.radio_t.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.network.PodcastDownloader;

import java.io.File;

public class PodcastUriPathProvider {
    private PodcastDownloader downloadManager;

    public PodcastUriPathProvider(Context context) {
        downloadManager = new PodcastDownloader(context);
    }

    @Nullable
    public String getUri(@NonNull PodcastItem podcastItem) {
        if (podcastItem.getMedia() == null) return null;
        if (downloadManager.isFileExist(podcastItem)) {
            File localFile = downloadManager.getDestinationPath(podcastItem);
            if (localFile.length() == podcastItem.getMedia().getFileSize())
                return localFile.getPath();
            else return podcastItem.getMedia().getUrl();
        } else return podcastItem.getMedia().getUrl();
    }
}
