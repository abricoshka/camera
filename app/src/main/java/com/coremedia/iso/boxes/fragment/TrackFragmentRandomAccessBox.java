package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class TrackFragmentRandomAccessBox extends AbstractFullBox {
    private List<Entry> entries;
    private int lengthSizeOfSampleNum;
    private int lengthSizeOfTrafNum;
    private int lengthSizeOfTrunNum;
    private long trackId;

    public TrackFragmentRandomAccessBox() {
        super("tfra");
        this.lengthSizeOfTrafNum = 2;
        this.lengthSizeOfTrunNum = 2;
        this.lengthSizeOfSampleNum = 2;
        this.entries = Collections.emptyList();
    }

    public String toString() {
        return "TrackFragmentRandomAccessBox{trackId=" + this.trackId + ", entries=" + this.entries + '}';
    }
}
