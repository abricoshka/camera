package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class OriginalFormatBox extends AbstractBox {
    private String dataFormat;

    public OriginalFormatBox() {
        super("frma");
        this.dataFormat = "    ";
    }

    public String getDataFormat() {
        return this.dataFormat;
    }

    public String toString() {
        return "OriginalFormatBox[dataFormat=" + getDataFormat() + "]";
    }
}
