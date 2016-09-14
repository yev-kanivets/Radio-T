package com.challenge.android.radio_t.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.adapter.PodcastItemAdapter;
import com.challenge.android.radio_t.model.PodcastItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PodcastListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PodcastListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PodcastListFragment extends Fragment {
    private static final String ARG_PODCAST_LIST = "arg_podcast_list";

    private ArrayList<PodcastItem> podcastList;

    private OnFragmentInteractionListener listener;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    public PodcastListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param podcastList list of podcasts to show.
     * @return A new instance of fragment PodcastListFragment.
     */
    public static PodcastListFragment newInstance(@NonNull ArrayList<PodcastItem> podcastList) {
        PodcastListFragment fragment = new PodcastListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PODCAST_LIST, podcastList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            podcastList = getArguments().getParcelableArrayList(ARG_PODCAST_LIST);
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
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void initViews(View rootView) {
        if (rootView == null) return;
        ButterKnife.bind(PodcastListFragment.this, rootView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new PodcastItemAdapter(podcastList));
    }

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
        void onPodcastItemSelected(@NonNull PodcastItem podcastItem);
    }
}
