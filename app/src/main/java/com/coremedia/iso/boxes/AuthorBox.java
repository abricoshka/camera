package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class AuthorBox extends AbstractFullBox {
    private String author;
    private String language;

    public AuthorBox() {
        super("auth");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getAuthor() {
        return this.author;
    }

    public String toString() {
        return "AuthorBox[language=" + getLanguage() + ";author=" + getAuthor() + "]";
    }
}
