package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class DescriptionBox extends AbstractFullBox {
    private String description;
    private String language;

    public DescriptionBox() {
        super("dscp");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return "DescriptionBox[language=" + getLanguage() + ";description=" + getDescription() + "]";
    }
}
