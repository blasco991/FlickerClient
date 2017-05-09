package com.blasco991.flickrclient.model;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.view.View;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by blasco991 on 11/04/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;

    private LruCache<String, Bitmap> mMemoryCache;

    public Model() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @GuardedBy("itself")
    private final List<Entry> pictureInfos = Collections.synchronizedList(new LinkedList<Entry>());

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storePictureInfos(Iterable<Entry> pictureInfos) {
        this.pictureInfos.clear();
        for (Entry pi : pictureInfos)
            this.pictureInfos.add(pi);

        mvc.forEachView(View::onModelChanged);
    }

    public List<Entry> getPictureInfos() {
        return pictureInfos;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
