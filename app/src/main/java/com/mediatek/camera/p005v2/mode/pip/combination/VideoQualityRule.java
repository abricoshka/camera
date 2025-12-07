package com.mediatek.camera.p005v2.mode.pip.combination;

import android.media.CamcorderProfile;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.setting.SettingItem;
import com.mediatek.camera.p005v2.util.Utils;
import com.mediatek.camera.v2.setting.SettingItem.Record;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class VideoQualityRule implements ISettingRule {
    private String mCurrentCameraId;
    private final SettingCtrl mSettingCtrl;
    private ISettingServant mSettingServant;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(VideoQualityRule.class.getSimpleName());
    private static final String QUALITY_QCIF = Integer.toString(2);
    private static final String QUALITY_CIF = Integer.toString(3);
    private static final String QUALITY_480P = Integer.toString(4);
    private static final String QUALITY_720P = Integer.toString(5);
    private static final String QUALITY_1080P = Integer.toString(6);
    private static final String QUALITY_QVGA = Integer.toString(7);
    private static final String QUALITY_2160P = Integer.toString(8);
    private static final int[] NORMAL_SUPPORT_QUALIYS = {8, 6, 5, 4, 3, 7, 2};
    private static final String[] NORMAL_SUPPORT_QUALIYS_STRING = {QUALITY_2160P, QUALITY_1080P, QUALITY_720P, QUALITY_480P, QUALITY_CIF, QUALITY_QVGA, QUALITY_QCIF};

    public VideoQualityRule(SettingCtrl settingCtrl) {
        this.mCurrentCameraId = null;
        this.mSettingCtrl = settingCtrl;
        this.mCurrentCameraId = this.mSettingCtrl.getCurrentCameraId();
        this.mSettingServant = this.mSettingCtrl.getSettingServant(this.mSettingCtrl.getCurrentCameraId());
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void execute() {
        String value = null;
        String currentCameraId = this.mSettingCtrl.getCurrentCameraId();
        if (!this.mCurrentCameraId.equals(currentCameraId)) {
            this.mSettingServant = this.mSettingCtrl.getSettingServant(currentCameraId);
            this.mCurrentCameraId = currentCameraId;
        }
        String settingValue = this.mSettingServant.getSettingValue("photo_pip_key");
        SettingItem settingItem = this.mSettingServant.getSettingItem("pref_video_quality_key");
        if ("on".equalsIgnoreCase(settingValue)) {
            List<String> supportedPIPVideoQualities = getSupportedPIPVideoQualities();
            String quality = getQuality(this.mSettingServant.getSettingValue("pref_video_quality_key"), supportedPIPVideoQualities);
            settingItem.setValue(quality);
            LogHelper.m26i(TAG, "enter pip set quality:" + quality);
            if (supportedPIPVideoQualities != null) {
                value = Utils.buildEnableList((String[]) supportedPIPVideoQualities.toArray(new String[supportedPIPVideoQualities.size()]));
                settingItem.setOverrideValue(value);
            }
            settingItem.getClass();
            settingItem.addOverrideRecord("photo_pip_key", settingItem.new Record(quality, value));
            return;
        }
        if ("off".equalsIgnoreCase(settingValue)) {
            int overrideCount = settingItem.getOverrideCount();
            if (settingItem.getOverrideRecord("photo_pip_key") == null) {
                return;
            }
            settingItem.removeOverrideRecord("photo_pip_key");
            int i = overrideCount - 1;
            if (i > 0) {
                SettingItem.Record topOverrideRecord = settingItem.getTopOverrideRecord();
                if (topOverrideRecord != null) {
                    value = topOverrideRecord.getValue();
                    settingItem.setValue(value);
                    String overrideValue = topOverrideRecord.getOverrideValue();
                    settingItem.setValue(value);
                    settingItem.setOverrideValue(overrideValue);
                }
            } else {
                String sharedPreferencesValue = this.mSettingServant.getSharedPreferencesValue("pref_video_quality_key");
                settingItem.setOverrideValue(null);
                settingItem.setValue(sharedPreferencesValue);
                value = sharedPreferencesValue;
            }
            LogHelper.m26i(TAG, "exit pip set quality:" + value + " overrideCount " + i);
        }
    }

    @Override // com.mediatek.camera.p005v2.setting.ISettingRule
    public void addLimitation(String str, List<String> list) {
    }

    private List<String> getSupportedPIPVideoQualities() {
        int i = 0;
        LogHelper.m26i(TAG, "getSupportedPIPVideoQualities");
        ArrayList arrayList = new ArrayList();
        int iIntValue = Integer.valueOf("0").intValue();
        int length = NORMAL_SUPPORT_QUALIYS_STRING.length;
        for (int i2 = 0; i2 < length && i < 3; i2++) {
            if (CamcorderProfile.hasProfile(iIntValue, NORMAL_SUPPORT_QUALIYS[i2])) {
                i++;
                if (Utils.getVideoProfile(iIntValue, NORMAL_SUPPORT_QUALIYS[i2]).videoFrameWidth <= 1920) {
                    arrayList.add(NORMAL_SUPPORT_QUALIYS_STRING[i2]);
                }
            }
        }
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    private String getQuality(String str, List<String> list) {
        if (list != null && (!list.contains(str)) && Integer.toString(6).equals(str)) {
            str = Integer.toString(5);
        }
        return !list.contains(str) ? list.get(0) : str;
    }
}
