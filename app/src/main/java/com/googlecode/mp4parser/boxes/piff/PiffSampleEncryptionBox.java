package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox;

/* loaded from: classes.dex */
public class PiffSampleEncryptionBox extends AbstractSampleEncryptionBox {
    public PiffSampleEncryptionBox() {
        super("uuid");
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public byte[] getUserType() {
        return new byte[]{-94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12};
    }
}
