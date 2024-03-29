package com.challenge.android.radio_t.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.network.PodcastDownloader;
import com.challenge.android.radio_t.player.TrackState;
import com.challenge.android.radio_t.service.PodcastService;
import com.challenge.android.radio_t.util.PermissionsChecker;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PodcastDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PodcastDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PodcastDetailFragment extends Fragment {
    private static final String ARG_TRACK_STATE = "arg_track_state";
    private static final int REQUEST_PERMISSIONS = 509;

    private TrackState trackState;
    private boolean playing;
    private boolean seeking;

    private OnFragmentInteractionListener listener;
    private Menu menu;
    private PodcastDownloader downloader;

    @Bind(R.id.iv_cover)
    public ImageView ivCover;
    @Bind(R.id.tv_subtitle)
    public TextView tvSubtitle;
    @Bind(R.id.seek_bar)
    public AppCompatSeekBar seekBar;
    @Bind(R.id.tv_position)
    public TextView tvPosition;
    @Bind(R.id.tv_duration)
    public TextView tvDuration;
    @Bind(R.id.iv_play_pause)
    public ImageView ivPlayPause;

    public PodcastDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param trackState track state with podcast to display.
     * @return A new instance of fragment PodcastDetailFragment.
     */
    public static PodcastDetailFragment newInstance(@NonNull TrackState trackState) {
        PodcastDetailFragment fragment = new PodcastDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACK_STATE, trackState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloader = new PodcastDownloader(getActivity());
        if (getArguments() != null) {
            trackState = getArguments().getParcelable(ARG_TRACK_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_podcast_detail, container, false);
        initViews(rootView);
        setHasOptionsMenu(true);
        updateWithTrackState(trackState);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PodcastService.BROADCAST_TRACK_STATE_CHANGED);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_podcast_details, menu);
        this.menu = menu;
        if (downloader.isFileExist(trackState.getPodcastItem())) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                if (checkWesPermission()) downloadPodcast();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) downloadPodcast();
    }

    @OnClick(R.id.iv_play_pause)
    public void playPause() {
        if (listener != null) {
            ivPlayPause.setEnabled(false);
            if (playing) listener.onPauseClicked();
            else listener.onPlayClicked();
        }
    }

    @OnClick(R.id.iv_prev)
    public void prevClicked() {
        if (listener != null) listener.onPrevClicked();
    }

    @OnClick(R.id.iv_next)
    public void nextClicked() {
        if (listener != null) listener.onNextClicked();
    }

    private void initViews(View rootView) {
        if (rootView == null) return;
        ButterKnife.bind(PodcastDetailFragment.this, rootView);

        PodcastItem podcastItem = trackState.getPodcastItem();
        seekBar.setEnabled(false);
        Picasso.with(getActivity()).load(podcastItem.getImageUrl()).into(ivCover);
        tvSubtitle.setText(podcastItem.getSubtitle());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && listener != null) listener.onSeek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seeking = false;
            }
        });
    }

    private boolean checkWesPermission() {
        PermissionsChecker permissionsChecker = new PermissionsChecker(getActivity());
        if (permissionsChecker.lacksPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
            return false;
        } else return true;
    }

    private void downloadPodcast() {
        downloader = new PodcastDownloader(getActivity());
        if (!downloader.isFileExist(trackState.getPodcastItem())) {
            downloader.download(trackState.getPodcastItem());
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }
    }

    private void updateWithTrackState(@NonNull TrackState trackState) {
        if (playing != trackState.isPlaying()) {
            playing = trackState.isPlaying();
            if (playing) ivPlayPause.setImageResource(R.drawable.ic_pause);
            else ivPlayPause.setImageResource(R.drawable.ic_play);
            ivPlayPause.setEnabled(true);
        }

        seekBar.setEnabled(true);
        seekBar.setMax(trackState.getDuration());
        if (!seeking) seekBar.setProgress(trackState.getPosition());
        tvPosition.setText(trackState.getPrettyPosition());
        tvDuration.setText(trackState.getPrettyDuration());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case PodcastService.BROADCAST_TRACK_STATE_CHANGED:
                    PodcastItem podcastItem = trackState.getPodcastItem();
                    TrackState trackState = intent.getParcelableExtra(PodcastService.EXTRA_TRACK_STATE);
                    if (trackState != null && podcastItem.equals(trackState.getPodcastItem()))
                        updateWithTrackState(trackState);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onPlayClicked();

        void onPauseClicked();

        void onPrevClicked();

        void onNextClicked();

        void onSeek(int progress);
    }
}
