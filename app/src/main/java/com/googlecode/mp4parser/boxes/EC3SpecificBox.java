package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class EC3SpecificBox extends AbstractBox {
    List<Entry> entries;

    public EC3SpecificBox() {
        super("dec3");
        this.entries = new LinkedList();
    }
}
