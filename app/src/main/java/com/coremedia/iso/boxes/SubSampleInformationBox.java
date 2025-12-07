package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SubSampleInformationBox extends AbstractFullBox {
    private List<SampleEntry> entries;
    private long entryCount;

    public SubSampleInformationBox() {
        super("subs");
        this.entries = new ArrayList();
    }

    public String toString() {
        return "SubSampleInformationBox{entryCount=" + this.entryCount + ", entries=" + this.entries + '}';
    }
}
