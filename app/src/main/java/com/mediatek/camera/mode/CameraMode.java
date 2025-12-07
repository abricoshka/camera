package com.mediatek.camera.mode;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFeatureConfig;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.ISelfTimeManager;
import com.mediatek.camera.util.Log;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class CameraMode implements ICameraMode {
    protected Activity mActivity;
    protected MediaActionSound mCameraSound;
    private ICameraMode.ModeState mCurrentModeState = ICameraMode.ModeState.STATE_UNKNOWN;
    protected ICameraAppUi mICameraAppUi;
    protected ICameraContext mICameraContext;
    protected ICameraDeviceManager.ICameraDevice mICameraDevice;
    protected ICameraDeviceManager mICameraDeviceManager;
    protected IFeatureConfig mIFeatureConfig;
    protected IFileSaver mIFileSaver;
    protected IFocusManager mIFocusManager;
    protected IModuleCtrl mIModuleCtrl;
    protected ISelfTimeManager mISelfTimeManager;
    protected ISettingCtrl mISettingCtrl;

    public CameraMode(ICameraContext iCameraContext) {
        Assert.assertNotNull(iCameraContext);
        this.mActivity = iCameraContext.getActivity();
        this.mCameraSound = new MediaActionSound();
        this.mICameraContext = iCameraContext;
        this.mIModuleCtrl = iCameraContext.getModuleController();
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mICameraAppUi = iCameraContext.getCameraAppUi();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mIFeatureConfig = iCameraContext.getFeatureConfig();
        this.mIFileSaver = iCameraContext.getFileSaver();
        this.mISelfTimeManager = iCameraContext.getSelfTimeManager();
        Assert.assertNotNull(this.mIModuleCtrl);
        Assert.assertNotNull(this.mISettingCtrl);
        Assert.assertNotNull(this.mICameraAppUi);
        Assert.assertNotNull(this.mICameraDeviceManager);
        Assert.assertNotNull(this.mIFeatureConfig);
        Assert.assertNotNull(this.mIFileSaver);
        Assert.assertNotNull(this.mISelfTimeManager);
    }

    protected void updateDevice() {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Log.m31d("CameraMode", "[updateDevice] camerId = " + currentCameraId);
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(currentCameraId);
        Assert.assertNotNull(this.mICameraDevice);
    }

    protected void updateFocusManager() {
        Log.m31d("CameraMode", "[updateFocusManager]...");
        this.mIFocusManager = this.mICameraContext.getFocusManager();
        Assert.assertNotNull(this.mIFocusManager);
    }

    @Override // com.mediatek.camera.ICameraMode
    public ICameraMode.ModeState getModeState() {
        return this.mCurrentModeState;
    }

    public void setModeState(ICameraMode.ModeState modeState) {
        this.mCurrentModeState = modeState;
        Log.m31d("CameraMode", "[setModeState] state = " + modeState);
    }

    @Override // com.mediatek.camera.ICameraMode
    public void resume() {
    }

    @Override // com.mediatek.camera.ICameraMode
    public void pause() {
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean open() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean close() {
        Log.m31d("CameraMode", "[close]...");
        this.mCameraSound.release();
        this.mCameraSound = null;
        return true;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isDisplayUseSurfaceView() {
        return true;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isDeviceUseSurfaceView() {
        return true;
    }

    @Override // com.mediatek.camera.ICameraMode
    public SurfaceTexture getBottomSurfaceTexture() {
        return null;
    }

    @Override // com.mediatek.camera.ICameraMode
    public SurfaceTexture getTopSurfaceTexture() {
        return null;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isNeedDualCamera() {
        return false;
    }

    protected boolean isEnoughSpace() {
        return this.mIFileSaver.isEnoughSpace();
    }
}
