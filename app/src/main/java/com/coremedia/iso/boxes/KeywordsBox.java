package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class KeywordsBox extends AbstractFullBox {
    private String[] keywords;
    private String language;

    public KeywordsBox() {
        super("kywd");
    }

    public String getLanguage() {
        return this.language;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("KeywordsBox[language=").append(getLanguage());
        for (int i = 0; i < this.keywords.length; i++) {
            stringBuffer.append(";keyword").append(i).append("=").append(this.keywords[i]);
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
