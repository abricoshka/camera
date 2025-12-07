package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class FileTypeBox extends AbstractBox {
    private List<String> compatibleBrands;
    private String majorBrand;
    private long minorVersion;

    public FileTypeBox() {
        super("ftyp");
        this.compatibleBrands = Collections.emptyList();
    }

    public String getMajorBrand() {
        return this.majorBrand;
    }

    public long getMinorVersion() {
        return this.minorVersion;
    }

    @DoNotParseDetail
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileTypeBox[");
        sb.append("majorBrand=").append(getMajorBrand());
        sb.append(";");
        sb.append("minorVersion=").append(getMinorVersion());
        for (String str : this.compatibleBrands) {
            sb.append(";");
            sb.append("compatibleBrand=").append(str);
        }
        sb.append("]");
        return sb.toString();
    }
}
