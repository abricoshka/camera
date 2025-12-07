package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ItemDataBox extends AbstractBox {
    ByteBuffer data;

    public ItemDataBox() {
        super("idat");
        this.data = ByteBuffer.allocate(0);
    }
}
