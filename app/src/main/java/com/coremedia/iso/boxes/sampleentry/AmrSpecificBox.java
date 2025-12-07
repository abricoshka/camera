package com.coremedia.iso.boxes.sampleentry;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class AmrSpecificBox extends AbstractBox {
    private int decoderVersion;
    private int framesPerSample;
    private int modeChangePeriod;
    private int modeSet;
    private String vendor;

    public AmrSpecificBox() {
        super("damr");
    }

    public String getVendor() {
        return this.vendor;
    }

    public int getDecoderVersion() {
        return this.decoderVersion;
    }

    public int getModeSet() {
        return this.modeSet;
    }

    public int getModeChangePeriod() {
        return this.modeChangePeriod;
    }

    public int getFramesPerSample() {
        return this.framesPerSample;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AmrSpecificBox[vendor=").append(getVendor());
        sb.append(";decoderVersion=").append(getDecoderVersion());
        sb.append(";modeSet=").append(getModeSet());
        sb.append(";modeChangePeriod=").append(getModeChangePeriod());
        sb.append(";framesPerSample=").append(getFramesPerSample());
        sb.append("]");
        return sb.toString();
    }
}
