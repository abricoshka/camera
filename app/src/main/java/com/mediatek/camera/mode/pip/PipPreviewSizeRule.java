package com.mediatek.camera.mode.pip;

import android.app.Activity;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class PipPreviewSizeRule implements ISettingRule {
    private ICameraDeviceManager deviceManager;
    private Activity mActivity;
    private ICameraDeviceManager.ICameraDevice mBackCamDevice;
    private ICameraContext mCameraContext;
    private ISettingCtrl mISettingCtrl;
    private Parameters mParameters;
    private ICameraDeviceManager.ICameraDevice mTopCamDevice;
    private Parameters mTopParameters;
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinders = new ArrayList();
    private boolean mSwitchingPip = false;
    private String mCurrentPreviewRatio = null;

    public PipPreviewSizeRule(ICameraContext iCameraContext) {
        Log.m31d("PipPreviewSizeRule", "[PipPreviewSizeRule]constructor...");
        this.mCameraContext = iCameraContext;
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() throws NumberFormatException {
        String settingValue;
        this.deviceManager = this.mCameraContext.getCameraDeviceManager();
        this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
        if (this.mTopCamDevice != null) {
            this.mSwitchingPip = this.mTopCamDevice.getCameraId() == this.deviceManager.getCurrentCameraId();
        } else {
            this.mSwitchingPip = false;
        }
        this.mTopCamDevice = this.deviceManager.getCameraDevice(getTopCameraId());
        this.mISettingCtrl = this.mCameraContext.getSettingController();
        this.mActivity = this.mCameraContext.getActivity();
        this.mParameters = this.mBackCamDevice.getParameters();
        if (this.mTopCamDevice != null) {
            this.mTopParameters = this.mTopCamDevice.getParameters();
        }
        int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("photo_pip_key"));
        Log.m31d("PipPreviewSizeRule", "[execute]index = " + iConditionSatisfied + " mSwitchingPip = " + this.mSwitchingPip);
        if (this.mSwitchingPip) {
            settingValue = this.mCurrentPreviewRatio;
            this.mISettingCtrl.setSettingValue("pref_camera_picturesize_ratio_key", settingValue, this.deviceManager.getCurrentCameraId());
            ListPreference listPreference = this.mISettingCtrl.getListPreference("pref_camera_picturesize_ratio_key");
            if (listPreference != null) {
                listPreference.setValue(settingValue);
            }
        } else {
            settingValue = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key");
        }
        this.mCurrentPreviewRatio = settingValue;
        if (iConditionSatisfied == -1) {
            SettingUtils.setPreviewSize(this.mActivity, this.mParameters, settingValue);
        } else {
            setPreviewSize(settingValue);
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        Log.m31d("PipPreviewSizeRule", "[addLimitation]condition = " + str);
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinders.add(mappingFinder);
    }

    public void setPreviewSize(String str) throws NumberFormatException {
        SettingUtils.setPipPreviewSize(this.mActivity, this.mParameters, this.mTopParameters, this.mISettingCtrl, str);
    }

    private int conditionSatisfied(String str) {
        return this.mConditions.indexOf(str);
    }

    private int getTopCameraId() {
        return this.deviceManager.getCurrentCameraId() == this.deviceManager.getBackCameraId() ? this.deviceManager.getFrontCameraId() : this.deviceManager.getBackCameraId();
    }
}
