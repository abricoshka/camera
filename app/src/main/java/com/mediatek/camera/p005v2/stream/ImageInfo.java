package com.mediatek.camera.p005v2.stream;

/* loaded from: classes.dex */
public class ImageInfo {
    private byte[] mData;
    private int mFormat;
    private int mHeigth;
    private int mWidth;

    public ImageInfo(byte[] bArr, int i, int i2, int i3) {
        this.mData = bArr;
        this.mWidth = i;
        this.mHeigth = i2;
        this.mFormat = i3;
    }

    public byte[] getData() {
        return this.mData;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeigth() {
        return this.mHeigth;
    }

    public int getFormat() {
        return this.mFormat;
    }
}
