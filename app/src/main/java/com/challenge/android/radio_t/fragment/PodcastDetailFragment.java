package com.challenge.android.radio_t.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.PodcastItem;

import butterknife.ButterKnife;

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

    private OnFragmentInteractionListener mListener;

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
        View rootView = inflater.inflate(R.layout.fragment_podcast_list, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    private void initViews(View rootView) {
        if (rootView == null) return;
        ButterKnife.bind(PodcastDetailFragment.this, rootView);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onPlayClicked(Uri uri);
    }
}
