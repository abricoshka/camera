package com.mediatek.camera.p005v2.control;

import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;

/* loaded from: classes.dex */
public class ControlHelper {
    public static final MeteringRectangle[] ZERO_WEIGHT_3A_REGION = {new MeteringRectangle(0, 0, 0, 0, 0)};
    private static final int CAMERA2_REGION_WEIGHT = (int) lerp(0.0f, 1000.0f, 0.022f);

    public static MeteringRectangle[] regionsForNormalizedCoord(float f, float f2, float f3, Rect rect, int i) {
        int iMin = (int) (Math.min(rect.width(), rect.height()) * 0.5f * f3);
        PointF pointFNormalizedSensorCoordsForNormalizedDisplayCoords = normalizedSensorCoordsForNormalizedDisplayCoords(f, f2, i);
        int iWidth = (int) (rect.left + (pointFNormalizedSensorCoordsForNormalizedDisplayCoords.x * rect.width()));
        int iHeight = (int) ((pointFNormalizedSensorCoordsForNormalizedDisplayCoords.y * rect.height()) + rect.top);
        Rect rect2 = new Rect(iWidth - iMin, iHeight - iMin, iWidth + iMin, iMin + iHeight);
        rect2.left = clamp(rect2.left, rect.left, rect.right);
        rect2.top = clamp(rect2.top, rect.top, rect.bottom);
        rect2.right = clamp(rect2.right, rect.left, rect.right);
        rect2.bottom = clamp(rect2.bottom, rect.top, rect.bottom);
        return new MeteringRectangle[]{new MeteringRectangle(rect2, CAMERA2_REGION_WEIGHT)};
    }

    public static MeteringRectangle[] afRegionsForNormalizedCoord(float f, float f2, Rect rect, int i) {
        return regionsForNormalizedCoord(f, f2, 0.2f, rect, i);
    }

    public static MeteringRectangle[] aeRegionsForNormalizedCoord(float f, float f2, Rect rect, int i) {
        return regionsForNormalizedCoord(f, f2, 0.3f, rect, i);
    }

    public static int clamp(int i, int i2, int i3) {
        if (i > i3) {
            return i3;
        }
        if (i < i2) {
            return i2;
        }
        return i;
    }

    public static float lerp(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    public static PointF normalizedSensorCoordsForNormalizedDisplayCoords(float f, float f2, int i) {
        switch (i) {
            case 0:
                break;
            case 90:
                break;
            case 180:
                break;
            case 270:
                break;
        }
        return new PointF(f, f2);
    }
}
