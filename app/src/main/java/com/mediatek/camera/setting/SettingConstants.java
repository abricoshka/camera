package com.mediatek.camera.setting;

/* loaded from: classes.dex */
public class SettingConstants {
    public static final String[] KEYS_FOR_SETTING;
    public static final int[] RESET_SETTING_ITEMS;
    public static final int[] SETTING_GROUP_CAMERA_3D_FOR_TAB;
    public static final int[] SETTING_GROUP_CAMERA_FOR_TAB;
    public static final int[] SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW;
    public static final int[] SETTING_GROUP_CAMERA_FOR_UI;
    public static final int[] SETTING_GROUP_COMMON_FOR_LOMOEFFECT;
    public static final int[] SETTING_GROUP_COMMON_FOR_TAB;
    public static final int[] SETTING_GROUP_COMMON_FOR_TAB_PREVIEW;
    public static final int[] SETTING_GROUP_MAIN_COMMON_FOR_TAB;
    public static final int[] SETTING_GROUP_SUB_COMMON;
    public static final int[] SETTING_GROUP_VIDEO_FOR_TAB;
    public static final int[] SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW;
    public static final int[] SETTING_GROUP_VIDEO_FOR_UI;
    private static final int[] SETTING_TYPE = new int[64];
    public static final int[] SUPPORT_BY_3RDPARTY_BUT_HIDDEN;
    public static final int[] THIRDPART_RESET_SETTING_ITEMS;
    public static final int[] UN_SUPPORT_BY_3RDPARTY;
    public static final int[] UN_SUPPORT_BY_FRONT_CAMERA;

    static {
        SETTING_TYPE[10] = 2;
        SETTING_TYPE[36] = 2;
        SETTING_TYPE[17] = 2;
        SETTING_TYPE[22] = 2;
        SETTING_TYPE[23] = 2;
        SETTING_TYPE[18] = 2;
        SETTING_TYPE[24] = 2;
        SETTING_TYPE[27] = 2;
        SETTING_TYPE[42] = 2;
        SETTING_TYPE[46] = 2;
        SETTING_TYPE[47] = 2;
        SETTING_TYPE[9] = 2;
        SETTING_TYPE[54] = 2;
        SETTING_TYPE[57] = 2;
        SETTING_TYPE[62] = 2;
        SETTING_TYPE[63] = 2;
        SETTING_TYPE[37] = 1;
        SETTING_TYPE[38] = 1;
        SETTING_TYPE[41] = 1;
        SETTING_TYPE[48] = 1;
        SETTING_TYPE[50] = 1;
        SETTING_TYPE[51] = 1;
        SETTING_TYPE[39] = 1;
        SETTING_TYPE[56] = 1;
        SETTING_TYPE[0] = 0;
        SETTING_TYPE[1] = 0;
        SETTING_TYPE[2] = 0;
        SETTING_TYPE[3] = 0;
        SETTING_TYPE[4] = 0;
        SETTING_TYPE[5] = 0;
        SETTING_TYPE[6] = 0;
        SETTING_TYPE[55] = 0;
        SETTING_TYPE[58] = 0;
        SETTING_TYPE[60] = 0;
        SETTING_TYPE[40] = 3;
        SETTING_TYPE[11] = 3;
        SETTING_TYPE[12] = 3;
        SETTING_TYPE[13] = 3;
        SETTING_TYPE[14] = 3;
        SETTING_TYPE[15] = 3;
        SETTING_TYPE[16] = 3;
        SETTING_TYPE[26] = 3;
        SETTING_TYPE[25] = 3;
        SETTING_TYPE[19] = 3;
        SETTING_TYPE[20] = 3;
        SETTING_TYPE[21] = 3;
        SETTING_TYPE[28] = 3;
        SETTING_TYPE[7] = 3;
        SETTING_TYPE[29] = 3;
        SETTING_TYPE[30] = 3;
        SETTING_TYPE[31] = 3;
        SETTING_TYPE[32] = 3;
        SETTING_TYPE[33] = 3;
        SETTING_TYPE[34] = 3;
        SETTING_TYPE[35] = 3;
        SETTING_TYPE[43] = 3;
        SETTING_TYPE[44] = 3;
        SETTING_TYPE[45] = 3;
        SETTING_TYPE[8] = 3;
        SETTING_TYPE[49] = 3;
        SETTING_TYPE[52] = 3;
        SETTING_TYPE[53] = 3;
        SETTING_TYPE[59] = 3;
        SETTING_TYPE[61] = 3;
        KEYS_FOR_SETTING = new String[64];
        KEYS_FOR_SETTING[40] = "pref_camera_flashmode_key";
        KEYS_FOR_SETTING[10] = "pref_camera_id_key";
        KEYS_FOR_SETTING[11] = "pref_camera_exposure_key";
        KEYS_FOR_SETTING[12] = "pref_camera_scenemode_key";
        KEYS_FOR_SETTING[13] = "pref_camera_whitebalance_key";
        KEYS_FOR_SETTING[36] = "pref_camera_image_properties_key";
        KEYS_FOR_SETTING[14] = "pref_camera_coloreffect_key";
        KEYS_FOR_SETTING[15] = "pref_camera_self_timer_key";
        KEYS_FOR_SETTING[26] = "pref_camera_zsd_key";
        KEYS_FOR_SETTING[17] = "pref_camera_recordlocation_key";
        KEYS_FOR_SETTING[25] = "pref_camera_picturesize_key";
        KEYS_FOR_SETTING[19] = "pref_camera_iso_key";
        KEYS_FOR_SETTING[20] = "pref_camera_antibanding_key";
        KEYS_FOR_SETTING[21] = "pref_video_eis_key";
        KEYS_FOR_SETTING[22] = "pref_camera_recordaudio_key";
        KEYS_FOR_SETTING[23] = "pref_camera_video_hd_recording_key";
        KEYS_FOR_SETTING[18] = "pref_video_quality_key";
        KEYS_FOR_SETTING[24] = "pref_camera_picturesize_ratio_key";
        KEYS_FOR_SETTING[27] = "pref_voice_key";
        KEYS_FOR_SETTING[28] = "pref_video_3dnr_key";
        KEYS_FOR_SETTING[7] = "pref_slow_motion_key";
        KEYS_FOR_SETTING[29] = "pref_slow_motion_video_quality_key";
        KEYS_FOR_SETTING[30] = "perf_camera_ais_key";
        KEYS_FOR_SETTING[31] = "pref_camera_edge_key";
        KEYS_FOR_SETTING[32] = "pref_camera_hue_key";
        KEYS_FOR_SETTING[33] = "pref_camera_saturation_key";
        KEYS_FOR_SETTING[34] = "pref_camera_brightness_key";
        KEYS_FOR_SETTING[35] = "pref_camera_contrast_key";
        KEYS_FOR_SETTING[37] = "camera_mode_key";
        KEYS_FOR_SETTING[38] = "capture_mode_key";
        KEYS_FOR_SETTING[16] = "pref_camera_shot_number";
        KEYS_FOR_SETTING[39] = "recoding_hint_key";
        KEYS_FOR_SETTING[41] = "pref_camera_jpegquality_key";
        KEYS_FOR_SETTING[42] = "pref_stereo3d_mode_key";
        KEYS_FOR_SETTING[46] = "pref_camera_facebeauty_properties_key";
        KEYS_FOR_SETTING[43] = "pref_facebeauty_smooth_key";
        KEYS_FOR_SETTING[44] = "pref_facebeauty_skin_color_key";
        KEYS_FOR_SETTING[45] = "pref_facebeauty_sharp_key";
        KEYS_FOR_SETTING[47] = "pref_face_detect_key";
        KEYS_FOR_SETTING[8] = "pref_hdr_key";
        KEYS_FOR_SETTING[9] = "pref_asd_key";
        KEYS_FOR_SETTING[48] = "mute_recoding_sound_key";
        KEYS_FOR_SETTING[49] = "pref_face_beauty_multi_mode_key";
        KEYS_FOR_SETTING[50] = "pref_facebeauty_slim_key";
        KEYS_FOR_SETTING[51] = "pref_facebeauty_big_eyes_key";
        KEYS_FOR_SETTING[54] = "pref_dual_camera_key";
        KEYS_FOR_SETTING[52] = "pref_fast_af_key";
        KEYS_FOR_SETTING[53] = "pref_distance_key";
        KEYS_FOR_SETTING[0] = "face_beauty_key";
        KEYS_FOR_SETTING[1] = "panorama_key";
        KEYS_FOR_SETTING[2] = "photo_pip_key";
        KEYS_FOR_SETTING[3] = "video_pip_key";
        KEYS_FOR_SETTING[4] = "video_key";
        KEYS_FOR_SETTING[5] = "refocus_key";
        KEYS_FOR_SETTING[58] = "video_stereo_key";
        KEYS_FOR_SETTING[60] = "photo_stereo_key";
        KEYS_FOR_SETTING[6] = "normal_key";
        KEYS_FOR_SETTING[55] = "object_tracking_key";
        KEYS_FOR_SETTING[56] = "heartbeat-monitor";
        KEYS_FOR_SETTING[57] = "pref_dng_key";
        KEYS_FOR_SETTING[59] = "pref_af_mode_key";
        KEYS_FOR_SETTING[61] = "pref_refocus_video_quality_key";
        KEYS_FOR_SETTING[62] = "pref_refocus_video_quality_key";
        KEYS_FOR_SETTING[63] = "pref_live_focus_key";
        SETTING_GROUP_COMMON_FOR_TAB = new int[]{54, 59, 17, 49, 11, 14, 12, 13, 36, 20};
        SETTING_GROUP_COMMON_FOR_TAB_PREVIEW = new int[]{25, 24, 18, 29};
        SETTING_GROUP_MAIN_COMMON_FOR_TAB = new int[]{17, 49, 36, 20};
        SETTING_GROUP_COMMON_FOR_LOMOEFFECT = new int[]{54, 59, 17, 49, 11, 12, 13, 36, 20};
        SETTING_GROUP_CAMERA_FOR_TAB = new int[]{25, 24, 26, 30, 27, 47, 9, 57, 15, 16, 19, 46, 62};
        SETTING_GROUP_CAMERA_FOR_TAB_NO_PREVIEW = new int[]{30, 26, 27, 47, 15, 16, 19};
        SETTING_GROUP_VIDEO_FOR_TAB = new int[]{28, 21, 22, 23, 18, 29, 61};
        SETTING_GROUP_VIDEO_FOR_TAB_NO_PREVIEW = new int[]{28, 21, 22, 23};
        UN_SUPPORT_BY_3RDPARTY = new int[]{26, 27, 28, 16, 30, 47, 8, 9, 7, 49, 46, 57, 36, 52, 53, 54, 55, 57, 62};
        SUPPORT_BY_3RDPARTY_BUT_HIDDEN = new int[]{12, 13, 19, 20, 14, 23, 17, 18};
        UN_SUPPORT_BY_FRONT_CAMERA = new int[]{7, 16, 29, 42};
        SETTING_GROUP_CAMERA_FOR_UI = new int[]{40, 11, 12, 13, 14, 17, 20, 28, 15, 30, 26, 16, 25, 19, 46, 43, 44, 45, 31, 32, 33, 34, 35, 24, 27, 47, 49, 8, 9};
        SETTING_GROUP_VIDEO_FOR_UI = new int[]{40, 11, 12, 13, 14, 17, 20, 28, 21, 22, 23, 31, 32, 33, 34, 35};
        SETTING_GROUP_SUB_COMMON = new int[]{11, 14, 13, 12};
        RESET_SETTING_ITEMS = new int[]{11, 12, 13, 14, 15, 31, 32, 33, 34, 35, 19, 23, 8, 28, 9, 7, 57, 62};
        SETTING_GROUP_CAMERA_3D_FOR_TAB = new int[]{26, 27, 47, 15, 16, 24, 19, 46};
        THIRDPART_RESET_SETTING_ITEMS = new int[]{11, 12, 13, 14, 15, 31, 32, 33, 34, 35, 19, 28, 23};
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

    public static int getSettingType(int i) {
        return SETTING_TYPE[i];
    }
}
