package com.challenge.android.radio_t.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.fragment.PodcastDetailFragment;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.service.PodcastService;

public class PodcastDetailActivity extends AppCompatActivity implements PodcastDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = "PodcastDetailActivity";

    public static final String KEY_PODCAST_ITEM = "key_podcast_item";

    private PodcastItem podcastItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);

        podcastItem = getIntent().getParcelableExtra(KEY_PODCAST_ITEM);
        if (podcastItem == null) {
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
        intentFilter.addAction(PodcastService.BROADCAST_PODCAST_ITEM_SET);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onPlayClicked() {

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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(podcastItem.getTitle());
        }
    }

    private void showPodcastDetailFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, PodcastDetailFragment.newInstance(podcastItem));
        transaction.commit();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PodcastService.BROADCAST_PODCAST_ITEM_SET:
                    PodcastItem item = intent.getParcelableExtra(PodcastService.EXTRA_PODCAST_ITEM);
                    if (item != null) {
                        podcastItem = item;
                        getIntent().putExtra(KEY_PODCAST_ITEM, podcastItem);
                        showPodcastDetailFragment();
                    }
                    break;

                default:
                    break;
            }
        }
    };
}
