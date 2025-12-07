package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.boxes.sampleentry.SampleEntry;

/* loaded from: classes.dex */
public class QuicktimeTextSampleEntry extends SampleEntry {
    String fontName;
    int foregroundB;
    int foregroundG;
    int foregroundR;

    public QuicktimeTextSampleEntry() {
        super("text");
        this.foregroundR = 65535;
        this.foregroundG = 65535;
        this.foregroundB = 65535;
        this.fontName = "";
    }
}
