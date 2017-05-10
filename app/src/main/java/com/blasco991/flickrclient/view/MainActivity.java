package com.blasco991.flickrclient.view;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.blasco991.flickrclient.FlickerApp;
import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.R;


public class MainActivity extends AppCompatActivity implements com.blasco991.flickrclient.view.View {
    private MVC mvc;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mvc = ((FlickerApp) getApplication()).getMVC();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void search(View view) {
        EditText searchTerms = (EditText) findViewById(R.id.input_search_term);
        progressBar.setVisibility(View.VISIBLE);
        PicturesListActivity.start(this, searchTerms.getText().toString());
    }

    @Override
    @UiThread
    protected void onStart() {
        super.onStart();
        mvc.register(this);
    }

    @Override
    @UiThread
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    @UiThread
    protected void onStop() {
        mvc.unregister(this);
        super.onStop();
    }

    @Override
    public void onModelChanged() {
    }

    @Override
    public void onModelChanged(int entryID) {

    }
}
