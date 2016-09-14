package com.challenge.android.radio_t.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.fragment.PodcastDetailFragment;
import com.challenge.android.radio_t.model.PodcastItem;

public class PodcastDetailActivity extends AppCompatActivity implements PodcastDetailFragment.OnFragmentInteractionListener {
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

    @Override
    public void onPlayClicked(Uri uri) {

    }
}
