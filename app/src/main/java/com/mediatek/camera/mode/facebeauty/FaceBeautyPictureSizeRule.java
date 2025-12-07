package com.mediatek.camera.mode.facebeauty;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.preference.ListPreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FaceBeautyPictureSizeRule implements ISettingRule {
    private long PICTURE_SIZE_4M = 4000000;
    private List<String> mConditions = new ArrayList();
    private ICameraContext mICameraContext;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;
    private SettingItem pictureSetting;

    public FaceBeautyPictureSizeRule(ISettingCtrl iSettingCtrl, ICameraContext iCameraContext) {
        this.mISettingCtrl = iSettingCtrl;
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mICameraContext = iCameraContext;
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x01c5  */
    @Override // com.mediatek.camera.ISettingRule
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void execute() throws java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 459
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.facebeauty.FaceBeautyPictureSizeRule.execute():void");
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
    }

    private void setResultSettingValue(int i, String str, String str2, boolean z, SettingItem settingItem) {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        settingItem.setValue(str);
        ListPreference listPreference = settingItem.getListPreference();
        if ("disable-value".equals(str2)) {
            if (listPreference != null) {
                listPreference.setEnabled(false);
            }
        } else {
            if (listPreference != null) {
                listPreference.setOverrideValue(str2, z);
            }
            ParametersHelper.setParametersValue(parameters, currentCameraId, settingItem.getKey(), str);
        }
    }
}
