package com.challenge.android.radio_t.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.service.PodcastService;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(SplashActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_FETCH_RSS_FEED);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PodcastService.BROADCAST_RSS_CHANNEL_FETCHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void channelReceived(@Nullable Channel channel) {
        if (isFinishing()) return;

        if (channel == null) showRssFailedAlert();
        else {
            Intent intent = new Intent(SplashActivity.this, PodcastListActivity.class);
            intent.putExtra(PodcastListActivity.KEY_CHANNEL, channel);
            startActivity(intent);
            finish();
        }
    }

    private void showRssFailedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(getString(R.string.failed_load_rss));
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PodcastService.BROADCAST_RSS_CHANNEL_FETCHED:
                    Channel channel = intent.getParcelableExtra(PodcastService.EXTRA_CHANNEL);
                    channelReceived(channel);
                    break;

                default:
                    break;
            }
        }
    };
}
