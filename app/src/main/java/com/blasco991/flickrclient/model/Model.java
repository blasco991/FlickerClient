package com.blasco991.flickrclient.model;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.view.View;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;

/**
 * Created by blasco991 on 11/04/17.
 */

@ThreadSafe
public class Model {
    private MVC mvc;

    @GuardedBy("itself")
    private final LinkedList<Entry> pictureInfos = new LinkedList<>();

    public void setMVC(MVC mvc) {
        this.mvc = mvc;
    }

    public void storePictureInfos(Iterable<Entry> pictureInfos) {
        synchronized (this.pictureInfos) {
            this.pictureInfos.clear();
            for (Entry pi : pictureInfos)
                this.pictureInfos.add(pi);
        }

        mvc.forEachView(View::onModelChanged);
    }

    public Entry[] getPictureInfos() {
        synchronized (pictureInfos) {
            return pictureInfos.toArray(new Entry[pictureInfos.size()]);
        }
    }

}
