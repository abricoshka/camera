package com.mediatek.camera.p005v2.setting;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class SettingConvertor {
    private static Map<String, Class<? extends EnumMode>> mEnumClasses = new HashMap();

    public interface EnumMode {
        String getName();

        int getValue();
    }

    public enum FaceDetectMode implements EnumMode {
        OFF(0),
        ON(1);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static FaceDetectMode[] valuesCustom() {
            return values();
        }

        FaceDetectMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum SceneMode implements EnumMode {
        AUTO(0),
        FACE_PORTRAIT(1),
        ACTION(2),
        PORTRAIT(3),
        LANDSCAPE(4),
        NIGHT(5),
        NIGHT_PORTRAIT(6),
        THEATRE(7),
        BEACH(8),
        SNOW(9),
        SUNSET(10),
        STEADYPHOTO(11),
        FIREWORKS(12),
        SPORTS(13),
        PARTY(14),
        CANDLELIGHT(15),
        BARCODE(16),
        HIGH_SPEED_VIDEO(17),
        HDR(18),
        BACKLIGHT_PORTRAIT(32);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SceneMode[] valuesCustom() {
            return values();
        }

        SceneMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum AWBMode implements EnumMode {
        OFF(0),
        AUTO(1),
        INCANDESCENT(2),
        FLUORESCENT(3),
        WARM_FLUORESCENT(4),
        DAYLIGHT(5),
        CLOUDY_DAYLIGHT(6),
        TWILIGHT(7),
        SHADE(8);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AWBMode[] valuesCustom() {
            return values();
        }

        AWBMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum EffectMode implements EnumMode {
        NONE(0),
        MONO(1),
        NEGATIVE(2),
        SOLARIZE(3),
        SEPIA(4),
        POSTERIZE(5),
        WHITEBOARD(6),
        BLACKBOARD(7),
        AQUA(8);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static EffectMode[] valuesCustom() {
            return values();
        }

        EffectMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum EISMode implements EnumMode {
        OFF(0),
        ON(1);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static EISMode[] valuesCustom() {
            return values();
        }

        EISMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum AnitbandingMode implements EnumMode {
        OFF(0),
        HZ_50(1),
        HZ_60(2),
        AUTO(3);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AnitbandingMode[] valuesCustom() {
            return values();
        }

        AnitbandingMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum ASDDetectMode implements EnumMode {
        OFF(0),
        ON(1);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ASDDetectMode[] valuesCustom() {
            return values();
        }

        ASDDetectMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    public enum NoiceReductionMode implements EnumMode {
        OFF(0),
        ON(1);

        private int value;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static NoiceReductionMode[] valuesCustom() {
            return values();
        }

        NoiceReductionMode(int i) {
            this.value = 0;
            this.value = i;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public int getValue() {
            return this.value;
        }

        @Override // com.mediatek.camera.v2.setting.SettingConvertor.EnumMode
        public String getName() {
            return toString();
        }
    }

    static {
        mEnumClasses.put("pref_face_detect_key", FaceDetectMode.class);
        mEnumClasses.put("pref_camera_scenemode_key", SceneMode.class);
        mEnumClasses.put("pref_camera_whitebalance_key", AWBMode.class);
        mEnumClasses.put("pref_camera_coloreffect_key", EffectMode.class);
        mEnumClasses.put("pref_camera_antibanding_key", AnitbandingMode.class);
        mEnumClasses.put("pref_video_eis_key", EISMode.class);
        mEnumClasses.put("pref_asd_key", ASDDetectMode.class);
        mEnumClasses.put("pref_video_3dnr_key", NoiceReductionMode.class);
    }

    public static String convertModeEnumToString(String str, int i) {
        Class<? extends EnumMode> cls = mEnumClasses.get(str);
        if (cls != null) {
            for (EnumMode enumMode : (EnumMode[]) cls.getEnumConstants()) {
                if (enumMode.getValue() == i) {
                    return enumMode.getName().replace('_', '-').toLowerCase();
                }
            }
        }
        return null;
    }

    public static String[] convertModeEnumToString(String str, int[] iArr) {
        Class<? extends EnumMode> cls = mEnumClasses.get(str);
        if (cls != null) {
            EnumMode[] enumModeArr = (EnumMode[]) cls.getEnumConstants();
            String[] strArr = new String[iArr.length];
            for (int i = 0; i < iArr.length; i++) {
                int i2 = iArr[i];
                int length = enumModeArr.length;
                int i3 = 0;
                while (true) {
                    if (i3 < length) {
                        EnumMode enumMode = enumModeArr[i3];
                        if (enumMode.getValue() != i2) {
                            i3++;
                        } else {
                            strArr[i] = enumMode.getName().replace('_', '-').toLowerCase();
                            break;
                        }
                    }
                }
            }
            return strArr;
        }
        return new String[0];
    }

    public static int convertStringToEnum(String str, String str2) {
        int value = 0;
        Class<? extends EnumMode> cls = mEnumClasses.get(str);
        if (cls != null) {
            for (EnumMode enumMode : (EnumMode[]) cls.getEnumConstants()) {
                if (enumMode.getName().replace('_', '-').toLowerCase().equalsIgnoreCase(str2)) {
                    value = enumMode.getValue();
                }
            }
            return value;
        }
        return Integer.parseInt(str2);
    }
}
