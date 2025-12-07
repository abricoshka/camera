package com.android.camera.bridge;

import android.app.Activity;
import com.mediatek.camera.addition.continuousshot.CsView;
import com.mediatek.camera.addition.effect.EffectView;
import com.mediatek.camera.mode.facebeauty.FaceBeautyView;
import com.mediatek.camera.mode.panorama.PanoramaView;
import com.mediatek.camera.mode.pip.PipView;
import com.mediatek.camera.mode.stereocamera.StereoView;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraView;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class ViewFactory {

    /* renamed from: -com-mediatek-camera-platform-ICameraAppUi$SpecViewTypeSwitchesValues */
    private static final /* synthetic */ int[] f62x572093cb = null;
    private static volatile ViewFactory sViewFactory = null;

    /* renamed from: -getcom-mediatek-camera-platform-ICameraAppUi$SpecViewTypeSwitchesValues */
    private static /* synthetic */ int[] m16x2fc69a7() {
        if (f62x572093cb != null) {
            return f62x572093cb;
        }
        int[] iArr = new int[ICameraAppUi.SpecViewType.valuesCustom().length];
        try {
            iArr[ICameraAppUi.SpecViewType.ADDITION_CONTINUE_SHOT.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.ADDITION_EFFECT.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.ADDITION_OBJECT_TRACKING.ordinal()] = 7;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.MODE_FACE_BEAUTY.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.MODE_PANORAMA.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.MODE_PIP.ordinal()] = 5;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.MODE_SLOW_MOTION.ordinal()] = 8;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraAppUi.SpecViewType.MODE_STEREO.ordinal()] = 6;
        } catch (NoSuchFieldError e8) {
        }
        f62x572093cb = iArr;
        return iArr;
    }

    private ViewFactory() {
    }

    public static ViewFactory getInstance() {
        if (sViewFactory == null) {
            synchronized (ViewFactory.class) {
                if (sViewFactory == null) {
                    sViewFactory = new ViewFactory();
                }
            }
        }
        return sViewFactory;
    }

    public ICameraView createViewManager(Activity activity, ICameraAppUi.SpecViewType specViewType) {
        Log.m34i("ViewFactory", "[createViewManager]type = " + specViewType);
        switch (m16x2fc69a7()[specViewType.ordinal()]) {
            case 1:
                return new CsView(activity);
            case 2:
                return new EffectView(activity);
            case 3:
                return new FaceBeautyView(activity);
            case 4:
                return new PanoramaView(activity);
            case 5:
                return new PipView(activity);
            case 6:
                return new StereoView(activity);
            default:
                return null;
        }
    }
}
