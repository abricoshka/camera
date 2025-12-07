package com.mediatek.camera.setting;

import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.R;

/* loaded from: classes.dex */
public class SettingDataBase {
    private static final String[] DEFAULT_VALUE_FOR_SETTING;
    private static final int[] DEFAULT_VALUE_FOR_SETTING_ID;
    private static final int[][] MATRIX_RESTRICTION_STATE = new int[64][];
    private static final String[] MATRIX_SCENE_COLUMN;
    private static final int[][] MATRIX_SCENE_STATE;
    private static final String[][] RESET_STATE_VALUE;
    private static final int[] RESTRCTION_SETTING_INDEX;
    private static final Restriction[] RESTRICTIOINS;
    private static final String[] STEREO_CAPTURE_SUPPORT_SCENE_MODE;
    public static final String[] VIDEO_QUALITY_WHITHOUT_4K2K;
    private static final String[] VIDEO_SUPPORT_SCENE_MODE;

    static {
        MATRIX_RESTRICTION_STATE[40] = new int[]{200, 300, 300, 300, 200, 200, 200, 300, 300, 300, 200};
        MATRIX_RESTRICTION_STATE[10] = new int[]{200, 100, 200, 100, 100, 200, 200, 200, 100, 100, 200};
        MATRIX_RESTRICTION_STATE[11] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[12] = new int[]{200, 301, 200, 300, 300, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[13] = new int[]{200, 300, 200, 200, 300, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[7] = new int[]{200, 300, 300, 200, 300, 300, 100, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[14] = new int[]{200, 300, 300, 300, 300, 300, 200, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[15] = new int[]{200, 200, 200, 300, 200, 300, 300, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[26] = new int[]{200, 300, 300, 300, 200, 300, 300, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[17] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[24] = new int[]{200, 200, 200, 100, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[25] = new int[]{200, 200, 200, 100, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[19] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[30] = new int[]{200, 300, 200, 300, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[20] = new int[]{200, 200, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[21] = new int[]{200, 200, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[22] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[23] = new int[]{200, 200, 200, 200, 200, 300, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[18] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[29] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[27] = new int[]{200, 200, 200, 300, 200, 200, 200, 300, 200, 300, 300};
        MATRIX_RESTRICTION_STATE[28] = new int[]{200, 200, 300, 200, 200, 300, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[31] = new int[]{200, 300, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[32] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[33] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[34] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[35] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[52] = new int[]{200, 300, 200, 200, 200, 300, 200, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[53] = new int[]{200, 300, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[37] = new int[]{300, 300, 300, 300, 300, 300, 301, 301, 300, 301, 300};
        MATRIX_RESTRICTION_STATE[38] = new int[]{300, 300, 302, 306, 303, 300, 300, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[16] = new int[]{300, 100, 300, 300, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[39] = new int[]{300, 200, 300, 300, 300, 300, 301, 300, 300, 301, 300};
        MATRIX_RESTRICTION_STATE[41] = new int[]{300, 300, 300, 301, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[43] = new int[]{200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[44] = new int[]{200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[45] = new int[]{200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[50] = new int[]{200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[51] = new int[]{200, 100, 200, 100, 100, 100, 100, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[47] = new int[]{200, 200, 301, 300, 301, 300, 300, 300, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[49] = new int[]{200, 200, 200, 200, 200, 100, 200, 100, 100, 100, 100};
        MATRIX_RESTRICTION_STATE[8] = new int[]{200, 200, 300, 300, 300, 300, 200, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[9] = new int[]{200, 300, 300, 300, 200, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[48] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[56] = new int[]{300, 301, 301, 301, 301, 301, 301, 301, 301, 301, 300};
        MATRIX_RESTRICTION_STATE[57] = new int[]{200, 300, 300, 300, 200, 300, 300, 300, 200, 300, 300};
        RESTRCTION_SETTING_INDEX = new int[]{6, 8, 0, 1, 9, 2, 4, 3, 5, 58, 60};
        RESET_STATE_VALUE = new String[64][];
        RESET_STATE_VALUE[40] = new String[]{"off"};
        RESET_STATE_VALUE[10] = new String[]{"0"};
        RESET_STATE_VALUE[11] = new String[]{"0", "1"};
        RESET_STATE_VALUE[12] = new String[]{"auto", "hdr"};
        RESET_STATE_VALUE[13] = new String[]{"auto", "daylight", "incandescent"};
        RESET_STATE_VALUE[30] = new String[]{"off"};
        RESET_STATE_VALUE[14] = new String[]{"none"};
        RESET_STATE_VALUE[15] = new String[]{"0"};
        RESET_STATE_VALUE[26] = new String[]{"off"};
        RESET_STATE_VALUE[17] = null;
        RESET_STATE_VALUE[25] = null;
        RESET_STATE_VALUE[19] = new String[]{"auto"};
        RESET_STATE_VALUE[20] = new String[]{"auto"};
        RESET_STATE_VALUE[21] = new String[]{"off"};
        RESET_STATE_VALUE[28] = new String[]{"off"};
        RESET_STATE_VALUE[22] = new String[]{"on", "off"};
        RESET_STATE_VALUE[23] = new String[]{"normal"};
        RESET_STATE_VALUE[18] = new String[]{"9"};
        RESET_STATE_VALUE[29] = new String[]{"21"};
        RESET_STATE_VALUE[31] = new String[]{"middle", "low", "high"};
        RESET_STATE_VALUE[52] = new String[]{"off"};
        RESET_STATE_VALUE[53] = new String[]{"off"};
        RESET_STATE_VALUE[32] = new String[]{"middle"};
        RESET_STATE_VALUE[33] = new String[]{"middle", "low"};
        RESET_STATE_VALUE[34] = new String[]{"middle"};
        RESET_STATE_VALUE[35] = new String[]{"middle"};
        RESET_STATE_VALUE[43] = new String[]{"0"};
        RESET_STATE_VALUE[44] = new String[]{"0"};
        RESET_STATE_VALUE[45] = new String[]{"0"};
        RESET_STATE_VALUE[47] = new String[]{"off", "on"};
        RESET_STATE_VALUE[49] = new String[]{"Single", "Multi", "OFF"};
        RESET_STATE_VALUE[39] = new String[]{Boolean.toString(false), Boolean.toString(true)};
        RESET_STATE_VALUE[38] = new String[]{"normal", "hdr", "face_beauty", "asd", "bestshot", "evbracketshot", "autorama"};
        RESET_STATE_VALUE[16] = new String[]{"40"};
        RESET_STATE_VALUE[41] = new String[]{Integer.toString(2), Integer.toString(2)};
        RESET_STATE_VALUE[37] = new String[]{Integer.toString(1), Integer.toString(2)};
        RESET_STATE_VALUE[27] = new String[]{"off"};
        RESET_STATE_VALUE[7] = new String[]{"off", "on"};
        RESET_STATE_VALUE[8] = new String[]{"off", "on"};
        RESET_STATE_VALUE[9] = new String[]{"off", "on"};
        RESET_STATE_VALUE[48] = new String[]{"0", "1"};
        RESET_STATE_VALUE[56] = new String[]{"true", "false"};
        RESET_STATE_VALUE[57] = new String[]{"off", "on"};
        DEFAULT_VALUE_FOR_SETTING_ID = new int[64];
        DEFAULT_VALUE_FOR_SETTING = new String[64];
        DEFAULT_VALUE_FOR_SETTING_ID[40] = R.string.pref_camera_flashmode_default;
        DEFAULT_VALUE_FOR_SETTING_ID[10] = R.string.pref_camera_id_default;
        DEFAULT_VALUE_FOR_SETTING_ID[11] = R.string.pref_camera_exposure_default;
        DEFAULT_VALUE_FOR_SETTING_ID[12] = R.string.pref_camera_scenemode_default;
        DEFAULT_VALUE_FOR_SETTING_ID[13] = R.string.pref_camera_whitebalance_default;
        DEFAULT_VALUE_FOR_SETTING_ID[14] = R.string.pref_camera_coloreffect_default;
        DEFAULT_VALUE_FOR_SETTING_ID[15] = R.string.pref_camera_selftimer_default;
        DEFAULT_VALUE_FOR_SETTING_ID[26] = R.string.pref_camera_zsd_default;
        DEFAULT_VALUE_FOR_SETTING_ID[16] = R.string.pref_camera_continuous_number_default;
        DEFAULT_VALUE_FOR_SETTING_ID[17] = R.string.pref_camera_recordlocation_default;
        DEFAULT_VALUE_FOR_SETTING_ID[25] = -1;
        DEFAULT_VALUE_FOR_SETTING_ID[19] = R.string.pref_camera_iso_default;
        DEFAULT_VALUE_FOR_SETTING_ID[20] = R.string.pref_camera_antibanding_default;
        DEFAULT_VALUE_FOR_SETTING_ID[21] = R.string.pref_camera_eis_default;
        DEFAULT_VALUE_FOR_SETTING_ID[28] = R.string.pref_camera_3dnr_default;
        DEFAULT_VALUE_FOR_SETTING_ID[22] = R.string.pref_camera_recordaudio_default;
        DEFAULT_VALUE_FOR_SETTING_ID[23] = R.string.pref_video_hd_recording_default;
        DEFAULT_VALUE_FOR_SETTING_ID[18] = -1;
        DEFAULT_VALUE_FOR_SETTING_ID[29] = -1;
        DEFAULT_VALUE_FOR_SETTING_ID[42] = R.string.pref_stereo3d_mode_default;
        DEFAULT_VALUE_FOR_SETTING_ID[31] = R.string.pref_camera_edge_default;
        DEFAULT_VALUE_FOR_SETTING_ID[32] = R.string.pref_camera_hue_default;
        DEFAULT_VALUE_FOR_SETTING_ID[33] = R.string.pref_camera_saturation_default;
        DEFAULT_VALUE_FOR_SETTING_ID[34] = R.string.pref_camera_brightness_default;
        DEFAULT_VALUE_FOR_SETTING_ID[35] = R.string.pref_camera_contrast_default;
        DEFAULT_VALUE_FOR_SETTING_ID[30] = R.string.pref_camera_ais_default;
        DEFAULT_VALUE_FOR_SETTING_ID[24] = -1;
        DEFAULT_VALUE_FOR_SETTING_ID[27] = R.string.pref_voice_default;
        DEFAULT_VALUE_FOR_SETTING_ID[7] = R.string.pref_slow_motion_default;
        DEFAULT_VALUE_FOR_SETTING_ID[43] = R.string.pref_facebeauty_smooth_default;
        DEFAULT_VALUE_FOR_SETTING_ID[44] = R.string.pref_facebeauty_skin_color_default;
        DEFAULT_VALUE_FOR_SETTING_ID[45] = R.string.pref_facebeauty_sharp_default;
        DEFAULT_VALUE_FOR_SETTING_ID[50] = R.string.pref_facebeauty_sharp_default;
        DEFAULT_VALUE_FOR_SETTING_ID[51] = R.string.pref_facebeauty_big_eys_default;
        DEFAULT_VALUE_FOR_SETTING_ID[47] = R.string.pref_camera_face_detect_default;
        DEFAULT_VALUE_FOR_SETTING_ID[49] = -1;
        DEFAULT_VALUE_FOR_SETTING_ID[8] = R.string.pref_camera_hdr_default;
        DEFAULT_VALUE_FOR_SETTING_ID[9] = R.string.pref_asd_default;
        DEFAULT_VALUE_FOR_SETTING_ID[57] = R.string.pref_dng_default;
        DEFAULT_VALUE_FOR_SETTING[12] = "auto";
        DEFAULT_VALUE_FOR_SETTING[13] = "auto";
        DEFAULT_VALUE_FOR_SETTING[19] = "auto";
        DEFAULT_VALUE_FOR_SETTING[20] = "auto";
        DEFAULT_VALUE_FOR_SETTING[14] = "none";
        DEFAULT_VALUE_FOR_SETTING[23] = "normal";
        DEFAULT_VALUE_FOR_SETTING[37] = Integer.toString(1);
        DEFAULT_VALUE_FOR_SETTING[38] = "normal";
        DEFAULT_VALUE_FOR_SETTING[56] = "true";
        DEFAULT_VALUE_FOR_SETTING[16] = "40";
        DEFAULT_VALUE_FOR_SETTING[41] = Integer.toString(2);
        DEFAULT_VALUE_FOR_SETTING[39] = Boolean.toString(false);
        DEFAULT_VALUE_FOR_SETTING[48] = "off";
        DEFAULT_VALUE_FOR_SETTING[50] = "off";
        DEFAULT_VALUE_FOR_SETTING[51] = "off";
        DEFAULT_VALUE_FOR_SETTING[0] = "off";
        DEFAULT_VALUE_FOR_SETTING[1] = "off";
        DEFAULT_VALUE_FOR_SETTING[2] = "off";
        DEFAULT_VALUE_FOR_SETTING[3] = "off";
        DEFAULT_VALUE_FOR_SETTING[4] = "off";
        DEFAULT_VALUE_FOR_SETTING[55] = "off";
        DEFAULT_VALUE_FOR_SETTING[5] = "off";
        VIDEO_QUALITY_WHITHOUT_4K2K = new String[]{Integer.toString(6), Integer.toString(5), Integer.toString(4)};
        VIDEO_SUPPORT_SCENE_MODE = new String[]{"auto", "night", "sunset", "party", "portrait", "landscape", "night-portrait", "theatre", "beach", "snow", "steadyphoto", "sports", "candlelight", "hdr"};
        STEREO_CAPTURE_SUPPORT_SCENE_MODE = new String[]{"auto", "night", "sunset", "party", "portrait", "landscape", "night-portrait", "theatre", "beach", "snow", "steadyphoto", "sports", "candlelight"};
        RESTRICTIOINS = new Restriction[]{new Restriction(7).setValues("on").setRestrictions(new Restriction(21).setEnable(false).setValues("off"), new Restriction(22).setEnable(false).setValues("off")), new Restriction(7).setType(0).setValues("on").setRestrictions(new Restriction(12).setEnable(false).setValues("auto"), new Restriction(9).setEnable(false).setValues("off"), new Restriction(28).setEnable(false).setValues("off"), new Restriction(8).setEnable(false).setValues("off")), new Restriction(30).setValues("ais").setRestrictions(new Restriction(12).setEnable(false).setValues("auto"), new Restriction(19).setEnable(false).setValues("auto")), new Restriction(52).setValues("on").setRestrictions(new Restriction(24).setEnable(true).setValues("1.7778"), new Restriction(7).setEnable(false).setValues("off"), new Restriction(8).setEnable(false).setValues("off")), new Restriction(53).setValues("on").setRestrictions(new Restriction(24).setEnable(true).setValues("1.7778"), new Restriction(7).setEnable(false).setValues("off"), new Restriction(8).setEnable(false).setValues("off")), new Restriction(22).setValues("off").setRestrictions(new Restriction(23).setEnable(false).setValues("normal")), new Restriction(39).setType(0).setValues(Boolean.toString(false)).setRestrictions(new Restriction(40).setEnable(true).setMappingFinder(new FlashMappingFinder(null)).setValues("auto", "on", "off")), new Restriction(39).setType(0).setValues(Boolean.toString(true)).setRestrictions(new Restriction(40).setEnable(true).setMappingFinder(new FlashMappingFinder(null)).setValues("auto", "torch", "off")), new Restriction(0).setValues("on").setRestrictions(new Restriction(18).setEnable(true).setValues(VIDEO_QUALITY_WHITHOUT_4K2K)), new Restriction(4).setValues("on").setRestrictions(new Restriction(12).setEnable(true).setValues(VIDEO_SUPPORT_SCENE_MODE)), new Restriction(57).setValues("on").setRestrictions(new Restriction(8).setEnable(false).setValues("off")), new Restriction(38).setValues("normal", "hdr", "asd", "autorama").setRestrictions(new Restriction(43).setValues("disable-value")), new Restriction(38).setValues("normal", "hdr", "asd", "autorama").setRestrictions(new Restriction(44).setValues("disable-value")), new Restriction(38).setValues("normal", "hdr", "asd", "autorama").setRestrictions(new Restriction(45).setValues("disable-value")), new Restriction(38).setValues("normal", "hdr", "asd", "autorama").setRestrictions(new Restriction(50).setValues("disable-value")), new Restriction(38).setValues("normal", "hdr", "asd", "autorama").setRestrictions(new Restriction(51).setValues("disable-value"))};
        MATRIX_SCENE_STATE = new int[64][];
        MATRIX_SCENE_STATE[11] = new int[]{300, 300, 300, 300, 300, 300, 301, 301, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[7] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_SCENE_STATE[13] = new int[]{300, 300, 301, 300, 300, 300, 300, 300, 301, 300, 300, 300, 300, 302, 200, 200};
        MATRIX_SCENE_STATE[19] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[31] = new int[]{300, 301, 302, 301, 301, 302, 302, 302, 302, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[32] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[33] = new int[]{300, 300, 300, 300, 300, 301, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[34] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[35] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[40] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 300, 200, 200, 200, 200, 200};
        MATRIX_SCENE_STATE[52] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[53] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 300, 200, 200, 200, 200, 200};
        MATRIX_SCENE_COLUMN = new String[]{"action", "portrait", "landscape", "night", "night-portrait", "theatre", "beach", "snow", "sunset", "steadyphoto", "fireworks", "sports", "party", "candlelight", "auto", "normal"};
    }

    private static class FlashMappingFinder implements ISettingRule.MappingFinder {
        /* synthetic */ FlashMappingFinder(FlashMappingFinder flashMappingFinder) {
            this();
        }

        private FlashMappingFinder() {
        }

        /* JADX WARN: Removed duplicated region for block: B:18:0x005c  */
        @Override // com.mediatek.camera.ISettingRule.MappingFinder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.String find(java.lang.String r5, java.util.List<java.lang.String> r6) {
            /*
                r4 = this;
                if (r6 == 0) goto L4d
                boolean r0 = r6.contains(r5)
                r0 = r0 ^ 1
                if (r0 == 0) goto L5c
                java.lang.String r0 = "on"
                boolean r0 = r0.equals(r5)
                if (r0 == 0) goto L4f
                java.lang.String r0 = "torch"
            L16:
                if (r6 == 0) goto L27
                boolean r1 = r6.contains(r0)
                r1 = r1 ^ 1
                if (r1 == 0) goto L27
                r0 = 0
                java.lang.Object r0 = r6.get(r0)
                java.lang.String r0 = (java.lang.String) r0
            L27:
                java.lang.String r1 = "SettingDataBase"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "find("
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.StringBuilder r2 = r2.append(r5)
                java.lang.String r3 = ") return "
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.StringBuilder r2 = r2.append(r0)
                java.lang.String r2 = r2.toString()
                com.mediatek.camera.util.Log.m34i(r1, r2)
                return r0
            L4d:
                r0 = r5
                goto L16
            L4f:
                java.lang.String r0 = "torch"
                boolean r0 = r0.equals(r5)
                if (r0 == 0) goto L5c
                java.lang.String r0 = "on"
                goto L16
            L5c:
                r0 = r5
                goto L16
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.SettingDataBase.FlashMappingFinder.find(java.lang.String, java.util.List):java.lang.String");
        }
    }

    public static String getSettingResetValue(int i, int i2) {
        if (i2 == 200) {
            return null;
        }
        if (i2 == 100) {
            return "disable-value";
        }
        if (i2 < 300 || i2 > 307) {
            return null;
        }
        return RESET_STATE_VALUE[i][i2 % 300];
    }

    public static int[][] getRestrictionMatrix() {
        return MATRIX_RESTRICTION_STATE;
    }

    public static int getSettingIndex(int i) {
        if (i >= RESTRCTION_SETTING_INDEX.length) {
            return -1;
        }
        return RESTRCTION_SETTING_INDEX[i];
    }

    public static int getSettingColumn(int i) {
        for (int i2 = 0; i2 < RESTRCTION_SETTING_INDEX.length; i2++) {
            if (i == RESTRCTION_SETTING_INDEX[i2]) {
                return i2;
            }
        }
        return -1;
    }

    public static String getDefaultValue(int i) {
        return DEFAULT_VALUE_FOR_SETTING[i];
    }

    public static String getSceneMode(int i) {
        return MATRIX_SCENE_COLUMN[i];
    }

    public static int[][] getSceneRestrictionMatrix() {
        return MATRIX_SCENE_STATE;
    }

    public static Restriction[] getRestrictions() {
        return RESTRICTIOINS;
    }
}
