package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class SyncSampleBox extends AbstractFullBox {
    private long[] sampleNumber;

    public SyncSampleBox() {
        super("stss");
    }

    public String toString() {
        return "SyncSampleBox[entryCount=" + this.sampleNumber.length + "]";
    }
}
