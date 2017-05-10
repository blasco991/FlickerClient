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
import com.blasco991.flickrclient.model.Entry;

public class PicturesListActivity extends Activity implements com.blasco991.flickrclient.view.View {
    private final static String TAG = PicturesListActivity.class.getName();
    private final static String PARAM_SEARCH_STRING = TAG + ".search_string";

    private MVC mvc;
    private RecyclerView.Adapter mAdapter;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1500;


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
        mRecyclerView.addOnItemTouchListener(new PictureInfoAdapter.RecyclerItemClickListener(this, mRecyclerView, new PictureInfoAdapter.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(android.view.View view, int position) {
                long now = System.currentTimeMillis(); //onDblClick
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    Intent intent = new Intent(PicturesListActivity.this, ViewImageActivity.class);
                    Entry entry = ((Entry) view.findViewById(R.id.imageView).getTag());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("entry", entry);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
                Log.d(TAG, now - mLastClickTime + "");
                mLastClickTime = now;

            }

            @Override
            public void onLongItemClick(android.view.View view, int position) {
            }
        }));

        mvc.controller.fetchPictureInfos(getIntent().getStringExtra(PARAM_SEARCH_STRING));
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
