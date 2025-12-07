package com.mediatek.camera.p005v2.detection.facedetection;

import android.graphics.Point;
import android.graphics.Rect;

/* loaded from: classes.dex */
public interface IFdPresenterListener {
    void onFaceDetected(int[] iArr, Rect[] rectArr, byte[] bArr, Point[][] pointArr, Rect rect);
}
