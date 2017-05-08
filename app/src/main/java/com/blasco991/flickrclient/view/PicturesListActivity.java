package com.blasco991.flickrclient.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.blasco991.flickrclient.FlickerApp;
import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.R;

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
        Log.d(TAG, "onCreate");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new PictureInfoAdapter(mvc.model.getPictureInfos());
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.hasFixedSize();
        Log.d(TAG, "onCreate setAdapter");

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


}
