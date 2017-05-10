package com.blasco991.flickrclient;

import android.app.Application;

import com.blasco991.flickrclient.ctrl.Controller;
import com.blasco991.flickrclient.model.Model;

/**
 * Created by blasco991 on 02/05/17.
 */

public class FlickerApp extends Application {

    private MVC mvc;

    @Override
    public void onCreate() {
        super.onCreate();
        mvc = new MVC(new Model(), new Controller(), getApplicationContext());
    }

    public MVC getMVC() {
        return mvc;
    }
}
