package com.blasco991.flickrclient.model;

import com.blasco991.flickrclient.MVC;
import com.blasco991.flickrclient.view.View;

import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by blasco991 on 11/04/17.
 */

@ThreadSafe
public class Model {

    private MVC mvc;
    private final List<Entry> pictureInfos = new ArrayList<>(100);

    public Model() {
    }

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
        return Collections.unmodifiableList(pictureInfos);
    }


}
