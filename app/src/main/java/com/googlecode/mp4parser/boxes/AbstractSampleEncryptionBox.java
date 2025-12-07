package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AbstractSampleEncryptionBox extends AbstractFullBox {
    int algorithmId;
    List<Entry> entries;
    int ivSize;
    byte[] kid;

    protected AbstractSampleEncryptionBox(String str) {
        super(str);
        this.algorithmId = -1;
        this.ivSize = -1;
        this.kid = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        this.entries = new LinkedList();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractSampleEncryptionBox abstractSampleEncryptionBox = (AbstractSampleEncryptionBox) obj;
        if (this.algorithmId != abstractSampleEncryptionBox.algorithmId || this.ivSize != abstractSampleEncryptionBox.ivSize) {
            return false;
        }
        if (this.entries == null ? abstractSampleEncryptionBox.entries != null : (!this.entries.equals(abstractSampleEncryptionBox.entries))) {
            return false;
        }
        return Arrays.equals(this.kid, abstractSampleEncryptionBox.kid);
    }

    public int hashCode() {
        return (((this.kid != null ? Arrays.hashCode(this.kid) : 0) + (((this.algorithmId * 31) + this.ivSize) * 31)) * 31) + (this.entries != null ? this.entries.hashCode() : 0);
    }
}
