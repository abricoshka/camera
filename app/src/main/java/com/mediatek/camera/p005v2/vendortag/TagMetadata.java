package com.mediatek.camera.p005v2.vendortag;

import android.hardware.camera2.CameraCharacteristics;

/* loaded from: classes.dex */
public class TagMetadata {
    public static final CameraCharacteristics.Key<int[]> ASD_AVAILABLE_MODES = new CameraCharacteristics.Key<>("com.mediatek.facefeature.availableasdmodes", int[].class);
    public static final CameraCharacteristics.Key<int[]> NR3D_AVAILABLE_MODES = new CameraCharacteristics.Key<>("com.mediatek.nrfeature.available3dnrmodes", int[].class);
}
