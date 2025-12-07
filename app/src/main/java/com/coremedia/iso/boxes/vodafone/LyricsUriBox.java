package com.coremedia.iso.boxes.vodafone;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class LyricsUriBox extends AbstractFullBox {
    private String lyricsUri;

    public LyricsUriBox() {
        super("lrcu");
    }

    public String getLyricsUri() {
        return this.lyricsUri;
    }

    public String toString() {
        return "LyricsUriBox[lyricsUri=" + getLyricsUri() + "]";
    }
}
