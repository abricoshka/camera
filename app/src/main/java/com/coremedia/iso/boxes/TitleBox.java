package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class TitleBox extends AbstractFullBox {
    private String language;
    private String title;

    public TitleBox() {
        super("titl");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return "TitleBox[language=" + getLanguage() + ";title=" + getTitle() + "]";
    }
}
