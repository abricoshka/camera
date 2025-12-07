package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class PerformerBox extends AbstractFullBox {
    private String language;
    private String performer;

    public PerformerBox() {
        super("perf");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getPerformer() {
        return this.performer;
    }

    public String toString() {
        return "PerformerBox[language=" + getLanguage() + ";performer=" + getPerformer() + "]";
    }
}
