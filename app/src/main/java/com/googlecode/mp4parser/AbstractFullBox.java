package com.googlecode.mp4parser;

import com.coremedia.iso.boxes.FullBox;

/* loaded from: classes.dex */
public abstract class AbstractFullBox extends AbstractBox implements FullBox {
    private int flags;
    private int version;

    protected AbstractFullBox(String str) {
        super(str);
    }

    protected AbstractFullBox(String str, byte[] bArr) {
        super(str, bArr);
    }

    public void setVersion(int i) {
        this.version = i;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int i) {
        this.flags = i;
    }
}
