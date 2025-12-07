package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class TrackHeaderBox extends AbstractFullBox {
    private int alternateGroup;
    private long creationTime;
    private long duration;
    private double height;
    private int layer;
    private long[] matrix;
    private long modificationTime;
    private long trackId;
    private float volume;
    private double width;

    public TrackHeaderBox() {
        super("tkhd");
        this.matrix = new long[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824};
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getModificationTime() {
        return this.modificationTime;
    }

    public long getTrackId() {
        return this.trackId;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getLayer() {
        return this.layer;
    }

    public int getAlternateGroup() {
        return this.alternateGroup;
    }

    public float getVolume() {
        return this.volume;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrackHeaderBox[");
        sb.append("creationTime=").append(getCreationTime());
        sb.append(";");
        sb.append("modificationTime=").append(getModificationTime());
        sb.append(";");
        sb.append("trackId=").append(getTrackId());
        sb.append(";");
        sb.append("duration=").append(getDuration());
        sb.append(";");
        sb.append("layer=").append(getLayer());
        sb.append(";");
        sb.append("alternateGroup=").append(getAlternateGroup());
        sb.append(";");
        sb.append("volume=").append(getVolume());
        for (int i = 0; i < this.matrix.length; i++) {
            sb.append(";");
            sb.append("matrix").append(i).append("=").append(this.matrix[i]);
        }
        sb.append(";");
        sb.append("width=").append(getWidth());
        sb.append(";");
        sb.append("height=").append(getHeight());
        sb.append("]");
        return sb.toString();
    }
}
