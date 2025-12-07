package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;

/* loaded from: classes.dex */
public class MetaBox extends AbstractContainerBox {
    private int flags;
    private int version;

    public MetaBox() {
        super("meta");
        this.version = 0;
        this.flags = 0;
    }
}
