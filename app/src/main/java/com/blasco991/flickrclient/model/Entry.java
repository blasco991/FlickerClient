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
    private final String title;
    private final String url;
    private final String url_preview;
    private final String tags;

    public Entry(String title, int id, String url, String url_preview, String tags) {
        this.ID = id;
        this.tags = tags;
        this.title = title;
        this.url = url;
        this.url_preview = url_preview;
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

    public String getTags() {
        return tags;
    }

    public String getUrlPreview() {
        return url_preview;
    }
}