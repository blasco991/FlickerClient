package com.blasco991.flickrclient;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.blasco991.flickrclient.ctrl.Controller;
import com.blasco991.flickrclient.model.Model;
import com.blasco991.flickrclient.view.View;

import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by blasco991 on 11/04/17.
 */
@ThreadSafe
public class MVC {
    public final Model model;
    public final Controller controller;
    public final Context context;
    private final List<View> views = new CopyOnWriteArrayList<>();

    public MVC(Model model, Controller controller, Context context) {
        this.model = model;
        this.context = context;
        this.controller = controller;

        model.setMVC(this);
        controller.setMVC(this);
    }

    public void register(View view) {
        views.add(view);
    }

    public void unregister(View view) {
        views.remove(view);
    }

    public interface ViewTask {
        void process(View view);
    }

    public void forEachView(ViewTask task) {
        // run a Runnable in the UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            for (View view : views)
                task.process(view);
        });
    }
}