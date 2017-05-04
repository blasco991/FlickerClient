package com.blasco991.flickrclient.view;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blasco991.flickrclient.FlickerApp;
import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Model;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PicturesListActivity extends ListActivity implements com.blasco991.flickrclient.view.View {
    private MVC mvc;
    private final static String TAG = PicturesListActivity.class.getName();
    private final static String PARAM_SEARCH_STRING = TAG + ".search_string";

    public static void start(Context parent, String search) {
        Intent intent = new Intent(parent, PicturesListActivity.class);
        intent.putExtra(PARAM_SEARCH_STRING, search);
        parent.startActivity(intent);
    }

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mvc = ((FlickerApp) getApplication()).getMVC();
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
        mvc.unregister(this);
        super.onStop();
    }

    @Override
    @UiThread
    public void onModelChanged() {
        ArrayAdapter<Model.PictureInfo> adapter = new PictureInfoAdapter
                (this, R.layout.row, mvc.model.getPictureInfos());
        setListAdapter(adapter);
    }

    @Override
    public void makeToast(String text) {

    }

    @Override
    public void makeToast(String text, int length) {

    }

    private class PictureInfoAdapter extends ArrayAdapter<Model.PictureInfo> {

        Context context;
        int layoutResourceId;
        Model.PictureInfo pictureInfos[] = null;

        PictureInfoAdapter(Context context, int layoutResourceId, Model.PictureInfo[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.pictureInfos = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            EntryHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new EntryHolder();
                holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
                holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

                row.setTag(holder);
            } else {
                holder = (EntryHolder) row.getTag();
            }

            Model.PictureInfo pictureInfo = pictureInfos[position];
            holder.txtTitle.setText(pictureInfo.getTitle());
            new ImageLoadTask(holder.imgIcon).execute(pictureInfo.getUrl());

            return row;
        }

    }

    private static class EntryHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }

    private class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

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
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
