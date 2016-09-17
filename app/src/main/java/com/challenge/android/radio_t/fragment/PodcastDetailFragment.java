package com.challenge.android.radio_t.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.PodcastItem;
import com.challenge.android.radio_t.player.TrackState;
import com.challenge.android.radio_t.service.PodcastService;
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
    private static final String ARG_PODCAST_ITEM = "arg_podcast_item";

    private PodcastItem podcastItem;
    private boolean playing;
    private boolean seeking;

    private OnFragmentInteractionListener listener;

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
     * @param podcastItem podcast to display.
     * @return A new instance of fragment PodcastDetailFragment.
     */
    public static PodcastDetailFragment newInstance(@NonNull PodcastItem podcastItem) {
        PodcastDetailFragment fragment = new PodcastDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PODCAST_ITEM, podcastItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            podcastItem = getArguments().getParcelable(ARG_PODCAST_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_podcast_detail, container, false);
        initViews(rootView);
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
