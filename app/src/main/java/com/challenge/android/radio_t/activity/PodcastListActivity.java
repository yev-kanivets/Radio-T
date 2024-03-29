package com.challenge.android.radio_t.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.fragment.PodcastListFragment;
import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.player.TrackState;
import com.challenge.android.radio_t.service.PodcastService;

import java.util.ArrayList;

public class PodcastListActivity extends AppCompatActivity implements PodcastListFragment.OnFragmentInteractionListener {
    public static final String KEY_CHANNEL = "key_channel";

    private Channel channel;
    private PodcastItem currentPodcastItem;
    private boolean waitingForBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channel = getIntent().getParcelableExtra(KEY_CHANNEL);
        if (channel == null) {
            finish();
            return;
        }

        initToolbar();
        showPodcastListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PodcastService.BROADCAST_TRACK_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onPodcastItemSelected(@NonNull PodcastItem podcastItem) {
        waitingForBroadcast = true;
        Intent intent = new Intent(PodcastListActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_SET_CURRENT_PODCAST_ITEM);
        intent.putExtra(PodcastService.EXTRA_PODCAST_ITEM, podcastItem);
        startService(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(channel.getTitle());
        }
    }

    private void showPodcastListFragment() {
        ArrayList<PodcastItem> podcastList = (ArrayList<PodcastItem>) channel.getPodcastItemList();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, PodcastListFragment.newInstance(podcastList));
        transaction.commit();
    }

    private void showPodcastDetailActivity(@NonNull TrackState trackState) {
        if (waitingForBroadcast) {
            waitingForBroadcast = false;
            currentPodcastItem = trackState.getPodcastItem();
            Intent intent = new Intent(PodcastListActivity.this, PodcastDetailActivity.class);
            intent.putExtra(PodcastDetailActivity.KEY_TRACK_STATE,
                    new TrackState(0, 0, false, currentPodcastItem));
            startActivity(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PodcastService.BROADCAST_TRACK_STATE_CHANGED:
                    TrackState trackState = intent.getParcelableExtra(PodcastService.EXTRA_TRACK_STATE);
                    if (trackState != null) showPodcastDetailActivity(trackState);
                    break;

                default:
                    break;
            }
        }
    };
}
