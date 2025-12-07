package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class UserBox extends AbstractBox {
    byte[] data;

    public String toString() {
        return "UserBox[type=" + getType() + ";userType=" + new String(getUserType()) + ";contentLength=" + this.data.length + "]";
    }
}
