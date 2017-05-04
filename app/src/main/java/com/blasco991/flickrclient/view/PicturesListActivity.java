package com.blasco991.flickrclient.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.blasco991.flickrclient.FlickerApp;
import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PicturesListActivity extends Activity implements com.blasco991.flickrclient.view.View {
    private final static String TAG = PicturesListActivity.class.getName();
    private final static String PARAM_SEARCH_STRING = TAG + ".search_string";

    private MVC mvc;
    private RecyclerView.Adapter mAdapter;

    public static void start(Context parent, String search) {
        Intent intent = new Intent(parent, PicturesListActivity.class);
        intent.putExtra(PARAM_SEARCH_STRING, search);
        parent.startActivity(intent);
    }

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);
        mvc = ((FlickerApp) getApplication()).getMVC();

        mAdapter = new PictureInfoAdapter(mvc.model.getPictureInfos());
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mvc.controller.fetchPictureInfos(getIntent().getStringExtra(PARAM_SEARCH_STRING));
    }

    @Override
    @UiThread
    protected void onStart() {
        super.onStart();
        mvc.register(this);
        onModelChanged();
    }

    @Override
    @UiThread
    protected void onStop() {
        super.onStop();
        mvc.unregister(this);
    }

    @Override
    @UiThread
    public void onModelChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private static class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        ImageLoadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL urlConnection = new URL(params[0]);
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
            imageView.setImageBitmap(result);
        }

    }

}
