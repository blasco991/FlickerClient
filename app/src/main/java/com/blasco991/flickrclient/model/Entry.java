package com.blasco991.flickrclient.model;

import net.jcip.annotations.Immutable;

import java.io.Serializable;

/**
 * Created by blasco991 on 02/05/17.
 */

@Immutable
public class Entry {
    final String title;
    final String url;

    public Entry(String title, String url) {
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
}