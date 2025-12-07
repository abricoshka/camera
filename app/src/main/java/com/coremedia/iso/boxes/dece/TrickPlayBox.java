package com.coremedia.iso.boxes.dece;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TrickPlayBox extends AbstractFullBox {
    private List<Entry> entries;

    public TrickPlayBox() {
        super("trik");
        this.entries = new ArrayList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrickPlayBox");
        sb.append("{entries=").append(this.entries);
        sb.append('}');
        return sb.toString();
    }
}
