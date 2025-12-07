package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class BaseMediaInfoAtom extends AbstractFullBox {
    short balance;
    short graphicsMode;
    int opColorB;
    int opColorG;
    int opColorR;
    short reserved;

    public BaseMediaInfoAtom() {
        super("gmin");
        this.graphicsMode = (short) 64;
        this.opColorR = 32768;
        this.opColorG = 32768;
        this.opColorB = 32768;
    }

    public String toString() {
        return "BaseMediaInfoAtom{graphicsMode=" + ((int) this.graphicsMode) + ", opColorR=" + this.opColorR + ", opColorG=" + this.opColorG + ", opColorB=" + this.opColorB + ", balance=" + ((int) this.balance) + ", reserved=" + ((int) this.reserved) + '}';
    }
}
