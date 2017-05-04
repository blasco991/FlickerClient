package com.blasco991.flickrclient.model;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.view.View;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;

/**
 * Created by blasco991 on 11/04/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;

    @GuardedBy("itself")
    private final LinkedList<PictureInfo> pictureInfos = new LinkedList<>();

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storePictureInfos(Iterable<PictureInfo> pictureInfos) {
        synchronized (this.pictureInfos) {
            this.pictureInfos.clear();
            for (PictureInfo pi : pictureInfos)
                this.pictureInfos.add(pi);
        }

        mvc.forEachView(View::onModelChanged);
    }

    public PictureInfo[] getPictureInfos() {
        synchronized (pictureInfos) {
            return pictureInfos.toArray(new PictureInfo[pictureInfos.size()]);
        }
    }

    @Immutable
    public static class PictureInfo {
        final String title;
        final String url;

        public PictureInfo(String title, String url) {
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
}
