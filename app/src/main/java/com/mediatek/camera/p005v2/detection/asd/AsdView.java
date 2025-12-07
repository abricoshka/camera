package com.mediatek.camera.p005v2.detection.asd;

import android.app.Activity;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.platform.app.AppUi;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingConvertor;
import java.util.List;

/* loaded from: classes.dex */
public class AsdView implements IAsdView {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AsdView.class.getSimpleName());
    private final Activity mActivity;
    private final AppUi mAppUi;
    private String mDetectedScene;
    private ISettingServant mSettingServant;

    public AsdView(Activity activity, AppUi appUi, ISettingServant iSettingServant) {
        this.mActivity = activity;
        this.mAppUi = appUi;
        this.mSettingServant = iSettingServant;
    }

    @Override // com.mediatek.camera.p005v2.detection.asd.IAsdView
    public void updateAsdView(int i) {
        String strConvertModeEnumToString = SettingConvertor.convertModeEnumToString("pref_camera_scenemode_key", i);
        this.mDetectedScene = strConvertModeEnumToString;
        this.mAppUi.updateAsdDetectedScene(this.mDetectedScene);
        if (strConvertModeEnumToString != null) {
            List<String> supportedValues = this.mSettingServant.getSupportedValues("pref_camera_scenemode_key");
            boolean zEqualsIgnoreCase = strConvertModeEnumToString.equalsIgnoreCase(SettingConvertor.SceneMode.HDR.toString().toLowerCase());
            boolean zEqualsIgnoreCase2 = strConvertModeEnumToString.equalsIgnoreCase(SettingConvertor.SceneMode.BACKLIGHT_PORTRAIT.toString().toLowerCase());
            if (supportedValues == null || (!supportedValues.contains(strConvertModeEnumToString)) || zEqualsIgnoreCase || zEqualsIgnoreCase2) {
                strConvertModeEnumToString = SettingConvertor.SceneMode.AUTO.toString().toLowerCase();
            }
            LogHelper.m23d(TAG, "onAsdDetectedScene mode = " + i + " ,and appliedSceneMode = " + strConvertModeEnumToString);
            this.mSettingServant.doSettingChange("pref_camera_scenemode_key", strConvertModeEnumToString, false);
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.asd.IAsdView
    public void hideAsdView() {
        this.mAppUi.updateAsdDetectedScene(null);
    }
}
