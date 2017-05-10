package com.blasco991.flickrclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

import java.util.List;

/**
 * Created by marian on 04/05/2017.
 */
public abstract class PictureInfoAdapter extends RecyclerView.Adapter<PictureInfoAdapter.UiHolder> {
    private static String TAG = PictureInfoAdapter.class.getName();

    private List<Entry> mDataset;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Entry entry);
    }

    PictureInfoAdapter(List<Entry> mDataset, OnItemClickListener listener) {
        this.mDataset = mDataset;
        this.listener = listener;
    }

    class UiHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        UiHolder(android.view.View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    @UiThread
    public UiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new UiHolder(cardView);
    }

    @Override
    @UiThread
    public abstract void onBindViewHolder(final UiHolder uiHolder, int position);

    @Override
    @UiThread
    public int getItemCount() {
        return mDataset.size();
    }

}