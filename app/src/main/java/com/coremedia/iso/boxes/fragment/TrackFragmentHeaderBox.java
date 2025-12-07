package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class TrackFragmentHeaderBox extends AbstractFullBox {
    private long baseDataOffset;
    private long defaultSampleDuration;
    private SampleFlags defaultSampleFlags;
    private long defaultSampleSize;
    private boolean durationIsEmpty;
    private long sampleDescriptionIndex;
    private long trackId;

    public TrackFragmentHeaderBox() {
        super("tfhd");
        this.baseDataOffset = -1L;
        this.defaultSampleDuration = -1L;
        this.defaultSampleSize = -1L;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrackFragmentHeaderBox");
        sb.append("{trackId=").append(this.trackId);
        sb.append(", baseDataOffset=").append(this.baseDataOffset);
        sb.append(", sampleDescriptionIndex=").append(this.sampleDescriptionIndex);
        sb.append(", defaultSampleDuration=").append(this.defaultSampleDuration);
        sb.append(", defaultSampleSize=").append(this.defaultSampleSize);
        sb.append(", defaultSampleFlags=").append(this.defaultSampleFlags);
        sb.append(", durationIsEmpty=").append(this.durationIsEmpty);
        sb.append('}');
        return sb.toString();
    }
}
