package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class SampleSizeBox extends AbstractFullBox {
    int sampleCount;
    private long sampleSize;
    private long[] sampleSizes;

    public SampleSizeBox() {
        super("stsz");
        this.sampleSizes = new long[0];
    }

    public long getSampleSize() {
        return this.sampleSize;
    }

    public long getSampleCount() {
        if (this.sampleSize > 0) {
            return this.sampleCount;
        }
        return this.sampleSizes.length;
    }

    public String toString() {
        return "SampleSizeBox[sampleSize=" + getSampleSize() + ";sampleCount=" + getSampleCount() + "]";
    }
}
