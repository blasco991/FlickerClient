package com.blasco991.flickrclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by marian on 04/05/2017.
 */
public class PictureInfoAdapter extends RecyclerView.Adapter<PictureInfoAdapter.MyViewHolder> {
    private static String TAG = PictureInfoAdapter.class.getName();

    private List<Entry> mDataset;

    PictureInfoAdapter(List<Entry> myDataset) {
        mDataset = myDataset;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView textView;
        ImageView imageView;

        MyViewHolder(android.view.View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    private class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

        private ProgressBar progressBar;
        private ImageView imageView;
        private String url;

        ImageLoadTask(ProgressBar progressBar, ImageView imageView) {
            this.progressBar = progressBar;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(android.view.View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL urlConnection = new URL(url = params[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        @UiThread
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //addBitmapToMemoryCache(url, result);
            imageView.setImageBitmap(result);
            imageView.post(() -> imageView.animate().alpha(1).setDuration(2000));
            progressBar.setVisibility(android.view.View.GONE);
        }

    }

    @Override
    @UiThread
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(cardView);
    }

    @Override
    @UiThread
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getTitle());
        final String imageKey = mDataset.get(position).getUrl();
        Bitmap bitmap;
        if ((bitmap = getBitmapFromMemCache(imageKey)) != null) {
            holder.imageView.setImageBitmap(bitmap);
            Log.d(TAG, "cache hit");
        } else {
            Log.d(TAG, "cache miss");
            holder.imageView.setImageResource(0);
            holder.imageView.setTag(imageKey);
            new ImageLoadTask(holder.progressBar, holder.imageView).execute(mDataset.get(position).getUrl());
        }
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