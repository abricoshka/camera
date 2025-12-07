package com.coremedia.iso.boxes;

/* loaded from: classes.dex */
public class HintMediaHeaderBox extends AbstractMediaHeaderBox {
    private long avgBitrate;
    private int avgPduSize;
    private long maxBitrate;
    private int maxPduSize;

    public HintMediaHeaderBox() {
        super("hmhd");
    }

    public String toString() {
        return "HintMediaHeaderBox{maxPduSize=" + this.maxPduSize + ", avgPduSize=" + this.avgPduSize + ", maxBitrate=" + this.maxBitrate + ", avgBitrate=" + this.avgBitrate + '}';
    }
}
