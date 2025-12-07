package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.boxes.ContainerBox;
import java.util.Arrays;

/* loaded from: classes.dex */
public class MpegSampleEntry extends SampleEntry implements ContainerBox {
    public String toString() {
        return "MpegSampleEntry" + Arrays.asList(getBoxes());
    }
}
