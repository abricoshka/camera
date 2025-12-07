package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.boxes.AbstractTrackEncryptionBox;

/* loaded from: classes.dex */
public class PiffTrackEncryptionBox extends AbstractTrackEncryptionBox {
    public PiffTrackEncryptionBox() {
        super("uuid");
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public byte[] getUserType() {
        return new byte[]{-119, 116, -37, -50, 123, -25, 76, 81, -124, -7, 113, 72, -7, -120, 37, 84};
    }

    @Override // com.googlecode.mp4parser.AbstractFullBox
    public int getFlags() {
        return 0;
    }
}
