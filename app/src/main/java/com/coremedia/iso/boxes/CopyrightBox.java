package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class CopyrightBox extends AbstractFullBox {
    private String copyright;
    private String language;

    public CopyrightBox() {
        super("cprt");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public String toString() {
        return "CopyrightBox[language=" + getLanguage() + ";copyright=" + getCopyright() + "]";
    }
}
