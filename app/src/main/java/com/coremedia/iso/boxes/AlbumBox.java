package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class AlbumBox extends AbstractFullBox {
    private String albumTitle;
    private String language;
    private int trackNumber;

    public AlbumBox() {
        super("albm");
    }

    public String getLanguage() {
        return this.language;
    }

    public String getAlbumTitle() {
        return this.albumTitle;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlbumBox[language=").append(getLanguage()).append(";");
        sb.append("albumTitle=").append(getAlbumTitle());
        if (this.trackNumber >= 0) {
            sb.append(";trackNumber=").append(getTrackNumber());
        }
        sb.append("]");
        return sb.toString();
    }
}
