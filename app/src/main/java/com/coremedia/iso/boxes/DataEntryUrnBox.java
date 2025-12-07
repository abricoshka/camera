package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class DataEntryUrnBox extends AbstractFullBox {
    private String location;
    private String name;

    public DataEntryUrnBox() {
        super("urn ");
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String toString() {
        return "DataEntryUrlBox[name=" + getName() + ";location=" + getLocation() + "]";
    }
}
