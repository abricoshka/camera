package com.mediatek.camera.p005v2.setting;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.util.FloatMath;
import android.util.Range;
import android.util.Size;
import android.view.SurfaceHolder;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class SettingCharacteristics {
    private CameraCharacteristics mCameraCharacteristics;
    private String mCameraId;
    private Context mContext;
    private List<Size> mSupportedPreviewSize;
    private Map<String, List<String>> mSupportedValuesMap = new HashMap();
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingCharacteristics.class.getSimpleName());
    private static final String QUALITY_QCIF = Integer.toString(2);
    private static final String QUALITY_CIF = Integer.toString(3);
    private static final String QUALITY_480P = Integer.toString(4);
    private static final String QUALITY_720P = Integer.toString(5);
    private static final String QUALITY_1080P = Integer.toString(6);
    private static final String QUALITY_QVGA = Integer.toString(7);
    private static final String QUALITY_2160P = Integer.toString(8);
    private static final int[] NORMAL_SUPPORT_QUALIYS = {8, 6, 5, 4, 3, 7, 2};
    private static final String[] NORMAL_SUPPORT_QUALIYS_STRING = {QUALITY_2160P, QUALITY_1080P, QUALITY_720P, QUALITY_480P, QUALITY_CIF, QUALITY_QVGA, QUALITY_QCIF};

    public SettingCharacteristics(CameraCharacteristics cameraCharacteristics, String str, Context context) {
        this.mCameraCharacteristics = cameraCharacteristics;
        this.mCameraId = str;
        this.mContext = context;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x01e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.util.List<java.lang.String> getSupportedValues(java.lang.String r11) {
        /*
            Method dump skipped, instructions count: 564
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.p005v2.setting.SettingCharacteristics.getSupportedValues(java.lang.String):java.util.List");
    }

    public List<Size> getSupportedPreviewSize() {
        if (this.mSupportedPreviewSize != null) {
            return this.mSupportedPreviewSize;
        }
        Size[] outputSizes = ((StreamConfigurationMap) this.mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceHolder.class);
        this.mSupportedPreviewSize = new ArrayList(outputSizes.length);
        for (Size size : outputSizes) {
            this.mSupportedPreviewSize.add(size);
        }
        return this.mSupportedPreviewSize;
    }

    private ArrayList<String> getSupportedVideoQuality() {
        int i = 0;
        ArrayList<String> arrayList = new ArrayList<>();
        int length = NORMAL_SUPPORT_QUALIYS_STRING.length;
        int i2 = 4;
        if ("1".equals(this.mCameraId)) {
            i2 = 2;
        }
        for (int i3 = 0; i3 < length && i < i2; i3++) {
            if (CamcorderProfile.hasProfile(Integer.parseInt(this.mCameraId), NORMAL_SUPPORT_QUALIYS[i3])) {
                i++;
                LogHelper.m26i(TAG, "supportSize = " + i);
                LogHelper.m26i(TAG, "NORMAL_SUPPORT_QUALIYS_STRING[i] = " + NORMAL_SUPPORT_QUALIYS_STRING[i3]);
                arrayList.add(NORMAL_SUPPORT_QUALIYS_STRING[i3]);
            }
        }
        return arrayList;
    }

    private List<String> getSupportedPictureRatio() {
        ArrayList arrayList = new ArrayList();
        Size[] outputSizes = ((StreamConfigurationMap) this.mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceHolder.class);
        double dFindFullscreenRatio = Utils.findFullscreenRatio(this.mContext);
        ArrayList arrayList2 = new ArrayList(outputSizes.length);
        for (Size size : outputSizes) {
            arrayList2.add(size);
        }
        if (Utils.getOptimalPreviewSize(this.mContext, arrayList2, dFindFullscreenRatio) != null && dFindFullscreenRatio != 1.3333d) {
            arrayList.add(String.valueOf(dFindFullscreenRatio));
        }
        arrayList.add("1.3333");
        return arrayList;
    }

    private List<String> getSupportedPictureSize(StreamConfigurationMap streamConfigurationMap, int i) {
        if (streamConfigurationMap == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Size[] highResolutionOutputSizes = streamConfigurationMap.getHighResolutionOutputSizes(i);
        if (highResolutionOutputSizes != null) {
            for (Size size : highResolutionOutputSizes) {
                arrayList.add(String.valueOf(size.getWidth()) + "x" + String.valueOf(size.getHeight()));
            }
        }
        Size[] outputSizes = streamConfigurationMap.getOutputSizes(i);
        if (outputSizes != null) {
            for (Size size2 : outputSizes) {
                arrayList.add(String.valueOf(size2.getWidth()) + "x" + String.valueOf(size2.getHeight()));
            }
        }
        return arrayList;
    }

    private List<String> getSupportedIsoValues(Range<Integer> range) {
        ArrayList arrayList = new ArrayList();
        if (range != null) {
            int iIntValue = ((Integer) range.getLower()).intValue();
            int iIntValue2 = ((Integer) range.getUpper()).intValue();
            arrayList.add("auto");
            int i = 0;
            int iPow = iIntValue;
            while (iPow < iIntValue2) {
                iPow = ((int) Math.pow(2.0d, i)) * 100;
                arrayList.add(String.valueOf(iPow));
                i++;
            }
            LogHelper.m23d(TAG, "minIso:" + iIntValue + ", maxIso:" + iIntValue2);
        }
        return arrayList;
    }

    private List<String> getSupportedExposureCompensation(Range<Integer> range, float f) {
        int iIntValue = ((Integer) range.getLower()).intValue();
        int iIntValue2 = ((Integer) range.getUpper()).intValue();
        LogHelper.m23d(TAG, "minExposureCompensation:" + iIntValue + ", maxExposureCompensation:" + iIntValue2 + ", exposureCompensationStep:" + f);
        int iFloor = (int) FloatMath.floor(iIntValue2 * f);
        int iCeil = (int) FloatMath.ceil(iIntValue * f);
        ArrayList arrayList = new ArrayList();
        LogHelper.m23d(TAG, "maxValue:" + iFloor + ", minValue:" + iCeil);
        while (iCeil <= iFloor) {
            arrayList.add(String.valueOf(iCeil));
            iCeil++;
        }
        LogHelper.m23d(TAG, "supportedValues:" + arrayList);
        return arrayList;
    }

    private List<String> getSupportedSceneMode(int[] iArr) {
        if (iArr == null || (iArr.length == 1 && iArr[0] == 0)) {
            return null;
        }
        ArrayList arrayList = new ArrayList(iArr.length);
        for (String str : SettingConvertor.convertModeEnumToString("pref_camera_scenemode_key", iArr)) {
            arrayList.add(str);
        }
        if (!arrayList.contains("auto")) {
            arrayList.add("auto");
        }
        return arrayList;
    }

    private List<Integer> getAvailableCapablities() {
        int[] iArr = (int[]) getValueFromKey(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        if (iArr == null) {
            LogHelper.m23d(TAG, "The camera " + this.mCameraId + " available capabilities is null");
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList(iArr.length);
        String str = "";
        for (int i : iArr) {
            arrayList.add(Integer.valueOf(i));
            str = str + i + ", ";
        }
        LogHelper.m23d(TAG, "The camera " + this.mCameraId + " available capabilities are:" + str);
        return arrayList;
    }

    private <T> T getValueFromKey(CameraCharacteristics.Key<T> key) {
        T t = null;
        try {
            t = (T) this.mCameraCharacteristics.get(key);
            if (t == null) {
                LogHelper.m24e(TAG, key.getName() + "was null");
            }
        } catch (IllegalArgumentException e) {
            LogHelper.m24e(TAG, key.getName() + " was not supported by this device");
        }
        return t;
    }

    private boolean isDngSupported() {
        boolean z;
        if (!getAvailableCapablities().contains(3)) {
            LogHelper.m24e(TAG, "RAW capablity do not support in camera " + this.mCameraId);
            return false;
        }
        Size[] outputSizes = ((StreamConfigurationMap) this.mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(32);
        if (outputSizes == null) {
            LogHelper.m24e(TAG, "No capture sizes available for raw format");
            return false;
        }
        for (Size size : outputSizes) {
            LogHelper.m23d(TAG, "raw supported size:" + size);
        }
        Rect rect = (Rect) getValueFromKey(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (rect == null) {
            LogHelper.m24e(TAG, "Active array is null");
            return false;
        }
        LogHelper.m23d(TAG, "Active array is:" + rect);
        Size size2 = new Size(rect.width(), rect.height());
        int length = outputSizes.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            Size size3 = outputSizes[i];
            if (size3.getWidth() == size2.getWidth() && size3.getHeight() == size2.getHeight()) {
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            return true;
        }
        LogHelper.m24e(TAG, "Aavailable sizes for RAW format do not include active array size");
        return false;
    }
}
