package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class MovieFragmentHeaderBox extends AbstractFullBox {
    private long sequenceNumber;

    public MovieFragmentHeaderBox() {
        super("mfhd");
    }

    public String toString() {
        return "MovieFragmentHeaderBox{sequenceNumber=" + this.sequenceNumber + '}';
    }
}
