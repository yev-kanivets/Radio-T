package com.challenge.android.radio_t.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.widget.RemoteViews;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.Content;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.player.TrackState;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PodcastNotificationManager {
    private static final int NOTIFICATION_ID = 966;

    private Context context;
    @SuppressWarnings("FieldCanBeLocal")
    private Target thumbnailTarget;

    private LruCache<String, Bitmap> imageCache;

    public PodcastNotificationManager(Context context) {
        this.context = context;
        imageCache = new LruCache<>(10);
    }

    public void showNotification(@NonNull TrackState trackState) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setContent(getContent(trackState));
        notificationBuilder.setSmallIcon(R.drawable.radio);

        Notification notification = notificationBuilder.getNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = getBigContentView(trackState);
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void hideNotification() {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @NonNull
    private RemoteViews getContent(@NonNull TrackState trackState) {
        PodcastItem podcastItem = trackState.getPodcastItem();
        RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                R.layout.view_podcast_notification);

        if (isImageLoaded(podcastItem.getImageUrl())) notificationView.setImageViewBitmap(
                R.id.iv_cover, getImageFromMedia(podcastItem.getImageUrl()));
        else loadImageFromMedia(podcastItem.getImageUrl());
        notificationView.setTextViewText(R.id.tv_title, podcastItem.getTitle());
        notificationView.setTextViewText(R.id.tv_author, podcastItem.getAuthor());

        return notificationView;
    }

    @NonNull
    private RemoteViews getBigContentView(@NonNull TrackState trackState) {
        PodcastItem podcastItem = trackState.getPodcastItem();
        RemoteViews notificationView = new RemoteViews(context.getPackageName(),
                R.layout.view_podcast_notification_big);

        if (isImageLoaded(podcastItem.getImageUrl())) notificationView.setImageViewBitmap(
                R.id.iv_cover, getImageFromMedia(podcastItem.getImageUrl()));
        else loadImageFromMedia(podcastItem.getImageUrl());
        notificationView.setTextViewText(R.id.tv_title, podcastItem.getTitle());
        notificationView.setTextViewText(R.id.tv_author, podcastItem.getAuthor());
        notificationView.setTextViewText(R.id.tv_position, trackState.getPrettyPosition());
        notificationView.setTextViewText(R.id.tv_duration, trackState.getPrettyDuration());
        notificationView.setProgressBar(R.id.progress_bar, trackState.getDuration(),
                trackState.getPosition(), false);

        return notificationView;
    }

    private boolean isImageLoaded(@Nullable String imageUrl) {
        if (imageUrl == null) return false;
        else return imageCache.get(imageUrl) != null;
    }

    @Nullable
    private Bitmap getImageFromMedia(@Nullable String imageUrl) {
        if (imageUrl == null) return null;
        else return imageCache.get(imageUrl);
    }

    private void loadImageFromMedia(@Nullable final String imageUrl) {
        if (imageUrl == null || thumbnailTarget != null) return;
        thumbnailTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                PodcastNotificationManager.this.imageCache.put(imageUrl, bitmap);
                PodcastNotificationManager.this.thumbnailTarget = null;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(context).load(imageUrl).into(thumbnailTarget);
    }
}
