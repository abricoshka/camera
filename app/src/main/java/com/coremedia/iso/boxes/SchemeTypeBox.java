package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class SchemeTypeBox extends AbstractFullBox {
    String schemeType;
    String schemeUri;

    public SchemeTypeBox() {
        super("schm");
        this.schemeType = "    ";
        this.schemeUri = null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Schema Type Box[");
        sb.append("schemeUri=").append(this.schemeUri).append("; ");
        sb.append("schemeType=").append(this.schemeType).append("; ");
        sb.append("schemeVersion=").append(this.schemeUri).append("; ");
        sb.append("]");
        return sb.toString();
    }
}
