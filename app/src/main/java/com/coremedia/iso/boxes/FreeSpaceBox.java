package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class FreeSpaceBox extends AbstractBox {
    byte[] data;

    public FreeSpaceBox() {
        super("skip");
    }

    public String toString() {
        return "FreeSpaceBox[size=" + this.data.length + ";type=" + getType() + "]";
    }
}
