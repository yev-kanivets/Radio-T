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
import android.util.Log;
import android.view.MenuItem;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.fragment.PodcastDetailFragment;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.player.TrackState;
import com.challenge.android.radio_t.service.PodcastService;

public class PodcastDetailActivity extends AppCompatActivity implements PodcastDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = "PodcastDetailActivity";

    public static final String KEY_TRACK_STATE = "key_track_state";

    private TrackState trackState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);

        trackState = getIntent().getParcelableExtra(KEY_TRACK_STATE);
        if (trackState == null) {
            finish();
            return;
        }

        initToolbar();
        showPodcastDetailFragment();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPlayClicked() {
        Log.d(TAG, "onPlayClicked() called");
        Intent intent = new Intent(PodcastDetailActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_PLAY);
        startService(intent);
    }

    @Override
    public void onPauseClicked() {
        Log.d(TAG, "onPauseClicked() called");
        Intent intent = new Intent(PodcastDetailActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_PAUSE);
        startService(intent);
    }

    @Override
    public void onPrevClicked() {
        Log.d(TAG, "onPrevClicked() called");
        Intent intent = new Intent(PodcastDetailActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_PREV_PODCAST_ITEM);
        startService(intent);
    }

    @Override
    public void onNextClicked() {
        Log.d(TAG, "onNextClicked() called");
        Intent intent = new Intent(PodcastDetailActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_NEXT_PODCAST_ITEM);
        startService(intent);
    }

    @Override
    public void onSeek(int progress) {
        Log.d(TAG, "onSeek() called with: progress = [" + progress + "]");
        Intent intent = new Intent(PodcastDetailActivity.this, PodcastService.class);
        intent.setAction(PodcastService.ACTION_SET_POSITION);
        intent.putExtra(PodcastService.EXTRA_TRACK_POSITION, progress);
        startService(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(trackState.getPodcastItem().getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showPodcastDetailFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, PodcastDetailFragment.newInstance(trackState));
        transaction.commit();
    }

    private void trackStateChanged(@NonNull TrackState trackState) {
        PodcastItem oldPodcastItem = this.trackState.getPodcastItem();
        this.trackState = trackState;
        getIntent().putExtra(KEY_TRACK_STATE, trackState);

        if (!oldPodcastItem.equals(trackState.getPodcastItem())) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(trackState.getPodcastItem().getTitle());
            }
            showPodcastDetailFragment();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PodcastService.BROADCAST_TRACK_STATE_CHANGED:
                    TrackState trackState = intent.getParcelableExtra(PodcastService.EXTRA_TRACK_STATE);
                    if (trackState != null) trackStateChanged(trackState);
                    break;

                default:
                    break;
            }
        }
    };
}
