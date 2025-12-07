package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class MovieHeaderBox extends AbstractFullBox {
    private long creationTime;
    private long duration;
    private long[] matrix;
    private long modificationTime;
    private long nextTrackId;
    private double rate;
    private long timescale;
    private float volume;

    public MovieHeaderBox() {
        super("mvhd");
        this.rate = 1.0d;
        this.volume = 1.0f;
        this.matrix = new long[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824};
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

    public double getRate() {
        return this.rate;
    }

    public float getVolume() {
        return this.volume;
    }

    public long getNextTrackId() {
        return this.nextTrackId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MovieHeaderBox[");
        sb.append("creationTime=").append(getCreationTime());
        sb.append(";");
        sb.append("modificationTime=").append(getModificationTime());
        sb.append(";");
        sb.append("timescale=").append(getTimescale());
        sb.append(";");
        sb.append("duration=").append(getDuration());
        sb.append(";");
        sb.append("rate=").append(getRate());
        sb.append(";");
        sb.append("volume=").append(getVolume());
        for (int i = 0; i < this.matrix.length; i++) {
            sb.append(";");
            sb.append("matrix").append(i).append("=").append(this.matrix[i]);
        }
        sb.append(";");
        sb.append("nextTrackId=").append(getNextTrackId());
        sb.append("]");
        return sb.toString();
    }
}
