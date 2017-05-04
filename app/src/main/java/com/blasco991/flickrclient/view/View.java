package com.blasco991.flickrclient.view;

import android.content.Context;
import android.support.annotation.UiThread;
import android.widget.Toast;

/**
 * Created by blasco991 on 11/04/17.
 */
public interface View {
    @UiThread
    void onModelChanged();

    @UiThread
    default void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    default void makeToast(Context context, String text, int length) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
