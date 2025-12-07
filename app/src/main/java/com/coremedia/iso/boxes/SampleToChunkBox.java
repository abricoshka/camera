package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class SampleToChunkBox extends AbstractFullBox {
    List<Entry> entries;

    public SampleToChunkBox() {
        super("stsc");
        this.entries = Collections.emptyList();
    }

    public String toString() {
        return "SampleToChunkBox[entryCount=" + this.entries.size() + "]";
    }
}
