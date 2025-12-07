package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SampleAuxiliaryInformationSizesBox extends AbstractFullBox {
    private String auxInfoType;
    private String auxInfoTypeParameter;
    private int defaultSampleInfoSize;
    private int sampleCount;
    private List<Short> sampleInfoSizes;

    public SampleAuxiliaryInformationSizesBox() {
        super("saiz");
        this.sampleInfoSizes = new LinkedList();
    }

    public String toString() {
        return "SampleAuxiliaryInformationSizesBox{defaultSampleInfoSize=" + this.defaultSampleInfoSize + ", sampleCount=" + this.sampleCount + ", auxInfoType='" + this.auxInfoType + "', auxInfoTypeParameter='" + this.auxInfoTypeParameter + "'}";
    }
}
