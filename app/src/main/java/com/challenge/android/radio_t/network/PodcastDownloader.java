package com.challenge.android.radio_t.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.challenge.android.radio_t.model.PodcastItem;

import java.io.File;

public class PodcastDownloader {
    private final Context context;
    private final DownloadManager downloadManager;

    public PodcastDownloader(Context context) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.context = context;
    }

    public void download(@Nullable PodcastItem podcastItem) {
        if (podcastItem == null || podcastItem.getMedia() == null) return;

        Uri destinationUri = Uri.fromFile(getDestinationPath(podcastItem));
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(podcastItem.getMedia().getUrl()));
        request.setTitle(podcastItem.getTitle());
        request.setDescription(podcastItem.getMedia().getUrl());
        request.setDestinationUri(destinationUri);

        downloadManager.enqueue(request);
    }

    public boolean isFileExist(@NonNull PodcastItem podcastItem) {
        return getDestinationPath(podcastItem).exists();
    }

    @NonNull
    public File getDestinationPath(@NonNull PodcastItem podcastItem) {
        if (podcastItem.getMedia() == null || podcastItem.getMedia().getFileName() == null)
            return new File("");
        return new File(context.getExternalFilesDir(null), podcastItem.getMedia().getFileName());
    }
}
