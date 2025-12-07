package com.coremedia.iso.boxes.vodafone;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class ContentDistributorIdBox extends AbstractFullBox {
    private String contentDistributorId;
    private String language;

    public ContentDistributorIdBox() {
        super("cdis");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getContentDistributorId() {
        return this.contentDistributorId;
    }

    public String toString() {
        return "ContentDistributorIdBox[language=" + getLanguage() + ";contentDistributorId=" + getContentDistributorId() + "]";
    }
}
