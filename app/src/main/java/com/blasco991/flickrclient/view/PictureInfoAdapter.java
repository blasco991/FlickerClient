package com.blasco991.flickrclient.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.*;
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
    private LruCache<String, Bitmap> mMemoryCache;

    private List<Entry> mDataset;

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

    PictureInfoAdapter(List<Entry> myDataset) {
        mDataset = myDataset;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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
            new ImageLoadTask(holder.progressBar, holder.imageView).execute(mDataset.get(position).getUrl());
        }
    }

    @Override
    @UiThread
    public int getItemCount() {
        return mDataset.size();
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
            addBitmapToMemoryCache(url, result);
            imageView.setImageBitmap(result);
            imageView.post(() -> imageView.animate().alpha(1).setDuration(2000));
            progressBar.setVisibility(android.view.View.GONE);
        }

    }

}