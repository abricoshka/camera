package com.coremedia.iso.boxes.vodafone;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class CoverUriBox extends AbstractFullBox {
    private String coverUri;

    public CoverUriBox() {
        super("cvru");
    }

    public String getCoverUri() {
        return this.coverUri;
    }

    public String toString() {
        return "CoverUriBox[coverUri=" + getCoverUri() + "]";
    }
}
