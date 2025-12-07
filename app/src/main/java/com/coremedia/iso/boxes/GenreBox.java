package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class GenreBox extends AbstractFullBox {
    private String genre;
    private String language;

    public GenreBox() {
        super("gnre");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getGenre() {
        return this.genre;
    }

    public String toString() {
        return "GenreBox[language=" + getLanguage() + ";genre=" + getGenre() + "]";
    }
}
