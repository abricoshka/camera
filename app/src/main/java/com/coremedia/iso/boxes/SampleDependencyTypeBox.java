package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SampleDependencyTypeBox extends AbstractFullBox {
    private List<Entry> entries;

    public SampleDependencyTypeBox() {
        super("sdtp");
        this.entries = new ArrayList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SampleDependencyTypeBox");
        sb.append("{entries=").append(this.entries);
        sb.append('}');
        return sb.toString();
    }
}
