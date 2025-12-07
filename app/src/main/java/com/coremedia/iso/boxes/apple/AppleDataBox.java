package com.coremedia.iso.boxes.apple;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public final class AppleDataBox extends AbstractFullBox {
    private byte[] data;
    private byte[] fourBytes;

    private static AppleDataBox getEmpty() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setVersion(0);
        appleDataBox.setFourBytes(new byte[4]);
        return appleDataBox;
    }

    public static AppleDataBox getStringAppleDataBox() {
        AppleDataBox empty = getEmpty();
        empty.setFlags(1);
        empty.setData(new byte[]{0});
        return empty;
    }

    public static AppleDataBox getUint8AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0});
        return appleDataBox;
    }

    public static AppleDataBox getUint16AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0, 0});
        return appleDataBox;
    }

    public static AppleDataBox getUint32AppleDataBox() {
        AppleDataBox appleDataBox = new AppleDataBox();
        appleDataBox.setFlags(21);
        appleDataBox.setData(new byte[]{0, 0, 0, 0});
        return appleDataBox;
    }

    public AppleDataBox() {
        super("data");
        this.fourBytes = new byte[4];
    }

    public void setData(byte[] bArr) {
        this.data = new byte[bArr.length];
        System.arraycopy(bArr, 0, this.data, 0, bArr.length);
    }

    public void setFourBytes(byte[] bArr) {
        System.arraycopy(bArr, 0, this.fourBytes, 0, 4);
    }

    public byte[] getData() {
        return this.data;
    }
}
