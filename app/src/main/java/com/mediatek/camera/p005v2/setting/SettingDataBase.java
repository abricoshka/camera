package com.mediatek.camera.p005v2.setting;

/* loaded from: classes.dex */
public class SettingDataBase {
    private static final int[][] MATRIX_RESTRICTION_STATE = new int[49][];
    private static final String[] MATRIX_SCENE_COLUMN;
    private static final int[][] MATRIX_SCENE_STATE;
    private static final String[][] RESET_STATE_VALUE;
    private static final int[] RESTRCTION_SETTING_INDEX;
    private static final Restriction[] RESTRICTIOINS;
    private static final String[] SETTING_DEFAULT_VALUES;
    public static final String[] VIDEO_QUALITY_WHITHOUT_FINE;
    private static final String[] VIDEO_SUPPORT_SCENE_MODE;

    static {
        MATRIX_RESTRICTION_STATE[39] = new int[]{200, 300, 300, 200, 200, 200, 200, 300};
        MATRIX_RESTRICTION_STATE[9] = new int[]{200, 100, 100, 100, 200, 200, 200, 100};
        MATRIX_RESTRICTION_STATE[10] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[11] = new int[]{200, 301, 300, 300, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[12] = new int[]{200, 300, 200, 300, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[6] = new int[]{200, 300, 200, 300, 300, 100, 300, 300};
        MATRIX_RESTRICTION_STATE[13] = new int[]{200, 300, 300, 300, 300, 200, 300, 300};
        MATRIX_RESTRICTION_STATE[14] = new int[]{200, 200, 300, 200, 300, 300, 300, 200};
        MATRIX_RESTRICTION_STATE[25] = new int[]{200, 300, 300, 300, 200, 300, 300, 200};
        MATRIX_RESTRICTION_STATE[16] = new int[]{200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[23] = new int[]{200, 200, 100, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[24] = new int[]{200, 200, 100, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[18] = new int[]{200, 300, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[29] = new int[]{200, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[19] = new int[]{200, 200, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[20] = new int[]{200, 200, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[21] = new int[]{200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[22] = new int[]{200, 200, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[17] = new int[]{200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[28] = new int[]{200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[26] = new int[]{200, 200, 300, 200, 200, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[27] = new int[]{200, 200, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[30] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[31] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[32] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[33] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[34] = new int[]{200, 300, 200, 200, 300, 200, 300, 200};
        MATRIX_RESTRICTION_STATE[44] = new int[]{200, 300, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[45] = new int[]{200, 300, 200, 200, 200, 200, 200, 200};
        MATRIX_RESTRICTION_STATE[36] = new int[]{300, 300, 300, 300, 300, 301, 301, 300};
        MATRIX_RESTRICTION_STATE[37] = new int[]{300, 300, 305, 300, 300, 300, 300, 200};
        MATRIX_RESTRICTION_STATE[15] = new int[]{300, 100, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[38] = new int[]{300, 200, 300, 300, 300, 301, 300, 300};
        MATRIX_RESTRICTION_STATE[40] = new int[]{300, 300, 301, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[42] = new int[]{200, 200, 300, 301, 300, 300, 300, 200};
        MATRIX_RESTRICTION_STATE[7] = new int[]{200, 200, 300, 300, 300, 200, 300, 300};
        MATRIX_RESTRICTION_STATE[8] = new int[]{200, 300, 300, 200, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[43] = new int[]{300, 300, 300, 300, 300, 300, 300, 300};
        MATRIX_RESTRICTION_STATE[48] = new int[]{200, 300, 300, 300, 300, 300, 300, 300};
        RESTRCTION_SETTING_INDEX = new int[]{5, 7, 0, 8, 1, 3, 2, 4};
        RESET_STATE_VALUE = new String[49][];
        RESET_STATE_VALUE[39] = new String[]{"off"};
        RESET_STATE_VALUE[9] = new String[]{"0"};
        RESET_STATE_VALUE[10] = new String[]{"0", "1"};
        RESET_STATE_VALUE[11] = new String[]{"auto", "hdr"};
        RESET_STATE_VALUE[12] = new String[]{"auto", "daylight", "incandescent"};
        RESET_STATE_VALUE[29] = new String[]{"off"};
        RESET_STATE_VALUE[13] = new String[]{"none"};
        RESET_STATE_VALUE[14] = new String[]{"0"};
        RESET_STATE_VALUE[25] = new String[]{"off"};
        RESET_STATE_VALUE[16] = null;
        RESET_STATE_VALUE[24] = null;
        RESET_STATE_VALUE[18] = new String[]{"auto"};
        RESET_STATE_VALUE[19] = new String[]{"auto"};
        RESET_STATE_VALUE[20] = new String[]{"off"};
        RESET_STATE_VALUE[27] = new String[]{"off"};
        RESET_STATE_VALUE[21] = new String[]{"on", "off"};
        RESET_STATE_VALUE[22] = new String[]{"normal"};
        RESET_STATE_VALUE[17] = new String[]{"9"};
        RESET_STATE_VALUE[28] = new String[]{"21"};
        RESET_STATE_VALUE[30] = new String[]{"middle", "low", "high"};
        RESET_STATE_VALUE[44] = new String[]{"off"};
        RESET_STATE_VALUE[45] = new String[]{"off"};
        RESET_STATE_VALUE[31] = new String[]{"middle"};
        RESET_STATE_VALUE[32] = new String[]{"middle", "low"};
        RESET_STATE_VALUE[33] = new String[]{"middle"};
        RESET_STATE_VALUE[34] = new String[]{"middle"};
        RESET_STATE_VALUE[42] = new String[]{"off", "on"};
        RESET_STATE_VALUE[38] = new String[]{Boolean.toString(false), Boolean.toString(true)};
        RESET_STATE_VALUE[37] = new String[]{"normal", "hdr", "asd", "burstshot", "evbracketshot", "autorama"};
        RESET_STATE_VALUE[15] = new String[]{"40"};
        RESET_STATE_VALUE[40] = new String[]{Integer.toString(2), Integer.toString(2)};
        RESET_STATE_VALUE[36] = new String[]{"1", "2"};
        RESET_STATE_VALUE[26] = new String[]{"off"};
        RESET_STATE_VALUE[6] = new String[]{"off", "on"};
        RESET_STATE_VALUE[7] = new String[]{"off", "on"};
        RESET_STATE_VALUE[8] = new String[]{"off", "on"};
        RESET_STATE_VALUE[43] = new String[]{"0", "1"};
        RESET_STATE_VALUE[48] = new String[]{"off", "on"};
        VIDEO_QUALITY_WHITHOUT_FINE = new String[]{Integer.toString(6), Integer.toString(5), Integer.toString(4)};
        VIDEO_SUPPORT_SCENE_MODE = new String[]{"auto", "night", "sunset", "party", "portrait", "landscape", "night-portrait", "theatre", "beach", "snow", "steadyphoto", "sports", "candlelight"};
        RESTRICTIOINS = new Restriction[]{new Restriction(6).setValues("on").setRestrictions(new Restriction(20).setEnable(false).setValues("off")), new Restriction(6).setType(0).setValues("on").setRestrictions(new Restriction(11).setEnable(false).setValues("auto"), new Restriction(8).setEnable(false).setValues("off"), new Restriction(27).setEnable(false).setValues("off"), new Restriction(7).setEnable(false).setValues("off")), new Restriction(44).setValues("on").setRestrictions(new Restriction(23).setEnable(true).setValues("1.7778")), new Restriction(45).setValues("on").setRestrictions(new Restriction(23).setEnable(true).setValues("1.7778")), new Restriction(21).setValues("off").setRestrictions(new Restriction(22).setEnable(false).setValues("normal")), new Restriction(17).setValues("23").setRestrictions(new Restriction(27).setEnable(false).setValues("off")), new Restriction(17).setValues(Integer.toString(8)).setRestrictions(new Restriction(27).setEnable(false).setValues("off")), new Restriction(3).setValues("on").setRestrictions(new Restriction(11).setEnable(true).setValues(VIDEO_SUPPORT_SCENE_MODE)), new Restriction(48).setValues("on").setRestrictions(new Restriction(7).setEnable(false).setValues("off"), new Restriction(15).setEnable(false).setValues("disable-value"))};
        MATRIX_SCENE_STATE = new int[49][];
        MATRIX_SCENE_STATE[10] = new int[]{300, 300, 300, 300, 300, 300, 301, 301, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[6] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        MATRIX_SCENE_STATE[12] = new int[]{300, 300, 301, 300, 300, 300, 300, 300, 301, 300, 300, 300, 300, 302, 200, 200};
        MATRIX_SCENE_STATE[18] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[30] = new int[]{300, 301, 302, 301, 301, 302, 302, 302, 302, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[31] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[32] = new int[]{300, 300, 300, 300, 300, 301, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[33] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[34] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[39] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 300, 200, 200, 200, 200, 200};
        MATRIX_SCENE_STATE[44] = new int[]{300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 200, 200};
        MATRIX_SCENE_STATE[45] = new int[]{200, 200, 200, 200, 200, 200, 200, 200, 200, 200, 300, 200, 200, 200, 200, 200};
        MATRIX_SCENE_COLUMN = new String[]{"action", "portrait", "landscape", "night", "night-portrait", "theatre", "beach", "snow", "sunset", "steadyphoto", "fireworks", "sports", "party", "candlelight", "auto", "normal"};
        SETTING_DEFAULT_VALUES = new String[49];
        SETTING_DEFAULT_VALUES[0] = "off";
        SETTING_DEFAULT_VALUES[1] = "off";
        SETTING_DEFAULT_VALUES[2] = "off";
        SETTING_DEFAULT_VALUES[3] = "off";
        SETTING_DEFAULT_VALUES[4] = "off";
        SETTING_DEFAULT_VALUES[5] = "on";
        SETTING_DEFAULT_VALUES[6] = "off";
        SETTING_DEFAULT_VALUES[7] = "off";
        SETTING_DEFAULT_VALUES[8] = "off";
        SETTING_DEFAULT_VALUES[9] = "off";
        SETTING_DEFAULT_VALUES[10] = "0";
        SETTING_DEFAULT_VALUES[11] = "auto";
        SETTING_DEFAULT_VALUES[12] = "auto";
        SETTING_DEFAULT_VALUES[13] = "none";
        SETTING_DEFAULT_VALUES[14] = "0";
        SETTING_DEFAULT_VALUES[15] = null;
        SETTING_DEFAULT_VALUES[16] = null;
        SETTING_DEFAULT_VALUES[17] = null;
        SETTING_DEFAULT_VALUES[18] = "auto";
        SETTING_DEFAULT_VALUES[19] = "off";
        SETTING_DEFAULT_VALUES[20] = "off";
        SETTING_DEFAULT_VALUES[21] = "off";
        SETTING_DEFAULT_VALUES[22] = "off";
        SETTING_DEFAULT_VALUES[23] = null;
        SETTING_DEFAULT_VALUES[24] = null;
        SETTING_DEFAULT_VALUES[25] = "off";
        SETTING_DEFAULT_VALUES[26] = "off";
        SETTING_DEFAULT_VALUES[27] = "off";
        SETTING_DEFAULT_VALUES[28] = null;
        SETTING_DEFAULT_VALUES[29] = "off";
        SETTING_DEFAULT_VALUES[30] = "middle";
        SETTING_DEFAULT_VALUES[31] = "middle";
        SETTING_DEFAULT_VALUES[32] = "middle";
        SETTING_DEFAULT_VALUES[33] = "middle";
        SETTING_DEFAULT_VALUES[34] = "middle";
        SETTING_DEFAULT_VALUES[35] = null;
        SETTING_DEFAULT_VALUES[36] = null;
        SETTING_DEFAULT_VALUES[37] = null;
        SETTING_DEFAULT_VALUES[38] = null;
        SETTING_DEFAULT_VALUES[39] = "off";
        SETTING_DEFAULT_VALUES[40] = null;
        SETTING_DEFAULT_VALUES[41] = "off";
        SETTING_DEFAULT_VALUES[42] = "off";
        SETTING_DEFAULT_VALUES[43] = "off";
        SETTING_DEFAULT_VALUES[44] = "off";
        SETTING_DEFAULT_VALUES[45] = "off";
        SETTING_DEFAULT_VALUES[46] = "off";
        SETTING_DEFAULT_VALUES[47] = "off";
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

    public static String getSceneMode(int i) {
        return MATRIX_SCENE_COLUMN[i];
    }

    public static int[][] getSceneRestrictionMatrix() {
        return MATRIX_SCENE_STATE;
    }

    public static Restriction[] getRestrictions() {
        return RESTRICTIOINS;
    }

    public static String getDefaultValue(int i) {
        if (i >= 49) {
            return null;
        }
        return SETTING_DEFAULT_VALUES[i];
    }
}
