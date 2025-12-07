package com.android.camera.p002v2.bridge;

import android.os.Handler;
import com.android.camera.p002v2.app.SettingAgent;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SettingAdapter implements SettingAgent {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingAdapter.class.getSimpleName());
    private static Map<String, String> mKeyMapping = new HashMap();
    private Map<String, String> mInvertedKeyMapping = new HashMap();
    private Map<SettingAgent.SettingChangedListener, SettingCtrl.ISettingFilterListener> mListenersMapping = new HashMap();
    private final SettingCtrl mSettingCtrl;

    static {
        mKeyMapping.put("pref_camera_recordlocation_key", "pref_camera_recordlocation_key");
        mKeyMapping.put("pref_video_quality_key", "pref_video_quality_key");
        mKeyMapping.put("pref_slow_motion_video_quality_key", "pref_slow_motion_video_quality_key");
        mKeyMapping.put("pref_camera_picturesize_key", "pref_camera_picturesize_key");
        mKeyMapping.put("pref_camera_flashmode_key", "pref_camera_flashmode_key");
        mKeyMapping.put("pref_camera_whitebalance_key", "pref_camera_whitebalance_key");
        mKeyMapping.put("pref_camera_scenemode_key", "pref_camera_scenemode_key");
        mKeyMapping.put("pref_camera_exposure_key", "pref_camera_exposure_key");
        mKeyMapping.put("pref_camera_iso_key", "pref_camera_iso_key");
        mKeyMapping.put("pref_camera_coloreffect_key", "pref_camera_coloreffect_key");
        mKeyMapping.put("pref_camera_zsd_key", "pref_camera_zsd_key");
        mKeyMapping.put("pref_camera_picturesize_stereo3d_key", "pref_camera_picturesize_stereo3d_key");
        mKeyMapping.put("pref_stereo3d_mode_key", "pref_stereo3d_mode_key");
        mKeyMapping.put("pref_camera_pictureformat_key", "pref_camera_pictureformat_key");
        mKeyMapping.put("pref_camera_recordaudio_key", "pref_camera_recordaudio_key");
        mKeyMapping.put("pref_camera_video_hd_recording_key", "pref_camera_video_hd_recording_key");
        mKeyMapping.put("pref_camera_image_properties_key", "pref_camera_image_properties_key");
        mKeyMapping.put("pref_camera_edge_key", "pref_camera_edge_key");
        mKeyMapping.put("pref_camera_hue_key", "pref_camera_hue_key");
        mKeyMapping.put("pref_camera_saturation_key", "pref_camera_saturation_key");
        mKeyMapping.put("pref_camera_brightness_key", "pref_camera_brightness_key");
        mKeyMapping.put("pref_camera_contrast_key", "pref_camera_contrast_key");
        mKeyMapping.put("pref_camera_self_timer_key", "pref_camera_self_timer_key");
        mKeyMapping.put("pref_camera_antibanding_key", "pref_camera_antibanding_key");
        mKeyMapping.put("pref_video_eis_key", "pref_video_eis_key");
        mKeyMapping.put("pref_video_3dnr_key", "pref_video_3dnr_key");
        mKeyMapping.put("pref_camera_shot_number", "pref_camera_shot_number");
        mKeyMapping.put("pref_dual_camera_key", "pref_dual_camera_key");
        mKeyMapping.put("pref_fast_af_key", "pref_fast_af_key");
        mKeyMapping.put("pref_distance_key", "pref_distance_key");
        mKeyMapping.put("pref_camera_picturesize_ratio_key", "pref_camera_picturesize_ratio_key");
        mKeyMapping.put("pref_voice_key", "pref_voice_key");
        mKeyMapping.put("pref_face_detect_key", "pref_face_detect_key");
        mKeyMapping.put("pref_panorama_key", "panorama_key");
        mKeyMapping.put("pref_hdr_key", "pref_hdr_key");
        mKeyMapping.put("pref_asd_key", "pref_asd_key");
        mKeyMapping.put("pref_photo_pip_key", "photo_pip_key");
        mKeyMapping.put("pref_video_pip_key", "video_pip_key");
        mKeyMapping.put("video_key", "video_key");
        mKeyMapping.put("refocus_key", "refocus_key");
        mKeyMapping.put("normal_key", "normal_key");
        mKeyMapping.put("pref_camera_id_key", "pref_camera_id_key");
        mKeyMapping.put("pref_slow_motion_key", "pref_slow_motion_key");
        mKeyMapping.put("perf_camera_ais_key", "perf_camera_ais_key");
        mKeyMapping.put("pref_dng_key", "dng_key");
    }

    public SettingAdapter(AppControllerAdapter appControllerAdapter) {
        this.mSettingCtrl = appControllerAdapter.getServices().getSettingController();
        invertMapping();
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public List<String> getSupportedValues(String str, String str2) {
        return this.mSettingCtrl.getSupportedValues(mKeyMapping.get(str), str2);
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public void configurateSetting(Map<String, String> map) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str : map.keySet()) {
            String str2 = map.get(str);
            String str3 = mKeyMapping.get(str);
            if (str3 != null) {
                linkedHashMap.put(str3, str2);
            } else {
                LogHelper.m26i(TAG, "[doSettingChanged], key:" + str + ", newKey:" + str3);
            }
        }
        this.mSettingCtrl.configurateSetting(linkedHashMap);
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public void doSettingChange(String str, String str2) {
        String str3 = mKeyMapping.get(str);
        if (str3 == null) {
            LogHelper.m26i(TAG, "[doSettingChanged], key:" + str + ", newKey:" + str3);
        } else {
            this.mSettingCtrl.doSettingChange(str3, str2);
        }
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public void doSettingChange(Map<String, String> map) {
        HashMap map2 = new HashMap();
        for (String str : map.keySet()) {
            String str2 = map.get(str);
            String str3 = mKeyMapping.get(str);
            if (str3 != null) {
                map2.put(str3, str2);
            } else {
                LogHelper.m26i(TAG, "[doSettingChanged], key:" + str + ", newKey:" + str3);
            }
        }
        this.mSettingCtrl.doSettingChange(map2);
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public void registerSettingChangedListener(final SettingAgent.SettingChangedListener settingChangedListener, Handler handler) {
        SettingCtrl.ISettingFilterListener iSettingFilterListener = new SettingCtrl.ISettingFilterListener() { // from class: com.android.camera.v2.bridge.SettingAdapter.1
            @Override // com.mediatek.camera.v2.setting.SettingCtrl.ISettingFilterListener
            public void onFilterResult(Map<String, String> map, Map<String, String> map2) {
                HashMap map3 = new HashMap();
                HashMap map4 = new HashMap();
                for (String str : map.keySet()) {
                    String str2 = map.get(str);
                    String str3 = map2.get(str);
                    String str4 = (String) SettingAdapter.this.mInvertedKeyMapping.get(str);
                    map3.put(str4, str2);
                    if (map2.containsKey(str)) {
                        map4.put(str4, str3);
                    }
                }
                settingChangedListener.onSettingResult(map3, map4);
            }
        };
        this.mListenersMapping.put(settingChangedListener, iSettingFilterListener);
        this.mSettingCtrl.registerSettingFilterListener(iSettingFilterListener, handler);
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public String getSharedPreferencesValue(String str, String str2) {
        return this.mSettingCtrl.getSharePreferenceValue(mKeyMapping.get(str), str2);
    }

    @Override // com.android.camera.p002v2.app.SettingAgent
    public void clearSharedPreferencesValue(String[] strArr, String str) {
        String[] strArr2 = new String[strArr.length];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < strArr.length) {
                strArr2[i2] = mKeyMapping.get(strArr[i2]);
                i = i2 + 1;
            } else {
                this.mSettingCtrl.clearSharedPreferencesValue(strArr2, str);
                return;
            }
        }
    }

    private void invertMapping() {
        for (String str : mKeyMapping.keySet()) {
            this.mInvertedKeyMapping.put(mKeyMapping.get(str), str);
        }
    }
}
