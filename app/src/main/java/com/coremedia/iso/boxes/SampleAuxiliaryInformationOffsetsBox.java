package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SampleAuxiliaryInformationOffsetsBox extends AbstractFullBox {
    private List<Long> offsets;

    public SampleAuxiliaryInformationOffsetsBox() {
        super("saio");
        this.offsets = new LinkedList();
    }
}
