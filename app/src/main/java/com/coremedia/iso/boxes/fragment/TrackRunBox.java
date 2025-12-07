package com.coremedia.iso.boxes.fragment;

import android.support.v4.app.NotificationCompat;
import com.googlecode.mp4parser.AbstractFullBox;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class TrackRunBox extends AbstractFullBox {
    private int dataOffset;
    private List<Entry> entries;
    private SampleFlags firstSampleFlags;

    public TrackRunBox() {
        super("trun");
        this.entries = new ArrayList();
    }

    public boolean isDataOffsetPresent() {
        return (getFlags() & 1) == 1;
    }

    public boolean isSampleSizePresent() {
        return (getFlags() & NotificationCompat.FLAG_GROUP_SUMMARY) == 512;
    }

    public boolean isSampleDurationPresent() {
        return (getFlags() & 256) == 256;
    }

    public boolean isSampleFlagsPresent() {
        return (getFlags() & 1024) == 1024;
    }

    public boolean isSampleCompositionTimeOffsetPresent() {
        return (getFlags() & 2048) == 2048;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrackRunBox");
        sb.append("{sampleCount=").append(this.entries.size());
        sb.append(", dataOffset=").append(this.dataOffset);
        sb.append(", dataOffsetPresent=").append(isDataOffsetPresent());
        sb.append(", sampleSizePresent=").append(isSampleSizePresent());
        sb.append(", sampleDurationPresent=").append(isSampleDurationPresent());
        sb.append(", sampleFlagsPresentPresent=").append(isSampleFlagsPresent());
        sb.append(", sampleCompositionTimeOffsetPresent=").append(isSampleCompositionTimeOffsetPresent());
        sb.append(", firstSampleFlags=").append(this.firstSampleFlags);
        sb.append('}');
        return sb.toString();
    }
}
