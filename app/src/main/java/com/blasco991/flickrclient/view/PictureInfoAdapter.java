package com.blasco991.flickrclient.view;

import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

/**
 * Created by marian on 04/05/2017.
 */
public class PictureInfoAdapter extends RecyclerView.Adapter<PictureInfoAdapter.Card> {
    private Entry[] mDataset;

    public class Card extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgView;

        public Card(TextView v, ImageView iv) {
            super(v);
            txtTitle = v;
            imgView = iv;
        }
    }

    public PictureInfoAdapter(Entry[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    @UiThread
    public Card onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null, false);
        TextView v = (TextView) cardView.findViewById(R.id.txtTitle);
        ImageView iv = (ImageView) cardView.findViewById(R.id.imgView);
        return new Card(v, iv);
    }

    @Override
    @UiThread
    public void onBindViewHolder(Card holder, int position) {
        holder.txtTitle.setText(mDataset[position].getTitle());
        //new ImageLoadTask(holder.imgView).execute(mDataset[position].getUrl());
    }

    @Override
    @UiThread
    public int getItemCount() {
        return mDataset.length;
    }
}