package com.blasco991.flickrclient.model;

import android.graphics.Bitmap;

import net.jcip.annotations.Immutable;

import java.io.Serializable;

/**
 * Created by blasco991 on 02/05/17.
 */

@Immutable
public class Entry implements Serializable {

    private int ID;
    private Bitmap preview;
    private final String title;
    private final String url;
    private final String tags;

    public Entry(String title, String url, int id, String tags) {
        this.ID = id;
        this.tags = tags;
        this.preview = null;
        this.title = title;
        this.url = url;
    }


    @Override
    public String toString() {
        return title + "\n" + url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public void setPreview(Bitmap bitmap) {
        this.preview = bitmap;
    }

    public String getTags() {
        return tags;
    }

}