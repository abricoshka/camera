package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class TrackReferenceTypeBox extends AbstractBox {
    private long[] trackIds;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrackReferenceTypeBox[type=").append(getType());
        for (int i = 0; i < this.trackIds.length; i++) {
            sb.append(";trackId");
            sb.append(i);
            sb.append("=");
            sb.append(this.trackIds[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
