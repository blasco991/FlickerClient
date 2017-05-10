package com.blasco991.flickrclient.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.blasco991.flickrclient.FlickerApp;
import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.R;
import com.blasco991.flickrclient.model.Entry;

import java.util.List;

public class PicturesListActivity extends Activity implements com.blasco991.flickrclient.view.View {
    private final static String TAG = PicturesListActivity.class.getName();

    private MVC mvc;
    private RecyclerView.Adapter mAdapter;


    public static void start(Context parent) {
        Intent intent = new Intent(parent, PicturesListActivity.class);
        parent.startActivity(intent);
    }

    @Override
    @UiThread
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_list);
        mvc = ((FlickerApp) getApplication()).getMVC();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        PictureInfoAdapter.OnItemClickListener listener = entry -> {
            Intent intent = new Intent(PicturesListActivity.this, ViewImageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("entry", entry);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        };

        List<Entry> mDataset = mvc.model.getPictureInfos();
        mAdapter = new PictureInfoAdapter(mDataset, listener) {
            @Override
            public void onBindViewHolder(UiHolder uiHolder, int position) {
                uiHolder.textView.setText(mDataset.get(position).getTitle());
                uiHolder.imageView.setContentDescription(mDataset.get(position).getTitle());
                Bitmap bitmap = mvc.controller.getBitmap(mDataset.get(position).getUrlPreview(), position);
                if (bitmap != null) {
                    uiHolder.imageView.setImageBitmap(bitmap);
                    uiHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                uiHolder.imageView.animate().alpha(1).setDuration(1000);
                uiHolder.imageView.setTag(mDataset.get(position)); //set tag to be Entry instance
                uiHolder.imageView.setOnClickListener(v -> listener.onItemClick((Entry) v.findViewById(R.id.imageView).getTag()));
            }

        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    @UiThread
    protected void onStart() {
        super.onStart();
        mvc.register(this);
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

    @Override
    public void onModelChanged(int entryID) {
        mAdapter.notifyItemChanged(entryID);
    }


}
