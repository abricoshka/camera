package com.mediatek.camera.addition;

import android.app.Activity;
import com.mediatek.camera.ICameraAddition;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IFeatureConfig;
import com.mediatek.camera.platform.IFileSaver;
import com.mediatek.camera.platform.IFocusManager;
import com.mediatek.camera.platform.IModuleCtrl;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class CameraAddition implements ICameraAddition {
    protected Activity mActivity;
    protected ICameraAppUi mICameraAppUi;
    protected ICameraContext mICameraContext;
    protected ICameraDeviceManager.ICameraDevice mICameraDevice;
    protected ICameraDeviceManager mICameraDeviceManager;
    protected IFeatureConfig mIFeatureConfig;
    protected IFileSaver mIFileSaver;
    protected IFocusManager mIFocusManager;
    protected IModuleCtrl mIModuleCtrl;
    protected ISettingCtrl mISettingCtrl;

    public CameraAddition(ICameraContext iCameraContext) {
        Assert.assertNotNull(iCameraContext);
        this.mICameraContext = iCameraContext;
        this.mActivity = iCameraContext.getActivity();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mIFileSaver = iCameraContext.getFileSaver();
        this.mICameraAppUi = iCameraContext.getCameraAppUi();
        this.mIModuleCtrl = iCameraContext.getModuleController();
        this.mIFeatureConfig = iCameraContext.getFeatureConfig();
        Assert.assertNotNull(this.mActivity);
        Assert.assertNotNull(this.mICameraDeviceManager);
        Assert.assertNotNull(this.mISettingCtrl);
        Assert.assertNotNull(this.mIFileSaver);
        Assert.assertNotNull(this.mICameraAppUi);
        Assert.assertNotNull(this.mIModuleCtrl);
        Assert.assertNotNull(this.mIFeatureConfig);
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void resume() {
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void pause() {
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void destory() {
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean isOpen() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public void setListener(ICameraAddition.Listener listener) {
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        return false;
    }

    @Override // com.mediatek.camera.ICameraAddition
    public boolean execute(ICameraAddition.AdditionActionType additionActionType, Object... objArr) {
        return false;
    }

    protected void updateCameraDevice() {
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
    }

    protected void updateFocusManager() {
        this.mIFocusManager = this.mICameraContext.getFocusManager();
    }
}
