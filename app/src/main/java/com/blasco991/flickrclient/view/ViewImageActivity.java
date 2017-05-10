package com.blasco991.flickrclient.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewImageActivity extends AppCompatActivity {

    private static final String TAG = ViewImageActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);


        ImageView imageView = (ImageView) findViewById(R.id.imageViewFull);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarFull);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        Entry entry = (Entry) bundle.getSerializable("entry");

        getSupportActionBar().setTitle(entry.getTitle());
        getSupportActionBar().setSubtitle(entry.getTags());
        new LoadImage(imageView, progressBar).execute(entry.getUrl());
        Log.d(TAG, entry.getUrl());
    }

    public class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private ProgressBar progressBar;

        LoadImage(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setVisibility(View.INVISIBLE);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
