package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class ItemLocationBox extends AbstractFullBox {
    public int baseOffsetSize;
    public int indexSize;
    public List<Item> items;
    public int lengthSize;
    public int offsetSize;

    public ItemLocationBox() {
        super("iloc");
        this.offsetSize = 8;
        this.lengthSize = 8;
        this.baseOffsetSize = 8;
        this.indexSize = 0;
        this.items = new LinkedList();
    }
}
