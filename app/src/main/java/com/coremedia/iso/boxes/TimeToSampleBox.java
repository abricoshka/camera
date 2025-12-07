package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class TimeToSampleBox extends AbstractFullBox {
    List<Entry> entries;

    public TimeToSampleBox() {
        super("stts");
        this.entries = Collections.emptyList();
    }

    public String toString() {
        return "TimeToSampleBox[entryCount=" + this.entries.size() + "]";
    }
}
