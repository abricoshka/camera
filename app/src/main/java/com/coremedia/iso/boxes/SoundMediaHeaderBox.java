package com.coremedia.iso.boxes;

/* loaded from: classes.dex */
public class SoundMediaHeaderBox extends AbstractMediaHeaderBox {
    private float balance;

    public SoundMediaHeaderBox() {
        super("smhd");
    }

    public float getBalance() {
        return this.balance;
    }

    public String toString() {
        return "SoundMediaHeaderBox[balance=" + getBalance() + "]";
    }
}
