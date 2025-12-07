package com.googlecode.mp4parser.boxes.threegpp26245;

import com.googlecode.mp4parser.AbstractBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class FontTableBox extends AbstractBox {
    List<FontRecord> entries;

    public FontTableBox() {
        super("ftab");
        this.entries = new LinkedList();
    }
}
