package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class AbstractTrackEncryptionBox extends AbstractFullBox {
    int defaultAlgorithmId;
    int defaultIvSize;
    byte[] default_KID;

    protected AbstractTrackEncryptionBox(String str) {
        super(str);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractTrackEncryptionBox abstractTrackEncryptionBox = (AbstractTrackEncryptionBox) obj;
        return this.defaultAlgorithmId == abstractTrackEncryptionBox.defaultAlgorithmId && this.defaultIvSize == abstractTrackEncryptionBox.defaultIvSize && Arrays.equals(this.default_KID, abstractTrackEncryptionBox.default_KID);
    }

    public int hashCode() {
        return (this.default_KID != null ? Arrays.hashCode(this.default_KID) : 0) + (((this.defaultAlgorithmId * 31) + this.defaultIvSize) * 31);
    }
}
