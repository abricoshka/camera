package com.mediatek.camera.mode.stereocamera.settingrule;

import android.util.Log;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import java.util.List;

/* loaded from: classes.dex */
public abstract class StereoSettingRule implements ISettingRule {
    protected ICameraContext mCameraContext;
    protected int mFeatureType;
    protected ICameraDeviceManager mICameraDeviceManager;
    protected ISettingCtrl mISettingCtrl;

    public StereoSettingRule(ICameraContext iCameraContext, int i) {
        this.mCameraContext = iCameraContext;
        this.mFeatureType = i;
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() {
        this.mISettingCtrl = this.mCameraContext.getSettingController();
        this.mICameraDeviceManager = this.mCameraContext.getCameraDeviceManager();
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        Log.i("StereoSettingRule", "[addLimitation]condition = " + str);
    }
}
