package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class TrackFragmentBaseMediaDecodeTimeBox extends AbstractFullBox {
    private long baseMediaDecodeTime;

    public TrackFragmentBaseMediaDecodeTimeBox() {
        super("tfdt");
    }

    public String toString() {
        return "TrackFragmentBaseMediaDecodeTimeBox{baseMediaDecodeTime=" + this.baseMediaDecodeTime + '}';
    }
}
