package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TfrfBox extends AbstractFullBox {
    public List<Entry> entries;

    public TfrfBox() {
        super("uuid");
        this.entries = new ArrayList();
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public byte[] getUserType() {
        return new byte[]{-44, -128, 126, -14, -54, 57, 70, -107, -114, 84, 38, -53, -98, 70, -89, -97};
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TfrfBox");
        sb.append("{entries=").append(this.entries);
        sb.append('}');
        return sb.toString();
    }
}
