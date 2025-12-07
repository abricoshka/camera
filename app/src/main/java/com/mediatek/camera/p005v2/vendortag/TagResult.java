package com.mediatek.camera.p005v2.vendortag;

import android.hardware.camera2.CaptureResult;

/* loaded from: classes.dex */
public class TagResult {
    public static final CaptureResult.Key<Integer> STATISTICS_ASD_MODE = new CaptureResult.Key<>("com.mediatek.facefeature.asdmode", Integer.TYPE);
    public static final CaptureResult.Key<int[]> STATISTICS_ASD_RESULT = new CaptureResult.Key<>("com.mediatek.facefeature.asdresult", int[].class);
    public static final CaptureResult.Key<Integer> STATISTICS_3DNR_MODE = new CaptureResult.Key<>("com.mediatek.nrfeature.3dnrmode", Integer.TYPE);
    public static final CaptureResult.Key<int[]> STATISTICS_3DNR_RESULT = new CaptureResult.Key<>("com.mediatek.nrfeature.3dnrmode", int[].class);
}
