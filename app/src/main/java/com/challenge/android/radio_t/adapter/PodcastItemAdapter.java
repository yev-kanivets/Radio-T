package com.challenge.android.radio_t.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.challenge.android.radio_t.R;
import com.challenge.android.radio_t.model.PodcastItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PodcastItemAdapter extends RecyclerView.Adapter<PodcastItemAdapter.ViewHolder> {

    private List<PodcastItem> podcastList;

    public PodcastItemAdapter(List<PodcastItem> podcastList) {
        this.podcastList = podcastList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_podcast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PodcastItem podcastItem = podcastList.get(position);
        Picasso.with(holder.itemView.getContext()).load(podcastItem.getLink());
        holder.tvTitle.setText(trim(podcastItem.getTitle()));
        holder.tvSubtitle.setText(trim(podcastItem.getSubtitle()));
    }

    @Override
    public int getItemCount() {
        return podcastList.size();
    }

    @NonNull
    private String trim(@Nullable String string) {
        if (string == null) return "";
        else return string.trim();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_thumbnail)
        public ImageView ivThumbnail;
        @Bind(R.id.tv_title)
        public TextView tvTitle;
        @Bind(R.id.tv_subtitle)
        public TextView tvSubtitle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(ViewHolder.this, view);
        }
    }
}
