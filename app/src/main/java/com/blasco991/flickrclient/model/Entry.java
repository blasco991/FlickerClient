package com.blasco991.flickrclient.model;

import java.io.Serializable;

/**
 * Created by blasco991 on 02/05/17.
 */

public class Entry implements Serializable {

    private final String name;
    private final String url;

    public Entry(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String toString() {
        return "Title:\t" + this.name + "\nURL:\t" + this.url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
