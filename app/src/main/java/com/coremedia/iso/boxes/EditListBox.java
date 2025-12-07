package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class EditListBox extends AbstractFullBox {
    private List<Entry> entries;

    public EditListBox() {
        super("elst");
        this.entries = new LinkedList();
    }

    public String toString() {
        return "EditListBox{entries=" + this.entries + '}';
    }
}
