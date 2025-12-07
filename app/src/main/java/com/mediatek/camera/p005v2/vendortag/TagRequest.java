package com.mediatek.camera.p005v2.vendortag;

import android.hardware.camera2.CaptureRequest;

/* loaded from: classes.dex */
public class TagRequest {
    public static final CaptureRequest.Key<Integer> STATISTICS_FORCE_FACE_3A = new CaptureRequest.Key<>("com.mediatek.facefeature.forceface3a", Integer.TYPE);
    public static final CaptureRequest.Key<Integer> STATISTICS_ASD_MODE = new CaptureRequest.Key<>("com.mediatek.facefeature.asdmode", Integer.TYPE);
    public static final CaptureRequest.Key<Integer> STATISTICS_3DNR_MODE = new CaptureRequest.Key<>("com.mediatek.nrfeature.3dnrmode", Integer.TYPE);
}
