package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class ClassificationBox extends AbstractFullBox {
    private String classificationEntity;
    private String classificationInfo;
    private int classificationTableIndex;
    private String language;

    public ClassificationBox() {
        super("clsf");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getClassificationEntity() {
        return this.classificationEntity;
    }

    public int getClassificationTableIndex() {
        return this.classificationTableIndex;
    }

    public String getClassificationInfo() {
        return this.classificationInfo;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassificationBox[language=").append(getLanguage());
        sb.append("classificationEntity=").append(getClassificationEntity());
        sb.append(";classificationTableIndex=").append(getClassificationTableIndex());
        sb.append(";language=").append(getLanguage());
        sb.append(";classificationInfo=").append(getClassificationInfo());
        sb.append("]");
        return sb.toString();
    }
}
