package com.mediatek.camera;

import android.support.v4.app.FrameMetricsAggregator;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.addition.Asd;
import com.mediatek.camera.addition.DistanceInfo;
import com.mediatek.camera.addition.continuousshot.ContinuousShot;
import com.mediatek.camera.addition.effect.EffectAddition;
import com.mediatek.camera.addition.remotecamera.RemoteCameraAddition;
import com.mediatek.camera.addition.thermalthrottle.ThermalThrottle;
import com.mediatek.camera.util.Log;
import java.util.Iterator;
import java.util.Vector;

/* loaded from: classes.dex */
public class AdditionManager {

    /* renamed from: -com-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f95commediatekcameraICameraMode$CameraModeTypeSwitchesValues = null;
    private final ICameraAddition mDistanceInfo;
    private final ICameraAddition mIContinuousShotAddition;
    private ICameraAddition mIEffect;
    private Vector<ICameraAddition> mModeAddition;
    private final ICameraAddition mRemoteCameraAddition;
    private Vector<ICameraAddition> mNormalAddition = new Vector<>();
    private Vector<ICameraAddition> mPhotoAddtion = new Vector<>();
    private Vector<ICameraAddition> mVideoAddtion = new Vector<>();
    private Vector<ICameraAddition> mPipPhotoAddition = new Vector<>();
    private Vector<ICameraAddition> mFaceBeautyAddition = new Vector<>();
    private Vector<ICameraAddition> mPipVideoAddition = new Vector<>();
    private Vector<ICameraAddition> mRefocusAddition = new Vector<>();
    private Vector<ICameraAddition> mDenoiseAddition = new Vector<>();
    private Vector<ICameraAddition> mDummyAddtion = new Vector<>();

    /* renamed from: -getcom-mediatek-camera-ICameraMode$CameraModeTypeSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m570getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues() {
        if (f95commediatekcameraICameraMode$CameraModeTypeSwitchesValues != null) {
            return f95commediatekcameraICameraMode$CameraModeTypeSwitchesValues;
        }
        int[] iArr = new int[ICameraMode.CameraModeType.valuesCustom().length];
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_FACE_BEAUTY.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PANORAMA.ordinal()] = 8;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_PIP.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_PHOTO_STEREO.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_SLOW_MOTION.ordinal()] = 9;
        } catch (NoSuchFieldError e6) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_STEREO_CAMERA.ordinal()] = 5;
        } catch (NoSuchFieldError e7) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO.ordinal()] = 6;
        } catch (NoSuchFieldError e8) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP.ordinal()] = 7;
        } catch (NoSuchFieldError e9) {
        }
        try {
            iArr[ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO.ordinal()] = 10;
        } catch (NoSuchFieldError e10) {
        }
        f95commediatekcameraICameraMode$CameraModeTypeSwitchesValues = iArr;
        return iArr;
    }

    public AdditionManager(ICameraContext iCameraContext) {
        if (iCameraContext.getFeatureConfig().isLomoEffectSupport()) {
            this.mIEffect = new EffectAddition(iCameraContext);
            this.mPhotoAddtion.add(this.mIEffect);
            this.mVideoAddtion.add(this.mIEffect);
        }
        this.mIContinuousShotAddition = new ContinuousShot(iCameraContext);
        this.mDistanceInfo = new DistanceInfo(iCameraContext);
        this.mRemoteCameraAddition = new RemoteCameraAddition(iCameraContext);
        this.mNormalAddition.add(new ThermalThrottle(iCameraContext));
        this.mPhotoAddtion.add(new Asd(iCameraContext));
        this.mPhotoAddtion.add(this.mIContinuousShotAddition);
        this.mNormalAddition.add(this.mDistanceInfo);
        this.mPhotoAddtion.add(this.mRemoteCameraAddition);
        this.mDenoiseAddition.add(this.mIContinuousShotAddition);
    }

    public void setCurrentMode(ICameraMode.CameraModeType cameraModeType) {
        Log.m31d("AdditionManager", "[setCurrentMode]type = " + cameraModeType);
        switch (m570getcommediatekcameraICameraMode$CameraModeTypeSwitchesValues()[cameraModeType.ordinal()]) {
            case 1:
                this.mModeAddition = this.mFaceBeautyAddition;
                break;
            case 2:
                this.mModeAddition = this.mPhotoAddtion;
                break;
            case 3:
                this.mModeAddition = this.mPipPhotoAddition;
                break;
            case 4:
                this.mModeAddition = this.mDenoiseAddition;
                break;
            case 5:
                this.mModeAddition = this.mRefocusAddition;
                break;
            case 6:
                this.mModeAddition = this.mVideoAddtion;
                break;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                this.mModeAddition = this.mPipVideoAddition;
                break;
            default:
                this.mModeAddition = this.mDummyAddtion;
                break;
        }
    }

    public void setListener(ICameraAddition.Listener listener) {
        Iterator<T> it = this.mModeAddition.iterator();
        while (it.hasNext()) {
            ((ICameraAddition) it.next()).setListener(listener);
        }
    }

    public void open(boolean z) {
        Log.m31d("AdditionManager", "[open]isMode = " + z);
        Vector<ICameraAddition> vector = this.mModeAddition;
        if (!z) {
            vector = this.mNormalAddition;
        }
        for (ICameraAddition iCameraAddition : vector) {
            if (iCameraAddition.isSupport()) {
                iCameraAddition.open();
            }
        }
    }

    public void onCameraParameterReady(boolean z) {
        Log.m31d("AdditionManager", "[onCameraParameterReady]isMode = " + z);
        Vector<ICameraAddition> vector = this.mModeAddition;
        if (!z) {
            vector = this.mNormalAddition;
        }
        for (ICameraAddition iCameraAddition : vector) {
            boolean zIsSupport = iCameraAddition.isSupport();
            boolean zIsOpen = iCameraAddition.isOpen();
            if (zIsSupport && (!zIsOpen)) {
                iCameraAddition.open();
            } else if (!zIsSupport && zIsOpen) {
                iCameraAddition.close();
            }
        }
    }

    public void resume() {
        Log.m31d("AdditionManager", "[resume]");
        Iterator<T> it = this.mNormalAddition.iterator();
        while (it.hasNext()) {
            ((ICameraAddition) it.next()).resume();
        }
        Iterator<T> it2 = this.mModeAddition.iterator();
        while (it2.hasNext()) {
            ((ICameraAddition) it2.next()).resume();
        }
    }

    public void pause() {
        Log.m31d("AdditionManager", "[pause]");
        Iterator<T> it = this.mNormalAddition.iterator();
        while (it.hasNext()) {
            ((ICameraAddition) it.next()).pause();
        }
        Iterator<T> it2 = this.mModeAddition.iterator();
        while (it2.hasNext()) {
            ((ICameraAddition) it2.next()).pause();
        }
    }

    public void destory() {
        Log.m31d("AdditionManager", "[destory]");
        Iterator<T> it = this.mNormalAddition.iterator();
        while (it.hasNext()) {
            ((ICameraAddition) it.next()).destory();
        }
        Iterator<T> it2 = this.mModeAddition.iterator();
        while (it2.hasNext()) {
            ((ICameraAddition) it2.next()).destory();
        }
    }

    public void close(boolean z) {
        Log.m31d("AdditionManager", "[close]isMode = " + z);
        Vector<ICameraAddition> vector = this.mModeAddition;
        if (!z) {
            vector = this.mNormalAddition;
        }
        for (ICameraAddition iCameraAddition : vector) {
            if (iCameraAddition.isOpen()) {
                iCameraAddition.close();
            }
        }
    }

    public boolean execute(ICameraMode.ActionType actionType, boolean z, Object... objArr) {
        Log.m31d("AdditionManager", "[execute]isMode = " + z + ",action type = " + actionType);
        Vector<ICameraAddition> vector = this.mModeAddition;
        if (!z) {
            vector = this.mNormalAddition;
        }
        boolean zExecute = false;
        Iterator<T> it = vector.iterator();
        while (it.hasNext()) {
            zExecute = !zExecute ? ((ICameraAddition) it.next()).execute(actionType, objArr) : true;
        }
        if (!z && ((actionType == ICameraMode.ActionType.ACTION_ON_PREVIEW_DISPLAY_SIZE_CHANGED || actionType == ICameraMode.ActionType.ACTION_ON_COMPENSATION_CHANGED) && this.mIEffect != null)) {
            this.mIEffect.execute(actionType, objArr);
        }
        return zExecute;
    }

    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) {
        Log.m31d("AdditionManager", "[execute],addition action type = " + additionActionType);
        boolean z = false;
        Iterator<T> it = this.mModeAddition.iterator();
        while (true) {
            boolean z2 = z;
            if (it.hasNext()) {
                z = !((ICameraAddition) it.next()).execute(additionActionType, objArr) ? z2 : true;
            } else {
                return z2;
            }
        }
    }

    public void onEffectClick() {
        if (this.mIEffect != null) {
            this.mIEffect.execute(ICameraAddition.AdditionActionType.ACTION_EFFECT_CLICK, new Object[0]);
        }
    }
}
