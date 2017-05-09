package com.blasco991.flickrclient.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.blasco991.flickrclient.R;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewFull);

        String imageURL = getIntent().getStringExtra("imageURL");
    }
}
