package com.blasco991.flickrclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

import java.util.List;

/**
 * Created by marian on 04/05/2017.
 */
public class PictureInfoAdapter extends RecyclerView.Adapter<PictureInfoAdapter.UiHolder> {
    private static String TAG = PictureInfoAdapter.class.getName();

    private List<Entry> mDataset;

    PictureInfoAdapter(List<Entry> myDataset) {
        mDataset = myDataset;
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
    public void onBindViewHolder(final UiHolder uiHolder, int position) {
        uiHolder.textView.setText(mDataset.get(position).getTitle());
        uiHolder.imageView.setContentDescription(mDataset.get(position).getTitle());
        Bitmap bitmap = mDataset.get(position).getPreview();
        if (bitmap != null) {
            uiHolder.imageView.setImageBitmap(bitmap);
            uiHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        uiHolder.imageView.animate().alpha(1).setDuration(1000);
        uiHolder.imageView.setTag(mDataset.get(position)); //set tag to be Entry instance
    }

    @Override
    @UiThread
    public int getItemCount() {
        return mDataset.size();
    }

    static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        interface OnItemClickListener {
            void onItemClick(android.view.View view, int position);

            void onLongItemClick(android.view.View view, int position);
        }

        GestureDetector mGestureDetector;

        RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    android.view.View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            android.view.View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

}