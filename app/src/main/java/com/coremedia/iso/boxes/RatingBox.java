package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class RatingBox extends AbstractFullBox {
    private String language;
    private String ratingCriteria;
    private String ratingEntity;
    private String ratingInfo;

    public RatingBox() {
        super("rtng");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getRatingEntity() {
        return this.ratingEntity;
    }

    public String getRatingCriteria() {
        return this.ratingCriteria;
    }

    public String getRatingInfo() {
        return this.ratingInfo;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RatingBox[language=").append(getLanguage());
        sb.append("ratingEntity=").append(getRatingEntity());
        sb.append(";ratingCriteria=").append(getRatingCriteria());
        sb.append(";language=").append(getLanguage());
        sb.append(";ratingInfo=").append(getRatingInfo());
        sb.append("]");
        return sb.toString();
    }
}
