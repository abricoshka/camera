package com.googlecode.mp4parser.boxes.threegpp26244;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SegmentIndexBox extends AbstractFullBox {
    List<Entry> entries;

    public SegmentIndexBox() {
        super("sidx");
        this.entries = new ArrayList();
    }
}
