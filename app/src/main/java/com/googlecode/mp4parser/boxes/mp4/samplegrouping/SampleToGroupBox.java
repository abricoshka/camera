package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SampleToGroupBox extends AbstractFullBox {
    List<Entry> entries;

    public SampleToGroupBox() {
        super("sbgp");
        this.entries = new LinkedList();
    }
}
