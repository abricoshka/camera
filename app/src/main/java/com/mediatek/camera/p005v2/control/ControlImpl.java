package com.mediatek.camera.p005v2.control;

import android.app.Activity;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.exposure.AutoExposure;
import com.mediatek.camera.p005v2.control.exposure.IExposure;
import com.mediatek.camera.p005v2.control.focus.AutoFocus;
import com.mediatek.camera.p005v2.control.focus.IFocus;
import com.mediatek.camera.p005v2.control.whitebalance.AutoWhiteBalance;
import com.mediatek.camera.p005v2.control.whitebalance.IWhiteBalance;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingConvertor;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class ControlImpl implements IControl$IAaaListener, IControl$IAaaController, ISettingServant.ISettingChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ControlImpl.class.getSimpleName());
    private final Activity mActivity;
    private final IExposure mExposure;
    private final IFocus mFocus;
    private String mInitializedCameraId;
    private final ModuleListener mModuleListener;
    private final ViewGroup mParentViewGroup;
    private String mSceneMode = null;
    private final ISettingServant mSettingServant;
    private final IWhiteBalance mWhiteBalance;

    public ControlImpl(AppController appController, ModuleListener moduleListener, boolean z, String str) {
        LogHelper.m26i(TAG, "ControlImpl cameraId: " + str);
        Assert.assertNotNull(appController);
        Assert.assertNotNull(moduleListener);
        this.mActivity = appController.getActivity();
        this.mParentViewGroup = appController.getCameraAppUi().getModuleLayoutRoot();
        this.mModuleListener = moduleListener;
        this.mInitializedCameraId = str;
        this.mSettingServant = appController.getServices().getSettingController().getSettingServant(str);
        if (z) {
            this.mFocus = new AutoFocus(this.mSettingServant, appController.getServices().getSoundPlayback(), appController, this, this.mParentViewGroup);
            this.mExposure = new AutoExposure(this.mSettingServant, this);
            this.mWhiteBalance = new AutoWhiteBalance(this.mSettingServant, this);
        } else {
            this.mFocus = new AutoFocus(this.mSettingServant, appController.getServices().getSoundPlayback(), appController, this, this.mParentViewGroup);
            this.mExposure = new AutoExposure(this.mSettingServant, this);
            this.mWhiteBalance = new AutoWhiteBalance(this.mSettingServant, this);
        }
    }

    public void open(Activity activity, ViewGroup viewGroup, boolean z) {
        this.mSettingServant.registerSettingChangedListener(this, null, 1);
        this.mFocus.open(activity, viewGroup, z);
        this.mExposure.open(activity, viewGroup, z);
        this.mWhiteBalance.open(activity, viewGroup, z);
    }

    public void resume() {
        updateSceneMode();
        this.mFocus.resume();
        this.mExposure.resume();
        this.mWhiteBalance.resume();
    }

    public void pause() {
        this.mFocus.pause();
        this.mExposure.pause();
        this.mWhiteBalance.pause();
    }

    public void close() {
        this.mSettingServant.unRegisterSettingChangedListener(this);
        this.mFocus.close();
        this.mExposure.close();
        this.mWhiteBalance.close();
    }

    public void onOrientationChanged(int i) {
        this.mFocus.onOrientationCompensationChanged((Utils.getDisplayRotation(this.mActivity) + i) % 360);
    }

    public void onPreviewAreaChanged(RectF rectF) {
        this.mFocus.onPreviewAreaChanged(rectF);
    }

    public void onSingleTapUp(float f, float f2) {
        LogHelper.m26i(TAG, "onSingleTapUp");
        this.mFocus.onSingleTapUp(f, f2);
    }

    public void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, ModuleListener.CaptureType captureType, boolean z) {
        int iConvertStringToEnum = SettingConvertor.convertStringToEnum("pref_camera_scenemode_key", this.mSceneMode);
        int i = (this.mSceneMode == null || !(SettingConvertor.SceneMode.AUTO.toString().equalsIgnoreCase(this.mSceneMode) ^ true)) ? 1 : 2;
        Set<ModuleListener.RequestType> setKeySet = map.keySet();
        LogHelper.m26i(TAG, "configuringSessionRequests control mode : " + i + " SceneMode : " + iConvertStringToEnum + " size : " + setKeySet.size());
        for (ModuleListener.RequestType requestType : setKeySet) {
            CaptureRequest.Builder builder = map.get(requestType);
            builder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(i));
            builder.set(CaptureRequest.CONTROL_SCENE_MODE, Integer.valueOf(iConvertStringToEnum));
            this.mFocus.configuringSessionRequest(requestType, builder, captureType, z);
            this.mExposure.configuringSessionRequest(requestType, builder, captureType, z);
            this.mWhiteBalance.configuringSessionRequest(requestType, builder, z);
        }
    }

    public void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
        this.mFocus.onPreviewCaptureStarted(captureRequest, j, j2);
        this.mExposure.onPreviewCaptureStarted(captureRequest, j, j2);
        this.mWhiteBalance.onPreviewCaptureStarted(captureRequest, j, j2);
    }

    public void onPreviewCaptureProgressed(CaptureRequest captureRequest, CaptureResult captureResult) {
        this.mFocus.onPreviewCaptureProgressed(captureRequest, captureResult);
    }

    public void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        this.mFocus.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
        this.mExposure.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
        this.mWhiteBalance.onPreviewCaptureCompleted(captureRequest, totalCaptureResult);
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        if (map.get("pref_camera_scenemode_key") != null) {
            updateSceneMode();
        }
    }

    @Override // com.mediatek.camera.p005v2.control.IControl$IAaaListener
    public ModuleListener.RequestType getRepeatingRequestType() {
        return this.mModuleListener.getRepeatingRequestType();
    }

    @Override // com.mediatek.camera.p005v2.control.IControl$IAaaListener
    public void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        if (this.mInitializedCameraId == null) {
            this.mModuleListener.requestChangeCaptureRequets(z, requestType, captureType);
        } else if ("0".equals(this.mInitializedCameraId)) {
            this.mModuleListener.requestChangeCaptureRequets(true, z, requestType, captureType);
        } else if ("1".equals(this.mInitializedCameraId)) {
            this.mModuleListener.requestChangeCaptureRequets(false, z, requestType, captureType);
        }
    }

    @Override // com.mediatek.camera.p005v2.control.IControl$IAaaController
    public void aePreTriggerAndCapture() {
        if (this.mExposure != null) {
            this.mExposure.aePreTriggerAndCapture();
        }
    }

    private void updateSceneMode() {
        LogHelper.m26i(TAG, "[updateSceneMode]+");
        String settingValue = this.mSettingServant.getSettingValue("pref_camera_scenemode_key");
        if (settingValue != null && (!settingValue.equals(this.mSceneMode))) {
            this.mSceneMode = settingValue;
            requestChangeCaptureRequets(false, getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
        }
        LogHelper.m26i(TAG, "[updateSceneMode]- ");
    }
}
