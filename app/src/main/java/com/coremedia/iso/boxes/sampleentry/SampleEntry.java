package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class SampleEntry extends AbstractBox implements ContainerBox {
    protected List<Box> boxes;
    private int dataReferenceIndex;

    protected SampleEntry(String str) {
        super(str);
        this.dataReferenceIndex = 1;
        this.boxes = new LinkedList();
    }

    public List<Box> getBoxes() {
        return this.boxes;
    }
}
