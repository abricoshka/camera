package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class TfxdBox extends AbstractFullBox {
    public TfxdBox() {
        super("uuid");
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public byte[] getUserType() {
        return new byte[]{109, 29, -101, 5, 66, -43, 68, -26, -128, -30, 20, 29, -81, -9, 87, -78};
    }
}
