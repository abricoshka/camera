package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class XmlBox extends AbstractFullBox {
    String xml;

    public XmlBox() {
        super("xml ");
        this.xml = "";
    }

    public String toString() {
        return "XmlBox{xml='" + this.xml + "'}";
    }
}
