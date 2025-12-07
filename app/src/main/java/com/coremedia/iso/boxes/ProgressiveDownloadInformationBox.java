package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ProgressiveDownloadInformationBox extends AbstractFullBox {
    List<Entry> entries;

    public ProgressiveDownloadInformationBox() {
        super("pdin");
        this.entries = Collections.emptyList();
    }

    public String toString() {
        return "ProgressiveDownloadInfoBox{entries=" + this.entries + '}';
    }
}
