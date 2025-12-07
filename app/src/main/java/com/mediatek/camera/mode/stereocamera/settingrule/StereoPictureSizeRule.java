package com.mediatek.camera.mode.stereocamera.settingrule;

import android.graphics.Point;
import android.util.Log;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingItem.Record;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class StereoPictureSizeRule extends StereoSettingRule {
    private SettingItem mCurrentSettingItem;
    private SettingItem mPictureSize;

    public StereoPictureSizeRule(ICameraContext iCameraContext, int i) {
        super(iCameraContext, i);
    }

    @Override // com.mediatek.camera.mode.stereocamera.settingrule.StereoSettingRule, com.mediatek.camera.ISettingRule
    public void execute() {
        String strBuildEnableList;
        super.execute();
        this.mCurrentSettingItem = this.mISettingCtrl.getSetting("refocus_key");
        this.mPictureSize = this.mISettingCtrl.getSetting("pref_camera_picturesize_key");
        String value = this.mPictureSize.getValue();
        String value2 = this.mCurrentSettingItem.getValue();
        Log.i("StereoPictureSizeRule", "currentValue = " + value2);
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        ListPreference listPreference = this.mPictureSize.getListPreference();
        if ("on".equals(value2)) {
            List<Point> listSplitSize = SettingUtils.splitSize(parameters.get("refocus-picture-size-values"));
            if (listSplitSize == null) {
                Log.e("StereoPictureSizeRule", "there is no picture size supported by refocus");
                return;
            }
            String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key");
            ArrayList arrayList = new ArrayList();
            for (Point point : listSplitSize) {
                if (Math.abs((point.x / point.y) - Double.parseDouble(settingValue)) <= 0.02d) {
                    arrayList.add(SettingUtils.pointToStr(point));
                }
            }
            if (arrayList.size() == 0) {
                Log.e("StereoPictureSizeRule", "there is no picture size meeted to current ratio:" + settingValue + " supported by refocus");
                return;
            }
            if (!arrayList.contains(value)) {
                value = (String) arrayList.get(arrayList.size() - 1);
            }
            if (arrayList.size() == 1) {
                strBuildEnableList = (String) arrayList.get(0);
            } else {
                strBuildEnableList = SettingUtils.buildEnableList((String[]) arrayList.toArray(new String[arrayList.size()]), value);
            }
            if (this.mPictureSize.isEnable()) {
                this.mPictureSize.setValue(value);
                if (listPreference != null) {
                    listPreference.setOverrideValue(strBuildEnableList, true);
                }
            }
            ParametersHelper.setParametersValue(parameters, currentCameraId, "pref_camera_picturesize_key", value);
            SettingItem settingItem = this.mPictureSize;
            settingItem.getClass();
            this.mPictureSize.addOverrideRecord("refocus_key", settingItem.new Record(value, strBuildEnableList));
            return;
        }
        this.mPictureSize.removeOverrideRecord("refocus_key");
        if (this.mPictureSize.getOverrideCount() > 0) {
            SettingItem.Record topOverrideRecord = this.mPictureSize.getTopOverrideRecord();
            if (topOverrideRecord != null) {
                String value3 = topOverrideRecord.getValue();
                String overrideValue = topOverrideRecord.getOverrideValue();
                this.mPictureSize.setValue(value3);
                ListPreference listPreference2 = this.mPictureSize.getListPreference();
                if (listPreference2 != null) {
                    listPreference2.setOverrideValue(overrideValue);
                    return;
                }
                return;
            }
            return;
        }
        ListPreference listPreference3 = this.mPictureSize.getListPreference();
        if (listPreference3 != null) {
            listPreference3.setOverrideValue(null);
        }
        this.mPictureSize.setValue(value);
    }
}
