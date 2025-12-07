package com.mediatek.camera.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import com.android.camera.CameraHolder;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class SettingUtils {
    private static int mLimitResolution;
    public static final double[] RATIOS = {1.3333d, 1.5d, 1.6667d, 1.7778d};
    public static final MappingFinder MAPPING_FINDER_PICTURE_SIZE = new PictureSizeMappingFinder();
    public static final MappingFinder MAPPING_FINDER_FLASH = new FlashMappingFinder();
    public static final MappingFinder MAPPING_FINDER_VIDEO_QUALITY = new VideoQualityMappingFinder();
    private static final DecimalFormat DECIMAL_FORMATOR = new DecimalFormat("######.####", new DecimalFormatSymbols(Locale.ENGLISH));
    private static double mCurrentFullScreenRatio = 1.7777777777777777d;
    private static int mFullScreenWidth = 0;
    private static int mFullScreenHeight = 0;
    private static String sZsdDefaultValue = null;
    private static String sAntiBandingDefaultValue = null;

    public interface MappingFinder {
        String find(String str, List<String> list);
    }

    private SettingUtils() {
    }

    public static String getPreferenceValue(Context context, SharedPreferences sharedPreferences, int i, String str) {
        String string = sharedPreferences.getString(SettingConstants.getSettingKey(i), str);
        if (i == 24 && string == null) {
            return "1.3333";
        }
        return string;
    }

    public static void setPreviewSize(Context context, Parameters parameters, String str) throws NumberFormatException {
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(context, parameters.getSupportedPreviewSizes(), Double.parseDouble(str), ParametersHelper.isDisplayRotateSupported(parameters));
        if (!parameters.getPreviewSize().equals(optimalPreviewSize)) {
            parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
        }
        setPreviewFrameRate(context, parameters, -1);
    }

    public static void setPipPreviewSize(Activity activity, Parameters parameters, Parameters parameters2, ISettingCtrl iSettingCtrl, String str) throws NumberFormatException {
        double d = Double.parseDouble(iSettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"));
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        parameters2.getSupportedPreviewSizes();
        Camera.Size optimalPreviewSize = getOptimalPreviewSize(activity, filterByBound(supportedPreviewSizes, 1920, 1088), d, ParametersHelper.isDisplayRotateSupported(parameters));
        Camera.Size optimalPreviewSize2 = getOptimalPreviewSize(activity, filterByBound(supportedPreviewSizes, 1920, 1088), d, ParametersHelper.isDisplayRotateSupported(parameters));
        if (optimalPreviewSize.width <= optimalPreviewSize2.width) {
            optimalPreviewSize2 = optimalPreviewSize;
        }
        parameters.setPreviewSize(optimalPreviewSize2.width, optimalPreviewSize2.height);
        parameters2.setPreviewSize(optimalPreviewSize2.width, optimalPreviewSize2.height);
        setPipPreviewFrameRate(iSettingCtrl, parameters, parameters2);
    }

    public static Camera.Size getOptimalPreviewSize(Context context, Parameters parameters, String str) throws NumberFormatException {
        return getOptimalPreviewSize(context, parameters.getSupportedPreviewSizes(), Double.parseDouble(str), ParametersHelper.isDisplayRotateSupported(parameters));
    }

    public static boolean setCameraPictureSize(String str, List<Camera.Size> list, Parameters parameters, String str2, Context context) {
        if (str.indexOf(120) == -1) {
            return false;
        }
        String strFind = MAPPING_FINDER_PICTURE_SIZE.find(str, buildSupportedPictureSizeByRatio(parameters, str2));
        int iIndexOf = strFind == null ? -1 : strFind.indexOf(120);
        if (iIndexOf == -1) {
            return false;
        }
        parameters.setPictureSize(Integer.parseInt(strFind.substring(0, iIndexOf)), Integer.parseInt(strFind.substring(iIndexOf + 1)));
        return true;
    }

    public static boolean isSupported(Object obj, List<?> list) {
        return list != null && list.indexOf(obj) >= 0;
    }

    public static void initialCameraPictureSize(Context context, Parameters parameters, SharedPreferences sharedPreferences) throws NumberFormatException {
        String str = null;
        List<String> listBuildPreviewRatios = buildPreviewRatios(context, parameters);
        if (listBuildPreviewRatios != null && listBuildPreviewRatios.size() > 0) {
            SharedPreferences.Editor editorEdit = sharedPreferences.edit();
            str = listBuildPreviewRatios.get(0);
            editorEdit.putString("pref_camera_picturesize_ratio_key", str);
            editorEdit.apply();
        }
        List<String> listBuildSupportedPictureSizeByRatio = buildSupportedPictureSizeByRatio(parameters, str);
        if (listBuildSupportedPictureSizeByRatio != null && listBuildSupportedPictureSizeByRatio.size() > 0) {
            String str2 = listBuildSupportedPictureSizeByRatio.get(listBuildSupportedPictureSizeByRatio.size() - 1);
            SharedPreferences.Editor editorEdit2 = sharedPreferences.edit();
            editorEdit2.putString("pref_camera_picturesize_key", str2);
            editorEdit2.apply();
            Point size = getSize(str2);
            parameters.setPictureSize(size.x, size.y);
        }
    }

    public static void sortSizesInAscending(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            String str = list.get(0);
            int i2 = 0;
            Point size = getSize(str);
            String str2 = str;
            String str3 = null;
            for (int i3 = 0; i3 < list.size() - i; i3++) {
                str3 = list.get(i3);
                Point size2 = getSize(str3);
                if (size2.x * size2.y > size.x * size.y) {
                    i2 = i3;
                    size = size2;
                    str2 = str3;
                }
            }
            list.set(i2, str3);
            list.set((list.size() - 1) - i, str2);
        }
    }

    public static List<String> buildSupportedPictureSizeByRatio(Parameters parameters, String str) throws NumberFormatException {
        ArrayList arrayList = new ArrayList();
        if (str == null) {
            return arrayList;
        }
        try {
            double d = Double.parseDouble(str);
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            if (supportedPictureSizes != null) {
                for (Camera.Size size : supportedPictureSizes) {
                    if (toleranceRatio(d, size.width / size.height)) {
                        arrayList.add(buildSize(size.width, size.height));
                    }
                }
            }
            return arrayList;
        } catch (NumberFormatException e) {
            Log.m37w("SettingUtils", "buildSupportedPictureSize() bad ratio: " + str, e);
            return arrayList;
        }
    }

    public static List<String> buildPreviewRatios(Context context, Parameters parameters) {
        ArrayList arrayList = new ArrayList();
        if (context != null && parameters != null) {
            arrayList.add(getRatioString(1.3333333333333333d));
            mCurrentFullScreenRatio = findFullscreenRatio(context);
            if (buildSupportedPictureSizeByRatio(parameters, getRatioString(mCurrentFullScreenRatio)).size() > 0) {
                String ratioString = getRatioString(mCurrentFullScreenRatio);
                if (!arrayList.contains(ratioString)) {
                    arrayList.add(ratioString);
                }
            }
        }
        return arrayList;
    }

    public static double getFullScreenRatio() {
        return mCurrentFullScreenRatio;
    }

    public static int getFullScreenWidth() {
        return mFullScreenWidth;
    }

    public static int getFullScreenHeight() {
        return mFullScreenHeight;
    }

    public static void setLimitResolution(int i) {
        mLimitResolution = i;
    }

    public static int getLimitResolution() {
        return mLimitResolution;
    }

    public static void setZsdDefaultValue(String str) {
        sZsdDefaultValue = str;
    }

    public static String getZsdDefaultValue() {
        return sZsdDefaultValue;
    }

    public static void setAntiBandingDefaultValue(String str) {
        sAntiBandingDefaultValue = str;
    }

    public static String getAntiBandingDefaultValue() {
        return sAntiBandingDefaultValue;
    }

    public static void filterLimitResolution(List<String> list) throws NumberFormatException {
        if (mLimitResolution > 0) {
            int i = 0;
            while (i < list.size()) {
                String str = list.get(i);
                int iIndexOf = str.indexOf(120);
                if (Integer.parseInt(str.substring(iIndexOf + 1)) * Integer.parseInt(str.substring(0, iIndexOf)) > mLimitResolution) {
                    list.remove(i);
                    i--;
                }
                i++;
            }
        }
    }

    public static double findFullscreenRatio(Context context) {
        double d;
        double d2 = 1.3333333333333333d;
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        mFullScreenWidth = point.x;
        mFullScreenHeight = point.y;
        if (point.x > point.y) {
            d = point.x / point.y;
        } else {
            d = point.y / point.x;
        }
        for (int i = 0; i < RATIOS.length; i++) {
            if (Math.abs(RATIOS[i] - d) < Math.abs(d - d2)) {
                d2 = RATIOS[i];
            }
        }
        return d2;
    }

    public static void setPreviewFrameRate(Context context, Parameters parameters, int i) {
        List<Integer> supportedPreviewFrameRates = null;
        if (i > 0) {
            supportedPreviewFrameRates = new ArrayList<>();
            supportedPreviewFrameRates.add(Integer.valueOf(i));
        }
        if (supportedPreviewFrameRates == null) {
            supportedPreviewFrameRates = parameters.getSupportedPreviewFrameRates();
        }
        if (supportedPreviewFrameRates != null) {
            parameters.setPreviewFrameRate(((Integer) Collections.max(supportedPreviewFrameRates)).intValue());
        }
    }

    private static Camera.Size getOptimalPreviewSize(Context context, List<Camera.Size> list, double d, boolean z) throws NumberFormatException {
        double dAbs;
        Camera.Size size;
        double dAbs2;
        double d2;
        Camera.Size size2;
        if (list == null) {
            return null;
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int iMin = Math.min(point.x, point.y);
        int iMax = Math.max(point.x, point.y);
        Camera.Size sizeFindBestMatchPanelSize = z ? findBestMatchPanelSize(context, list, d, iMax, iMin) : null;
        if (sizeFindBestMatchPanelSize != null) {
            return sizeFindBestMatchPanelSize;
        }
        double d3 = Double.MAX_VALUE;
        Camera.Size size3 = null;
        double d4 = Double.MAX_VALUE;
        for (Camera.Size size4 : list) {
            if (Math.abs((size4.width / size4.height) - d) > 0.02d) {
                dAbs2 = d3;
                d2 = d4;
                size2 = size3;
            } else if (Math.abs(size4.height - iMin) < d4) {
                double dAbs3 = Math.abs(size4.height - iMin);
                dAbs2 = Math.abs(size4.width - iMax);
                d2 = dAbs3;
                size2 = size4;
            } else if (Math.abs(size4.height - iMin) != d4 || Math.abs(size4.width - iMax) >= d3) {
                dAbs2 = d3;
                d2 = d4;
                size2 = size3;
            } else {
                dAbs2 = Math.abs(size4.width - iMax);
                d2 = d4;
                size2 = size4;
            }
            size3 = size2;
            d4 = d2;
            d3 = dAbs2;
        }
        if (size3 == null) {
            Log.m36w("SettingUtils", "No preview size match the aspect ratio" + d + ",then use the standard(4:3) preview size");
            double d5 = Double.MAX_VALUE;
            double d6 = Double.parseDouble("1.3333");
            for (Camera.Size size5 : list) {
                if (Math.abs((size5.width / size5.height) - d6) > 0.02d) {
                    dAbs = d5;
                    size = size3;
                } else if (Math.abs(size5.height - iMin) < d5) {
                    dAbs = Math.abs(size5.height - iMin);
                    size = size5;
                } else {
                    dAbs = d5;
                    size = size3;
                }
                size3 = size;
                d5 = dAbs;
            }
        }
        return size3;
    }

    public static String getRatioString(double d) {
        return DECIMAL_FORMATOR.format(d);
    }

    public static Point getSize(String str) {
        Point point = new Point();
        int iIndexOf = str.indexOf(120);
        if (iIndexOf != -1) {
            point.x = Integer.parseInt(str.substring(0, iIndexOf));
            point.y = Integer.parseInt(str.substring(iIndexOf + 1));
        }
        return point;
    }

    public static String buildSize(int i, int i2) {
        return "" + i + "x" + i2;
    }

    public static List<Point> splitSize(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList arrayList = new ArrayList();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            Point pointStrToSize = strToSize((String) it.next());
            if (pointStrToSize != null) {
                arrayList.add(pointStrToSize);
            }
        }
        if (arrayList.size() == 0) {
            return null;
        }
        return arrayList;
    }

    public static Point strToSize(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(120);
        if (iIndexOf != -1) {
            return new Point(Integer.parseInt(str.substring(0, iIndexOf)), Integer.parseInt(str.substring(iIndexOf + 1)));
        }
        Log.m32e("SettingUtils", "Invalid size parameter string=" + str);
        return null;
    }

    public static String pointToStr(Point point) {
        if (point != null) {
            return "" + point.x + "x" + point.y;
        }
        return "null";
    }

    public static boolean equals(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return obj.equals(obj2);
    }

    public static void updateFakeNewPictureSizes(Context context, int i, ListPreference listPreference) throws Resources.NotFoundException, NumberFormatException {
        int i2 = 0;
        String str = SystemProperties.get("persist.sys.backcam", "0");
        String str2 = SystemProperties.get("persist.sys.frontcam", "0");
        String[] stringArray = context.getResources().getStringArray(R.array.pref_camera_picturesize_fake_values);
        int i3 = Integer.parseInt(str);
        int i4 = Integer.parseInt(str2);
        if (listPreference != null && listPreference.getEntries() != null) {
            boolean z = i != CameraHolder.instance().getBackCameraId();
            if (!z) {
                if (i3 != 0) {
                    if (i3 > 2) {
                        CharSequence[] charSequenceArr = new CharSequence[4];
                        while (i2 < 4) {
                            if (i3 == 12) {
                                charSequenceArr[i2] = stringArray[(i3 - 2) + i2];
                            } else {
                                charSequenceArr[i2] = stringArray[(i3 - 3) + i2];
                            }
                            i2++;
                        }
                        if (charSequenceArr != null) {
                            listPreference.setEntries(charSequenceArr);
                            listPreference.setOriginalEntries(charSequenceArr);
                            return;
                        }
                        return;
                    }
                    if (i3 == 2) {
                        CharSequence[] charSequenceArr2 = new CharSequence[3];
                        while (i2 < 3) {
                            charSequenceArr2[i2] = stringArray[(i3 - 2) + i2];
                            i2++;
                        }
                        if (charSequenceArr2 != null) {
                            listPreference.setEntries(charSequenceArr2);
                            listPreference.setOriginalEntries(charSequenceArr2);
                            return;
                        }
                        return;
                    }
                    if (i3 == 1) {
                        CharSequence[] charSequenceArr3 = new CharSequence[2];
                        while (i2 < 2) {
                            charSequenceArr3[i2] = stringArray[(i3 - 1) + i2];
                            i2++;
                        }
                        if (charSequenceArr3 != null) {
                            listPreference.setEntries(charSequenceArr3);
                            listPreference.setOriginalEntries(charSequenceArr3);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            if (z && i4 != 0) {
                if (i4 > 2) {
                    CharSequence[] charSequenceArr4 = new CharSequence[4];
                    while (i2 < 4) {
                        charSequenceArr4[i2] = stringArray[(i4 - 3) + i2];
                        i2++;
                    }
                    if (charSequenceArr4 != null) {
                        listPreference.setEntries(charSequenceArr4);
                        listPreference.setOriginalEntries(charSequenceArr4);
                        return;
                    }
                    return;
                }
                if (i4 == 2) {
                    CharSequence[] charSequenceArr5 = new CharSequence[3];
                    while (i2 < 3) {
                        charSequenceArr5[i2] = stringArray[(i4 - 2) + i2];
                        i2++;
                    }
                    if (charSequenceArr5 != null) {
                        listPreference.setEntries(charSequenceArr5);
                        listPreference.setOriginalEntries(charSequenceArr5);
                        return;
                    }
                    return;
                }
                if (i4 == 1) {
                    CharSequence[] charSequenceArr6 = new CharSequence[2];
                    while (i2 < 2) {
                        charSequenceArr6[i2] = stringArray[(i4 - 1) + i2];
                        i2++;
                    }
                    if (charSequenceArr6 != null) {
                        listPreference.setEntries(charSequenceArr6);
                        listPreference.setOriginalEntries(charSequenceArr6);
                    }
                }
            }
        }
    }

    public static class VideoQualityMappingFinder implements MappingFinder {
        @Override // com.mediatek.camera.setting.SettingUtils.MappingFinder
        public String find(String str, List<String> list) {
            String string = (list != null && (list.contains(str) ^ true) && Integer.toString(6).equals(str)) ? Integer.toString(5) : str;
            if (list != null && (!list.contains(string))) {
                return list.get(0);
            }
            return string;
        }
    }

    public static class FlashMappingFinder implements MappingFinder {
        /* JADX WARN: Removed duplicated region for block: B:17:0x0039  */
        @Override // com.mediatek.camera.setting.SettingUtils.MappingFinder
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public java.lang.String find(java.lang.String r3, java.util.List<java.lang.String> r4) {
            /*
                r2 = this;
                if (r4 == 0) goto L29
                boolean r0 = r4.contains(r3)
                r0 = r0 ^ 1
                if (r0 == 0) goto L39
                java.lang.String r0 = "on"
                boolean r0 = r0.equals(r3)
                if (r0 == 0) goto L2b
                java.lang.String r3 = "torch"
                r0 = r3
            L17:
                if (r4 == 0) goto L28
                boolean r1 = r4.contains(r0)
                r1 = r1 ^ 1
                if (r1 == 0) goto L28
                r0 = 0
                java.lang.Object r0 = r4.get(r0)
                java.lang.String r0 = (java.lang.String) r0
            L28:
                return r0
            L29:
                r0 = r3
                goto L17
            L2b:
                java.lang.String r0 = "torch"
                boolean r0 = r0.equals(r3)
                if (r0 == 0) goto L39
                java.lang.String r3 = "on"
                r0 = r3
                goto L17
            L39:
                r0 = r3
                goto L17
            */
            throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.SettingUtils.FlashMappingFinder.find(java.lang.String, java.util.List):java.lang.String");
        }
    }

    public static class PictureSizeMappingFinder implements MappingFinder {
        @Override // com.mediatek.camera.setting.SettingUtils.MappingFinder
        public String find(String str, List<String> list) {
            String strBuildSize;
            if (str.indexOf(120) == -1 || list == null || !(!list.contains(str))) {
                strBuildSize = str;
            } else {
                int size = list.size();
                Point size2 = SettingUtils.getSize(list.get(size - 1));
                Point size3 = SettingUtils.getSize(str);
                int i = size - 2;
                while (i >= 0) {
                    Point size4 = SettingUtils.getSize(list.get(i));
                    if (size4 == null || Math.abs(size4.x - size3.x) >= Math.abs(size2.x - size3.x)) {
                        size4 = size2;
                    }
                    i--;
                    size2 = size4;
                }
                strBuildSize = SettingUtils.buildSize(size2.x, size2.y);
            }
            if (list != null && (!list.contains(strBuildSize))) {
                return list.get(0);
            }
            return strBuildSize;
        }
    }

    public static void restorePreferences(SharedPreferences sharedPreferences, boolean z) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (!z) {
            String[] strArr = new String[SettingConstants.KEYS_FOR_SETTING.length];
            for (int i = 0; i < strArr.length; i++) {
                strArr[i] = SettingConstants.KEYS_FOR_SETTING[i];
            }
            for (int i2 = 0; i2 < SettingConstants.UN_SUPPORT_BY_3RDPARTY.length; i2++) {
                strArr[SettingConstants.UN_SUPPORT_BY_3RDPARTY[i2]] = null;
            }
            for (int i3 = 0; i3 < SettingConstants.SUPPORT_BY_3RDPARTY_BUT_HIDDEN.length; i3++) {
                strArr[SettingConstants.SUPPORT_BY_3RDPARTY_BUT_HIDDEN[i3]] = null;
            }
            for (String str : strArr) {
                editorEdit.remove(str);
            }
        } else {
            editorEdit.clear();
        }
        editorEdit.apply();
    }

    public static void upgradeGlobalPreferences(SharedPreferences sharedPreferences, int i) {
        upgradeOldVersion(sharedPreferences);
        upgradeCameraId(sharedPreferences, i);
    }

    public static void upgradeLocalPreferences(SharedPreferences sharedPreferences) {
        int i;
        try {
            i = sharedPreferences.getInt("pref_local_version_key", 0);
        } catch (Exception e) {
            Log.m36w("SettingUtils", "[upgradeLocalPreferences]Exception = " + e);
            i = 0;
        }
        if (i == 2) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (i == 1) {
            editorEdit.remove("pref_video_quality_key");
        }
        editorEdit.putInt("pref_local_version_key", 2);
        editorEdit.apply();
    }

    public static int readPreferredCameraId(SharedPreferences sharedPreferences) {
        return Integer.parseInt(sharedPreferences.getString("pref_camera_id_key", "0"));
    }

    public static void resetCameraId(SharedPreferences sharedPreferences) {
        writePreferredCameraId(sharedPreferences, 0);
    }

    public static void writePreferredCameraId(SharedPreferences sharedPreferences, int i) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString("pref_camera_id_key", Integer.toString(i));
        editorEdit.apply();
    }

    public static void writePreferredCamera3DMode(SharedPreferences sharedPreferences, String str) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString("pref_stereo3d_mode_key", str);
        editorEdit.apply();
    }

    public static String readPreferredStereoCamera(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("pref_dual_camera_key", "off");
    }

    public static void writePreferredStereoCamera(SharedPreferences sharedPreferences, String str) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString("pref_dual_camera_key", str);
        editorEdit.apply();
    }

    public static void updateSettingCaptureModePreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putString("pref_hdr_key", "off");
        editorEdit.putString("pref_asd_key", "off");
        editorEdit.putString("pref_slow_motion_key", "off");
        editorEdit.apply();
    }

    public static boolean isDisableValue(String str) {
        if (!"disable-value".equals(str)) {
            return false;
        }
        return true;
    }

    public static String buildEnableList(String[] strArr, String str) {
        String str2 = null;
        if (strArr != null) {
            ArrayList arrayList = new ArrayList();
            int length = strArr.length;
            str2 = "[L];" + str + ";";
            for (int i = 0; i < length; i++) {
                if (!arrayList.contains(strArr[i])) {
                    arrayList.add(strArr[i]);
                    if (i == length - 1) {
                        str2 = str2 + strArr[i];
                    } else {
                        str2 = str2 + strArr[i] + ";";
                    }
                }
            }
        }
        return str2;
    }

    public static boolean isBuiltList(String str) {
        if (str == null || !str.startsWith("[L];")) {
            return false;
        }
        return true;
    }

    public static List<String> getEnabledList(String str) {
        ArrayList arrayList = new ArrayList();
        if (isBuiltList(str)) {
            String[] strArrSplit = str.split(";");
            int length = strArrSplit.length;
            for (int i = 2; i < length; i++) {
                if (!arrayList.contains(strArrSplit[i])) {
                    arrayList.add(strArrSplit[i]);
                }
            }
        }
        return arrayList;
    }

    public static String getDefaultValue(String str) {
        String[] strArrSplit;
        if (!isBuiltList(str) || (strArrSplit = str.split(";")) == null || strArrSplit.length <= 1) {
            return null;
        }
        return strArrSplit[1];
    }

    public static boolean toleranceRatio(Point point, Point point2) {
        return toleranceRatio(point.x / point.y, point2.x / point2.y);
    }

    private static List<Camera.Size> filterByBound(List<Camera.Size> list, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        for (Camera.Size size : list) {
            if (size.width <= i && size.height <= i2) {
                arrayList.add(size);
            }
        }
        return arrayList;
    }

    private static boolean toleranceRatio(double d, double d2) {
        return d2 <= 0.0d || Math.abs(d - d2) <= 0.02d;
    }

    private static void upgradeOldVersion(SharedPreferences sharedPreferences) {
        int i;
        String str;
        String str2;
        try {
            i = sharedPreferences.getInt("pref_version_key", 0);
        } catch (Exception e) {
            i = 0;
        }
        if (i == 5) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (i == 0) {
            i = 1;
        }
        if (i == 1) {
            String string = sharedPreferences.getString("pref_camera_jpegquality_key", "85");
            if (string.equals("65")) {
                str2 = "normal";
            } else if (string.equals("75")) {
                str2 = "fine";
            } else {
                str2 = "superfine";
            }
            editorEdit.putString("pref_camera_jpegquality_key", str2);
            i = 2;
        }
        if (i == 2) {
            if (sharedPreferences.getBoolean("pref_camera_recordlocation_key", false)) {
                str = "on";
            } else {
                str = "none";
            }
            editorEdit.putString("pref_camera_recordlocation_key", str);
            i = 3;
        }
        if (i == 3) {
            editorEdit.remove("pref_camera_videoquality_key");
            editorEdit.remove("pref_camera_video_duration_key");
        }
        editorEdit.putInt("pref_version_key", 5);
        editorEdit.apply();
    }

    private static void upgradeCameraId(SharedPreferences sharedPreferences, int i) {
        int preferredCameraId = readPreferredCameraId(sharedPreferences);
        if (preferredCameraId == 0) {
            return;
        }
        if (preferredCameraId < 0 || preferredCameraId >= i) {
            writePreferredCameraId(sharedPreferences, 0);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0051 A[PHI: r1
  0x0051: PHI (r1v5 java.util.List<java.lang.Integer>) = (r1v3 java.util.List<java.lang.Integer>), (r1v8 java.util.List<java.lang.Integer>) binds: [B:14:0x0047, B:5:0x0015] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void setPipPreviewFrameRate(com.mediatek.camera.ISettingCtrl r4, com.mediatek.camera.platform.Parameters r5, com.mediatek.camera.platform.Parameters r6) {
        /*
            r0 = 0
            java.lang.String r1 = "pref_camera_zsd_key"
            java.lang.String r1 = r4.getSettingValue(r1)
            java.lang.String r2 = "on"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L43
            java.util.List r1 = r5.getPIPFrameRateZSDOn()
            if (r6 == 0) goto L51
            java.util.List r0 = r6.getPIPFrameRateZSDOn()
            r3 = r0
            r0 = r1
            r1 = r3
        L1e:
            closeDynamicFrameRate(r5)
            closeDynamicFrameRate(r6)
            if (r0 == 0) goto L33
            java.lang.Object r0 = java.util.Collections.max(r0)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r5.setPreviewFrameRate(r0)
        L33:
            if (r6 == 0) goto L42
            java.lang.Object r0 = java.util.Collections.max(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r6.setPreviewFrameRate(r0)
        L42:
            return
        L43:
            java.util.List r1 = r5.getPIPFrameRateZSDOff()
            if (r6 == 0) goto L51
            java.util.List r0 = r6.getPIPFrameRateZSDOff()
            r3 = r0
            r0 = r1
            r1 = r3
            goto L1e
        L51:
            r3 = r0
            r0 = r1
            r1 = r3
            goto L1e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.SettingUtils.setPipPreviewFrameRate(com.mediatek.camera.ISettingCtrl, com.mediatek.camera.platform.Parameters, com.mediatek.camera.platform.Parameters):void");
    }

    private static void closeDynamicFrameRate(Parameters parameters) {
        if (parameters != null && parameters.isDynamicFrameRateSupported()) {
            parameters.setDynamicFrameRate(false);
        }
    }

    private static Camera.Size findBestMatchPanelSize(Context context, List<Camera.Size> list, double d, int i, int i2) {
        double dAbs = Double.MAX_VALUE;
        double dAbs2 = Double.MAX_VALUE;
        double d2 = i / i2;
        Camera.Size size = null;
        Iterator<T> it = list.iterator();
        while (true) {
            Camera.Size size2 = size;
            if (!it.hasNext()) {
                return size2;
            }
            size = (Camera.Size) it.next();
            if (Math.abs((size.width / size.height) - d) > 0.02d) {
                size = size2;
            } else if (Math.abs(d2 - d) <= 0.02d && (i2 > size.height || i > size.width)) {
                size = size2;
            } else if (Math.abs(size.height - i2) < dAbs) {
                dAbs = Math.abs(size.height - i2);
                dAbs2 = Math.abs(size.width - i);
            } else if (Math.abs(size.height - i2) != dAbs || Math.abs(size.width - i) >= dAbs2) {
                size = size2;
            } else {
                dAbs2 = Math.abs(size.width - i);
            }
        }
    }
}
