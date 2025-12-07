package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class CompositionTimeToSample extends AbstractFullBox {
    List<Entry> entries;

    public CompositionTimeToSample() {
        super("ctts");
        this.entries = Collections.emptyList();
    }
}
