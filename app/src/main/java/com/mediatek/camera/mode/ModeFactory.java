package com.mediatek.camera.mode;

import android.support.v4.app.FrameMetricsAggregator;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.mode.facebeauty.FaceBeautyMode;
import com.mediatek.camera.mode.panorama.PanoramaMode;
import com.mediatek.camera.mode.pip.PipPhotoMode;
import com.mediatek.camera.mode.pip.PipVideoMode;
import com.mediatek.camera.mode.stereocamera.StereoCameraMode;
import com.mediatek.camera.mode.stereocamera.StereoPhotoMode;
import com.mediatek.camera.mode.stereocamera.StereoVideoMode;

/* loaded from: classes.dex */
public class ModeFactory {

    /* renamed from: -com-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f101commediatekcameraICameraMode$CameraModeTypeSwitchesValues = null;
    private static volatile ModeFactory sModeFactory = null;

    /* renamed from: -getcom-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m733getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues() {
        if (f101commediatekcameraICameraMode$CameraModeTypeSwitchesValues != null) {
            return f101commediatekcameraICameraMode$CameraModeTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.CameraModeType.valuesCustom().length];
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PANORAMA.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_STEREO.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_SLOW_MOTION.ordinal()] = 10;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_STEREO_CAMERA.ordinal()] = 6;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO.ordinal()] = 7;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP.ordinal()] = 8;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO.ordinal()] = 9;
        } catch (NoSuchFieldError e10) {
        }
        f101commediatekcameraICameraMode$CameraModeTypeSwitchesValues = iArr;
        return iArr;
    }

    private ModeFactory() {
    }

    public static ModeFactory getInstance() {
        if (sModeFactory == null) {
            synchronized (ModeFactory.class) {
                if (sModeFactory == null) {
                    sModeFactory = new ModeFactory();
                }
            }
        }
        return sModeFactory;
    }

    public ICameraMode createMode(ICameraMode.CameraModeType cameraModeType, ICameraContext iCameraContext) {
        switch (m733getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues()[cameraModeType.ordinal()]) {
            case 1:
                return new FaceBeautyMode(iCameraContext);
            case 2:
                return new PanoramaMode(iCameraContext);
            case 3:
                return new PhotoMode(iCameraContext);
            case 4:
                return new PipPhotoMode(iCameraContext);
            case 5:
                return new StereoPhotoMode(iCameraContext);
            case 6:
                return new StereoCameraMode(iCameraContext);
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return new VideoMode(iCameraContext);
            case 8:
                return new PipVideoMode(iCameraContext);
            case 9:
                return new StereoVideoMode(iCameraContext);
            default:
                return new DummyMode();
        }
    }
}
