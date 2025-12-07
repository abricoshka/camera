package com.mediatek.camera.mode.stereocamera.settingrule;

import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.setting.SettingItem;

/* loaded from: classes.dex */
public class StereoFdRule extends StereoSettingRule {
    private SettingItem mCurrentSettingItem;

    public StereoFdRule(ICameraContext iCameraContext, int i) {
        super(iCameraContext, i);
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x00f4  */
    @Override // com.mediatek.camera.mode.stereocamera.settingrule.StereoSettingRule, com.mediatek.camera.ISettingRule
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void execute() {
        /*
            r6 = this;
            r0 = 0
            super.execute()
            com.mediatek.camera.platform.ICameraDeviceManager r1 = r6.mICameraDeviceManager
            int r1 = r1.getCurrentCameraId()
            com.mediatek.camera.platform.ICameraDeviceManager r2 = r6.mICameraDeviceManager
            com.mediatek.camera.platform.ICameraDeviceManager$ICameraDevice r1 = r2.getCameraDevice(r1)
            com.mediatek.camera.platform.Parameters r1 = r1.getParameters()
            boolean r2 = com.mediatek.camera.setting.ParametersHelper.isVsDofSupported(r1)
            if (r2 != 0) goto L24
            java.lang.String r0 = "StereoPictureSizeRule"
            java.lang.String r1 = "VsDof only support zsd capture"
            android.util.Log.i(r0, r1)
            return
        L24:
            if (r1 == 0) goto L2e
            if (r1 == 0) goto L38
            int r1 = r1.getMaxNumFocusAreas()
            if (r1 <= 0) goto L38
        L2e:
            java.lang.String r0 = "StereoPictureSizeRule"
            java.lang.String r1 = "VsDof not support fd when lens FF type."
            android.util.Log.i(r0, r1)
            return
        L38:
            com.mediatek.camera.ISettingCtrl r1 = r6.mISettingCtrl
            java.lang.String r2 = "refocus_key"
            com.mediatek.camera.setting.SettingItem r1 = r1.getSetting(r2)
            r6.mCurrentSettingItem = r1
            com.mediatek.camera.setting.SettingItem r1 = r6.mCurrentSettingItem
            java.lang.String r1 = r1.getValue()
            java.lang.String r2 = "StereoPictureSizeRule"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "[execute] FDRule currentValue = "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.String r3 = r3.toString()
            android.util.Log.i(r2, r3)
            com.mediatek.camera.ISettingCtrl r2 = r6.mISettingCtrl
            java.lang.String r3 = "pref_face_detect_key"
            com.mediatek.camera.setting.SettingItem r2 = r2.getSetting(r3)
            java.lang.String r3 = "on"
            boolean r1 = r3.equals(r1)
            if (r1 != 0) goto Lbf
            int r1 = r2.getOverrideCount()
            java.lang.String r3 = "refocus_key"
            com.mediatek.camera.setting.SettingItem$Record r3 = r2.getOverrideRecord(r3)
            if (r3 != 0) goto L83
            return
        L83:
            java.lang.String r3 = "refocus_key"
            r2.removeOverrideRecord(r3)
            int r1 = r1 + (-1)
            com.mediatek.camera.ISettingCtrl r3 = r6.mISettingCtrl
            java.lang.String r4 = "pref_face_detect_key"
            com.mediatek.camera.setting.preference.ListPreference r3 = r3.getListPreference(r4)
            if (r1 <= 0) goto Lb8
            com.mediatek.camera.setting.SettingItem$Record r2 = r2.getTopOverrideRecord()
            if (r2 == 0) goto Lf4
            java.lang.String r1 = r2.getValue()
            java.lang.String r0 = r2.getOverrideValue()
        La4:
            com.mediatek.camera.ISettingCtrl r2 = r6.mISettingCtrl
            java.lang.String r4 = "pref_face_detect_key"
            com.mediatek.camera.platform.ICameraDeviceManager r5 = r6.mICameraDeviceManager
            int r5 = r5.getCurrentCameraId()
            r2.setSettingValue(r4, r1, r5)
            if (r3 == 0) goto Lb7
            r3.setOverrideValue(r0)
        Lb7:
            return
        Lb8:
            if (r3 == 0) goto Lf4
            java.lang.String r1 = r3.getValue()
            goto La4
        Lbf:
            com.mediatek.camera.ISettingCtrl r0 = r6.mISettingCtrl
            java.lang.String r1 = "pref_face_detect_key"
            java.lang.String r3 = "off"
            com.mediatek.camera.platform.ICameraDeviceManager r4 = r6.mICameraDeviceManager
            int r4 = r4.getCurrentCameraId()
            r0.setSettingValue(r1, r3, r4)
            com.mediatek.camera.ISettingCtrl r0 = r6.mISettingCtrl
            java.lang.String r1 = "pref_face_detect_key"
            com.mediatek.camera.setting.preference.ListPreference r0 = r0.getListPreference(r1)
            java.lang.String r1 = "off"
            r0.setOverrideValue(r1)
            com.mediatek.camera.setting.SettingItem$Record r0 = new com.mediatek.camera.setting.SettingItem$Record
            r2.getClass()
            java.lang.String r1 = "off"
            java.lang.String r3 = "off"
            r0.<init>(r1, r3)
            java.lang.String r1 = "refocus_key"
            r2.addOverrideRecord(r1, r0)
            goto Lb7
        Lf4:
            r1 = r0
            goto La4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.stereocamera.settingrule.StereoFdRule.execute():void");
    }
}
