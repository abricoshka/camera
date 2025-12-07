package com.mediatek.camera.p005v2.util;

/* loaded from: classes.dex */
public class SettingKeys {
    public static final String[] KEYS_FOR_SETTING;
    private static final int[] SETTING_TYPE = new int[49];

    static {
        SETTING_TYPE[9] = 1;
        SETTING_TYPE[35] = 1;
        SETTING_TYPE[16] = 1;
        SETTING_TYPE[21] = 1;
        SETTING_TYPE[22] = 1;
        SETTING_TYPE[17] = 1;
        SETTING_TYPE[26] = 1;
        SETTING_TYPE[41] = 1;
        SETTING_TYPE[36] = 1;
        SETTING_TYPE[37] = 1;
        SETTING_TYPE[40] = 1;
        SETTING_TYPE[43] = 1;
        SETTING_TYPE[38] = 1;
        SETTING_TYPE[0] = 1;
        SETTING_TYPE[1] = 1;
        SETTING_TYPE[2] = 1;
        SETTING_TYPE[3] = 1;
        SETTING_TYPE[4] = 1;
        SETTING_TYPE[5] = 1;
        SETTING_TYPE[47] = 1;
        SETTING_TYPE[14] = 1;
        SETTING_TYPE[15] = 1;
        SETTING_TYPE[25] = 1;
        SETTING_TYPE[6] = 1;
        SETTING_TYPE[28] = 1;
        SETTING_TYPE[29] = 1;
        SETTING_TYPE[30] = 1;
        SETTING_TYPE[31] = 1;
        SETTING_TYPE[32] = 1;
        SETTING_TYPE[33] = 1;
        SETTING_TYPE[34] = 1;
        SETTING_TYPE[44] = 1;
        SETTING_TYPE[45] = 1;
        SETTING_TYPE[42] = 0;
        SETTING_TYPE[7] = 0;
        SETTING_TYPE[8] = 0;
        SETTING_TYPE[10] = 0;
        SETTING_TYPE[11] = 0;
        SETTING_TYPE[12] = 0;
        SETTING_TYPE[13] = 1;
        SETTING_TYPE[23] = 0;
        SETTING_TYPE[24] = 0;
        SETTING_TYPE[18] = 1;
        SETTING_TYPE[19] = 0;
        SETTING_TYPE[20] = 0;
        SETTING_TYPE[39] = 0;
        SETTING_TYPE[48] = 0;
        SETTING_TYPE[27] = 0;
        KEYS_FOR_SETTING = new String[49];
        KEYS_FOR_SETTING[39] = "pref_camera_flashmode_key";
        KEYS_FOR_SETTING[9] = "pref_camera_id_key";
        KEYS_FOR_SETTING[10] = "pref_camera_exposure_key";
        KEYS_FOR_SETTING[11] = "pref_camera_scenemode_key";
        KEYS_FOR_SETTING[12] = "pref_camera_whitebalance_key";
        KEYS_FOR_SETTING[35] = "pref_camera_image_properties_key";
        KEYS_FOR_SETTING[13] = "pref_camera_coloreffect_key";
        KEYS_FOR_SETTING[14] = "pref_camera_self_timer_key";
        KEYS_FOR_SETTING[25] = "pref_camera_zsd_key";
        KEYS_FOR_SETTING[16] = "pref_camera_recordlocation_key";
        KEYS_FOR_SETTING[24] = "pref_camera_picturesize_key";
        KEYS_FOR_SETTING[18] = "pref_camera_iso_key";
        KEYS_FOR_SETTING[19] = "pref_camera_antibanding_key";
        KEYS_FOR_SETTING[20] = "pref_video_eis_key";
        KEYS_FOR_SETTING[21] = "pref_camera_recordaudio_key";
        KEYS_FOR_SETTING[22] = "pref_camera_video_hd_recording_key";
        KEYS_FOR_SETTING[17] = "pref_video_quality_key";
        KEYS_FOR_SETTING[23] = "pref_camera_picturesize_ratio_key";
        KEYS_FOR_SETTING[26] = "pref_voice_key";
        KEYS_FOR_SETTING[27] = "pref_video_3dnr_key";
        KEYS_FOR_SETTING[6] = "pref_slow_motion_key";
        KEYS_FOR_SETTING[28] = "pref_slow_motion_video_quality_key";
        KEYS_FOR_SETTING[29] = "perf_camera_ais_key";
        KEYS_FOR_SETTING[30] = "pref_camera_edge_key";
        KEYS_FOR_SETTING[31] = "pref_camera_hue_key";
        KEYS_FOR_SETTING[32] = "pref_camera_saturation_key";
        KEYS_FOR_SETTING[33] = "pref_camera_brightness_key";
        KEYS_FOR_SETTING[34] = "pref_camera_contrast_key";
        KEYS_FOR_SETTING[36] = "camera_mode_key";
        KEYS_FOR_SETTING[37] = "capture_mode_key";
        KEYS_FOR_SETTING[15] = "pref_camera_shot_number";
        KEYS_FOR_SETTING[38] = "recoding_hint_key";
        KEYS_FOR_SETTING[40] = "pref_camera_jpegquality_key";
        KEYS_FOR_SETTING[41] = "pref_stereo3d_mode_key";
        KEYS_FOR_SETTING[42] = "pref_face_detect_key";
        KEYS_FOR_SETTING[7] = "pref_hdr_key";
        KEYS_FOR_SETTING[8] = "pref_asd_key";
        KEYS_FOR_SETTING[43] = "mute_recoding_sound_key";
        KEYS_FOR_SETTING[46] = "pref_dual_camera_key";
        KEYS_FOR_SETTING[44] = "pref_fast_af_key";
        KEYS_FOR_SETTING[45] = "pref_distance_key";
        KEYS_FOR_SETTING[0] = "panorama_key";
        KEYS_FOR_SETTING[1] = "photo_pip_key";
        KEYS_FOR_SETTING[2] = "video_pip_key";
        KEYS_FOR_SETTING[3] = "video_key";
        KEYS_FOR_SETTING[4] = "refocus_key";
        KEYS_FOR_SETTING[5] = "normal_key";
        KEYS_FOR_SETTING[47] = "object_tracking_key";
        KEYS_FOR_SETTING[48] = "dng_key";
    }

    public static String getSettingKey(int i) {
        return KEYS_FOR_SETTING[i];
    }

    public static int getSettingId(String str) {
        for (int i = 0; i < KEYS_FOR_SETTING.length; i++) {
            if (KEYS_FOR_SETTING[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public static int getSettingType(String str) {
        return getSettingType(getSettingId(str));
    }

    public static int getSettingType(int i) {
        return SETTING_TYPE[i];
    }
}
