package com.android.camera.p002v2.util;

/* loaded from: classes.dex */
public class SettingKeys {
    public static final String[] KEYS_FOR_SETTING;
    public static final String[] MODE_KEYS;
    public static final int[] RESET_SETTING_ITEMS;
    public static final int[] SETTING_GROUP_CAMERA_FOR_TAB;
    public static final int[] SETTING_GROUP_COMMON_FOR_LOMOEFFECT;
    public static final int[] SETTING_GROUP_COMMON_FOR_TAB;
    public static final int[] SETTING_GROUP_MAIN_COMMON_FOR_TAB;
    public static final int[] SETTING_GROUP_SUB_COMMON;
    public static final int[] SETTING_GROUP_VIDEO_FOR_TAB;
    private static final int[] SETTING_TYPE = new int[42];
    public static final int[] SUPPORT_BY_3RDPARTY_BUT_HIDDEN;
    public static final int[] THIRDPART_RESET_SETTING_ITEMS;
    public static final int[] UN_SUPPORT_BY_3RDPARTY;
    public static final int[] UN_SUPPORT_BY_FRONT_CAMERA;

    static {
        SETTING_TYPE[12] = 0;
        SETTING_TYPE[13] = 0;
        SETTING_TYPE[14] = 0;
        SETTING_TYPE[19] = 0;
        SETTING_TYPE[20] = 0;
        SETTING_TYPE[24] = 0;
        SETTING_TYPE[33] = 0;
        SETTING_TYPE[0] = 1;
        SETTING_TYPE[1] = 1;
        SETTING_TYPE[2] = 1;
        SETTING_TYPE[3] = 1;
        SETTING_TYPE[4] = 1;
        SETTING_TYPE[5] = 1;
        SETTING_TYPE[6] = 1;
        SETTING_TYPE[39] = 1;
        SETTING_TYPE[7] = 1;
        SETTING_TYPE[34] = 1;
        SETTING_TYPE[8] = 1;
        SETTING_TYPE[9] = 1;
        SETTING_TYPE[10] = 1;
        SETTING_TYPE[11] = 1;
        SETTING_TYPE[15] = 1;
        SETTING_TYPE[16] = 1;
        SETTING_TYPE[17] = 1;
        SETTING_TYPE[18] = 1;
        SETTING_TYPE[21] = 1;
        SETTING_TYPE[22] = 1;
        SETTING_TYPE[23] = 1;
        SETTING_TYPE[25] = 1;
        SETTING_TYPE[26] = 1;
        SETTING_TYPE[27] = 1;
        SETTING_TYPE[28] = 1;
        SETTING_TYPE[29] = 1;
        SETTING_TYPE[30] = 1;
        SETTING_TYPE[31] = 1;
        SETTING_TYPE[32] = 1;
        SETTING_TYPE[35] = 1;
        SETTING_TYPE[37] = 1;
        SETTING_TYPE[38] = 1;
        SETTING_TYPE[40] = 1;
        KEYS_FOR_SETTING = new String[42];
        KEYS_FOR_SETTING[34] = "pref_camera_flashmode_key";
        KEYS_FOR_SETTING[7] = "pref_camera_id_key";
        KEYS_FOR_SETTING[8] = "pref_camera_exposure_key";
        KEYS_FOR_SETTING[9] = "pref_camera_scenemode_key";
        KEYS_FOR_SETTING[10] = "pref_camera_whitebalance_key";
        KEYS_FOR_SETTING[33] = null;
        KEYS_FOR_SETTING[11] = null;
        KEYS_FOR_SETTING[12] = "pref_camera_self_timer_key";
        KEYS_FOR_SETTING[23] = "pref_camera_zsd_key";
        KEYS_FOR_SETTING[14] = "pref_camera_recordlocation_key";
        KEYS_FOR_SETTING[22] = "pref_camera_picturesize_key";
        KEYS_FOR_SETTING[16] = null;
        KEYS_FOR_SETTING[17] = "pref_camera_antibanding_key";
        KEYS_FOR_SETTING[18] = "pref_video_eis_key";
        KEYS_FOR_SETTING[19] = "pref_camera_recordaudio_key";
        KEYS_FOR_SETTING[20] = "pref_camera_video_hd_recording_key";
        KEYS_FOR_SETTING[15] = "pref_video_quality_key";
        KEYS_FOR_SETTING[21] = "pref_camera_picturesize_ratio_key";
        KEYS_FOR_SETTING[24] = null;
        KEYS_FOR_SETTING[25] = "pref_video_3dnr_key";
        KEYS_FOR_SETTING[4] = "pref_slow_motion_key";
        KEYS_FOR_SETTING[26] = "pref_slow_motion_video_quality_key";
        KEYS_FOR_SETTING[27] = "perf_camera_ais_key";
        KEYS_FOR_SETTING[28] = "pref_camera_edge_key";
        KEYS_FOR_SETTING[29] = "pref_camera_hue_key";
        KEYS_FOR_SETTING[30] = "pref_camera_saturation_key";
        KEYS_FOR_SETTING[31] = "pref_camera_brightness_key";
        KEYS_FOR_SETTING[32] = "pref_camera_contrast_key";
        KEYS_FOR_SETTING[13] = null;
        KEYS_FOR_SETTING[35] = "pref_stereo3d_mode_key";
        KEYS_FOR_SETTING[36] = "pref_face_detect_key";
        KEYS_FOR_SETTING[5] = "pref_hdr_key";
        KEYS_FOR_SETTING[6] = "pref_asd_key";
        KEYS_FOR_SETTING[39] = "pref_dual_camera_key";
        KEYS_FOR_SETTING[37] = "pref_fast_af_key";
        KEYS_FOR_SETTING[38] = "pref_distance_key";
        KEYS_FOR_SETTING[0] = "pref_panorama_key";
        KEYS_FOR_SETTING[1] = "pref_photo_pip_key";
        KEYS_FOR_SETTING[2] = "pref_video_pip_key";
        KEYS_FOR_SETTING[3] = "refocus_key";
        KEYS_FOR_SETTING[40] = "pref_dng_key";
        KEYS_FOR_SETTING[41] = "pref_video_stabilization_key";
        UN_SUPPORT_BY_3RDPARTY = new int[]{23, 24, 25, 13, 27, 36, 5, 6, 4, 33, 37, 38, 40, 1, 0, 39};
        SUPPORT_BY_3RDPARTY_BUT_HIDDEN = new int[]{9, 10, 16, 17, 11, 14, 15};
        RESET_SETTING_ITEMS = new int[]{8, 9, 10, 11, 12, 28, 29, 30, 31, 32, 16, 5, 6, 4};
        THIRDPART_RESET_SETTING_ITEMS = new int[]{8, 12};
        MODE_KEYS = new String[]{"pref_panorama_key", "pref_photo_pip_key", "pref_video_pip_key"};
        SETTING_GROUP_COMMON_FOR_TAB = new int[]{39, 14, 8, 11, 9, 10, 33, 17};
        SETTING_GROUP_MAIN_COMMON_FOR_TAB = new int[]{14, 33, 17};
        SETTING_GROUP_COMMON_FOR_LOMOEFFECT = new int[]{39, 14, 8, 9, 10, 33, 17};
        SETTING_GROUP_CAMERA_FOR_TAB = new int[]{23, 27, 24, 36, 6, 40, 12, 13, 22, 21, 16};
        SETTING_GROUP_VIDEO_FOR_TAB = new int[]{25, 18, 19, 15, 26};
        UN_SUPPORT_BY_FRONT_CAMERA = new int[]{4, 13, 26, 35};
        SETTING_GROUP_SUB_COMMON = new int[]{8, 11, 10, 9};
    }

    public static String getSettingKey(int i) {
        return KEYS_FOR_SETTING[i];
    }

    public static int getSettingId(String str) {
        for (int i = 0; i < KEYS_FOR_SETTING.length; i++) {
            if (KEYS_FOR_SETTING[i] != null && KEYS_FOR_SETTING[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public static int getSettingType(String str) {
        return SETTING_TYPE[getSettingId(str)];
    }
}
