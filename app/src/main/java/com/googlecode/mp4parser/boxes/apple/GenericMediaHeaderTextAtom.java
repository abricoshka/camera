package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class GenericMediaHeaderTextAtom extends AbstractBox {
    int unknown_1;
    int unknown_5;
    int unknown_9;

    public GenericMediaHeaderTextAtom() {
        super("text");
        this.unknown_1 = 65536;
        this.unknown_5 = 65536;
        this.unknown_9 = 1073741824;
    }
}
