package com.coremedia.iso.boxes.vodafone;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class AlbumArtistBox extends AbstractFullBox {
    private String albumArtist;
    private String language;

    public AlbumArtistBox() {
        super("albr");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public String toString() {
        return "AlbumArtistBox[language=" + getLanguage() + ";albumArtist=" + getAlbumArtist() + "]";
    }
}
