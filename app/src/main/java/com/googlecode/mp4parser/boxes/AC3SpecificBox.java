package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class AC3SpecificBox extends AbstractBox {
    int acmod;
    int bitRateCode;
    int bsid;
    int bsmod;
    int fscod;
    int lfeon;
    int reserved;

    public AC3SpecificBox() {
        super("dac3");
    }

    public String toString() {
        return "AC3SpecificBox{fscod=" + this.fscod + ", bsid=" + this.bsid + ", bsmod=" + this.bsmod + ", acmod=" + this.acmod + ", lfeon=" + this.lfeon + ", bitRateCode=" + this.bitRateCode + ", reserved=" + this.reserved + '}';
    }
}
