package com.challenge.android.radio_t.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.parser.RssParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class RssFeedDataProvider {
    private static final String TAG = "RssFeedDataProvider";
    private static final String RSS_FEED_URL = "http://feeds.rucast.net/radio-t";
    private static final String RSS_FEED_FILE_NAME = "rss_feed.txt";

    @Nullable
    private Context context;

    private boolean fetchingRssFeed;
    @Nullable
    private OnRssFeedDataListener listener;

    public RssFeedDataProvider(@Nullable Context context) {
        this.context = context;
    }

    public void fetchRssFeedData(@Nullable OnRssFeedDataListener listener) {
        this.listener = listener;
        Log.d(TAG, "fetchRssFeed() called " + fetchingRssFeed);
        if (fetchingRssFeed) return;
        fetchingRssFeed = true;
        RssFetcher fetcher = new RssFetcher();
        fetcher.fetch(RSS_FEED_URL, onRssFetchedListener);
    }

    private RssFetcher.OnRssFetchedListener onRssFetchedListener = new RssFetcher.OnRssFetchedListener() {
        @Override
        public void onRssFetched(@Nullable String rssData) {
            Log.d(TAG, "onRssFetched() called with: rssData = [" + rssData + "]");
            if (rssData == null) rssData = loadRssFeedFromStorage();
            if (rssData != null) {
                saveRssFeedToStorage(rssData);
                RssParser parser = new RssParser();
                parser.parse(rssData, onRssParsedListener);
            } else if (listener != null) listener.onFailed(null);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            String rssData = loadRssFeedFromStorage();
            if (rssData != null) {
                RssParser parser = new RssParser();
                parser.parse(rssData, onRssParsedListener);
            } else if (listener != null) {
                fetchingRssFeed = false;
                listener.onFailed(null);
            }
        }
    };

    private RssParser.OnRssParsedListener onRssParsedListener = new RssParser.OnRssParsedListener() {
        @Override
        public void onRssParsed(@NonNull Channel channel) {
            Log.d(TAG, "onRssParsed() called with: channel = [" + channel + "]");
            fetchingRssFeed = false;
            if (listener != null) listener.onDataFetched(channel);
        }

        @Override
        public void onFailed(@Nullable String reason) {
            Log.d(TAG, "onFailed() called with: " + "reason = [" + reason + "]");
            fetchingRssFeed = false;
            if (listener != null) listener.onFailed(null);
        }
    };

    private void saveRssFeedToStorage(@NonNull String rssFeed) {
        if (context == null) return;
        File filesDir = context.getFilesDir();
        if (filesDir != null) {
            File rssFeedFile = new File(filesDir, RSS_FEED_FILE_NAME);
            try {
                PrintWriter pw = new PrintWriter(rssFeedFile);
                pw.print(rssFeed);
                pw.flush();
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    private String loadRssFeedFromStorage() {
        if (context == null) return null;
        File filesDir = context.getFilesDir();
        if (filesDir != null) {
            File rssFeedFile = new File(filesDir, RSS_FEED_FILE_NAME);
            try {
                Scanner scanner = new Scanner(rssFeedFile);
                StringBuilder sb = new StringBuilder();
                while (scanner.hasNextLine()) {
                    sb.append(scanner.nextLine()).append("\n");
                }
                return sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }

    public interface OnRssFeedDataListener {
        void onDataFetched(@NonNull Channel channel);

        void onFailed(@Nullable String reason);
    }
}
