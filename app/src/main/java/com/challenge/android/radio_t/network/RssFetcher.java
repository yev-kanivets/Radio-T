package com.challenge.android.radio_t.network;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RssFetcher {
    public void fetch(@Nullable String rssUrl, @Nullable OnRssFetchedListener onRssFetchedListener) {
        if (onRssFetchedListener == null) return;
        if (rssUrl == null) onRssFetchedListener.onFailed(null);

        RssFetchAsyncTask task = new RssFetchAsyncTask(onRssFetchedListener);
        task.execute(rssUrl);
    }

    private class RssFetchAsyncTask extends AsyncTask<String, String, String> {
        @NonNull
        private final OnRssFetchedListener onRssFetchedListener;

        public RssFetchAsyncTask(@NonNull OnRssFetchedListener onRssFetchedListener) {
            this.onRssFetchedListener = onRssFetchedListener;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params == null || params.length == 0 || params[0] == null) return null;

            String rssUrl = params[0];
            StringBuilder sb = new StringBuilder();

            // We need to use Network operation only in one place, so no need to include 3rd party lib
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(rssUrl);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                Scanner sc = new Scanner(in);

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    sb.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.isEmpty()) onRssFetchedListener.onFailed(null);
            else onRssFetchedListener.onRssFetched(s);
        }
    }

    public interface OnRssFetchedListener {
        void onRssFetched(@Nullable String rssData);

        void onFailed(@Nullable String reason);
    }
}
