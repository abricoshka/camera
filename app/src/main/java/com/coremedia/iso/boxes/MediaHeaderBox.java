package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class MediaHeaderBox extends AbstractFullBox {
    private long creationTime;
    private long duration;
    private String language;
    private long modificationTime;
    private long timescale;

    public MediaHeaderBox() {
        super("mdhd");
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getModificationTime() {
        return this.modificationTime;
    }

    public long getTimescale() {
        return this.timescale;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getLanguage() {
        return this.language;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MediaHeaderBox[");
        sb.append("creationTime=").append(getCreationTime());
        sb.append(";");
        sb.append("modificationTime=").append(getModificationTime());
        sb.append(";");
        sb.append("timescale=").append(getTimescale());
        sb.append(";");
        sb.append("duration=").append(getDuration());
        sb.append(";");
        sb.append("language=").append(getLanguage());
        sb.append("]");
        return sb.toString();
    }
}
