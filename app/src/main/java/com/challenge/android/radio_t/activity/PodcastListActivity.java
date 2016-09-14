package com.challenge.android.radio_t.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.fragment.PodcastListFragment;
import com.challenge.android.radio_t.model.Channel;
import com.challenge.android.radio_t.model.PodcastItem;

import java.util.ArrayList;

public class PodcastListActivity extends AppCompatActivity implements PodcastListFragment.OnFragmentInteractionListener {
    public static final String KEY_CHANNEL = "key_channel";

    private Channel channel;

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
    public void onPodcastItemSelected(@NonNull PodcastItem podcastItem) {
        Intent intent = new Intent(PodcastListActivity.this, PodcastDetailActivity.class);
        intent.putExtra(PodcastDetailActivity.KEY_PODCAST_ITEM, podcastItem);
        startActivity(intent);
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
}
