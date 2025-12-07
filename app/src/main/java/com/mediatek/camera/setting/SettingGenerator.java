package com.mediatek.camera.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.text.TextUtils;
import android.util.FloatMath;
import com.android.camera.FeatureSwitcher;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.setting.preference.PreferenceGroup;
import com.mediatek.camera.setting.preference.PreferenceInflater;
import com.mediatek.camera.setting.preference.SharedPreferencesTransfer;
import com.mediatek.camera.util.Log;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class SettingGenerator {
    private int mCameraId;
    private Camera.CameraInfo[] mCameraInfo;
    private Context mContext;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private IModuleCtrl mIModuleCtrl;
    private PreferenceInflater mInflater;
    private SharedPreferencesTransfer mPrefTransfer;
    private HashMap<Integer, PreferenceGroup> mPreferencesGroupMap;
    private HashMap<Integer, ArrayList<ListPreference>> mPreferencesMap;
    private HashMap<Integer, ArrayList<SettingItem>> mSettingItemsMap;
    private List<String> mSupportedFaceBeautyProperties;
    private List<String> mSupportedImageProperties;
    public static boolean isSupport4K2K = false;
    public static final String QUALITY_HIGH_SPEED_480P = Integer.toString(2002);
    public static final String QUALITY_HIGH_SPEED_720P = Integer.toString(2003);
    public static final String QUALITY_HIGH_SPEED_1080P = Integer.toString(2004);
    public static final String QUALITY_HIGH_SPEED_2160P = Integer.toString(2005);
    private static final String QUALITY_QCIF = Integer.toString(2);
    private static final String QUALITY_CIF = Integer.toString(3);
    private static final String QUALITY_480P = Integer.toString(4);
    private static final String QUALITY_720P = Integer.toString(5);
    private static final String QUALITY_1080P = Integer.toString(6);
    private static final String QUALITY_QVGA = Integer.toString(7);
    private static final String QUALITY_2160P = Integer.toString(8);
    private static final String VIDEO_QUALITY_VSDOF_MEDIUM = Integer.toString(3101);
    private static final String VIDEO_QUALITY_VSDOF_HIGH = Integer.toString(3102);
    private static final int[] SLOW_MOTION_SUPPORT_QUALIYS = {2005, 2004, 2003, 2002};
    private static final String[] SLOW_MOTION_SUPPORT_QUALIYS_STRING = {QUALITY_HIGH_SPEED_2160P, QUALITY_HIGH_SPEED_1080P, QUALITY_HIGH_SPEED_720P, QUALITY_HIGH_SPEED_480P};
    private static final int[] NORMAL_SUPPORT_QUALIYS = {8, 6, 5, 4, 3, 7, 2};
    private static final String[] NORMAL_SUPPORT_QUALIYS_STRING = {QUALITY_2160P, QUALITY_1080P, QUALITY_720P, QUALITY_480P, QUALITY_CIF, QUALITY_QVGA, QUALITY_QCIF};
    private static final CharSequence[] COLOR_EFFECT_SUPPORT_BY_3RD = {"none", "mono", "sepia", "negative", "solarize", "aqua", "pastel", "mosaic", "red-tint", "blue-tint", "green-tint", "blackboard", "whiteboard", "sepiablue", "sepiagreen"};
    private int mPreferenceRes = 0;
    private List<String> mSupportedDualCamera = new ArrayList();
    private ArrayList<SettingItem> mSettingList = new ArrayList<>();

    public SettingGenerator(ICameraContext iCameraContext, SharedPreferencesTransfer sharedPreferencesTransfer) {
        this.mICameraContext = iCameraContext;
        this.mContext = iCameraContext.getActivity();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mIModuleCtrl = iCameraContext.getModuleController();
        this.mPrefTransfer = sharedPreferencesTransfer;
        this.mCameraInfo = this.mICameraDeviceManager.getCameraInfo();
        this.mCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mCameraId);
        int numberOfCameras = this.mICameraDeviceManager.getNumberOfCameras();
        this.mPreferencesGroupMap = new HashMap<>(numberOfCameras);
        this.mPreferencesMap = new HashMap<>(numberOfCameras);
        this.mSettingItemsMap = new HashMap<>(numberOfCameras);
    }

    public void createSettings(int i) throws NumberFormatException {
        this.mPreferenceRes = i;
        this.mInflater = new PreferenceInflater(this.mContext, this.mPrefTransfer);
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        PreferenceGroup preferenceGroup = (PreferenceGroup) this.mInflater.inflate(i);
        this.mPreferencesGroupMap.put(Integer.valueOf(currentCameraId), preferenceGroup);
        createSettingItems();
        createPreferences(preferenceGroup, currentCameraId);
    }

    public void updatePreferences() throws NumberFormatException {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(currentCameraId);
        this.mCameraId = currentCameraId;
        ArrayList<ListPreference> arrayList = this.mPreferencesMap.get(Integer.valueOf(currentCameraId));
        if (arrayList == null) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) this.mInflater.inflate(this.mPreferenceRes);
            this.mPreferencesGroupMap.put(Integer.valueOf(currentCameraId), preferenceGroup);
            createPreferences(preferenceGroup, currentCameraId);
            return;
        }
        ArrayList<SettingItem> arrayList2 = this.mSettingItemsMap.get(Integer.valueOf(currentCameraId));
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= arrayList.size()) {
                break;
            }
            SettingItem settingItem = arrayList2.get(i2);
            ListPreference listPreference = arrayList.get(i2);
            updateSettingItem(settingItem, listPreference);
            settingItem.clearAllOverrideRecord();
            settingItem.setLastValue(settingItem.getDefaultValue());
            if (listPreference != null) {
                listPreference.setOverrideValue(null);
            }
            i = i2 + 1;
        }
        SettingItem settingItem2 = getSettingItem(24, this.mCameraId);
        if (settingItem2 != null) {
            settingItem2.setLastValue(null);
        }
        SettingItem settingItem3 = getSettingItem(49, this.mCameraId);
        if (settingItem3 != null) {
            settingItem3.setLastValue(null);
        }
        SettingItem settingItem4 = getSettingItem(26, this.mCameraId);
        if (settingItem4 != null) {
            settingItem4.setLastValue(null);
        }
        SettingItem settingItem5 = getSettingItem(62, this.mCameraId);
        if (settingItem5 != null) {
            settingItem5.setLastValue(null);
        }
        SettingItem settingItem6 = getSettingItem(63, this.mCameraId);
        if (settingItem6 != null) {
            settingItem6.setLastValue(null);
        }
        SettingItem settingItem7 = getSettingItem(59, this.mCameraId);
        if (settingItem7 != null) {
            settingItem7.setLastValue(null);
        }
        SettingItem settingItem8 = getSettingItem(38, this.mCameraId);
        if (settingItem8 != null) {
            settingItem8.setLastValue(null);
        }
        SettingItem settingItem9 = getSettingItem(39, this.mCameraId);
        if (settingItem9 != null) {
            settingItem9.setLastValue(null);
        }
        SettingItem settingItem10 = getSettingItem(20, this.mCameraId);
        if (settingItem10 != null) {
            settingItem10.setLastValue(null);
        }
        overrideSettingByIntent();
    }

    public SettingItem getSettingItem(String str) {
        return getSettingItem(SettingConstants.getSettingId(str));
    }

    public SettingItem getSettingItem(int i) {
        return getSettingItem(i, this.mICameraDeviceManager.getCurrentCameraId());
    }

    public SettingItem getSettingItem(int i, int i2) {
        ArrayList<SettingItem> arrayList = this.mSettingItemsMap.get(Integer.valueOf(i2));
        if (arrayList == null) {
            return null;
        }
        return arrayList.get(i);
    }

    public PreferenceGroup getPreferenceGroup() {
        return this.mPreferencesGroupMap.get(Integer.valueOf(this.mICameraDeviceManager.getCurrentCameraId()));
    }

    public ListPreference getListPreference(int i) {
        ArrayList<ListPreference> arrayList = this.mPreferencesMap.get(Integer.valueOf(this.mICameraDeviceManager.getCurrentCameraId()));
        if (arrayList == null) {
            Log.m32e("SettingGenerator", "Call setting before setting updated, return null");
            return null;
        }
        return arrayList.get(i);
    }

    public void restoreSetting(int i) {
        ArrayList<String> mTKSupportedVideoQuality;
        ArrayList<SettingItem> arrayList = this.mSettingItemsMap.get(Integer.valueOf(i));
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                SettingItem settingItem = arrayList.get(i2);
                settingItem.setValue(settingItem.getDefaultValue());
            }
        }
        ArrayList<ListPreference> arrayList2 = this.mPreferencesMap.get(Integer.valueOf(i));
        if (arrayList2 != null) {
            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                ListPreference listPreference = arrayList2.get(i3);
                if (listPreference != null) {
                    listPreference.setOverrideValue(null, false);
                }
            }
        }
        SettingItem settingItem2 = getSettingItem("pref_video_quality_key");
        ListPreference listPreference2 = settingItem2.getListPreference();
        if (listPreference2 != null && (mTKSupportedVideoQuality = getMTKSupportedVideoQuality()) != null && mTKSupportedVideoQuality.size() > 0) {
            listPreference2.filterUnsupported(mTKSupportedVideoQuality);
            settingItem2.setDefaultValue(mTKSupportedVideoQuality.get(0));
            settingItem2.setLastValue(null);
            listPreference2.setValue(mTKSupportedVideoQuality.get(0));
        }
        SharedPreferences sharedPreferences = this.mPrefTransfer.getSharedPreferences("pref_camera_picturesize_ratio_key");
        SettingItem settingItem3 = getSettingItem("pref_camera_picturesize_ratio_key");
        ListPreference listPreference3 = settingItem3.getListPreference();
        if (listPreference3 != null) {
            String string = sharedPreferences.getString("pref_camera_picturesize_ratio_key", String.valueOf(1.3333333333333333d));
            listPreference3.setValue(string);
            settingItem3.setValue(string);
            settingItem3.setDefaultValue(string);
            settingItem3.setLastValue(null);
        }
        ArrayList<String> supportedFaceBeautyMode = getSupportedFaceBeautyMode();
        if (supportedFaceBeautyMode != null && supportedFaceBeautyMode.size() > 0) {
            String str = supportedFaceBeautyMode.get(0);
            SettingItem settingItem4 = getSettingItem("pref_face_beauty_multi_mode_key");
            settingItem4.setValue(str);
            settingItem4.setDefaultValue(str);
            ListPreference listPreference4 = settingItem4.getListPreference();
            if (listPreference4 != null) {
                listPreference4.setValue(str);
            }
        }
        SettingItem settingItem5 = getSettingItem("pref_camera_zsd_key");
        ListPreference listPreference5 = settingItem5.getListPreference();
        if (settingItem5.isEnable()) {
            listPreference5.setValue(settingItem5.getDefaultValue());
        }
        SettingItem settingItem6 = getSettingItem("pref_video_stabilization_key");
        ListPreference listPreference6 = settingItem6.getListPreference();
        if (settingItem6.isEnable()) {
            listPreference6.setValue(settingItem6.getDefaultValue());
        }
        SettingItem settingItem7 = getSettingItem("pref_live_focus_key");
        ListPreference listPreference7 = settingItem7.getListPreference();
        if (settingItem7.isEnable()) {
            listPreference7.setValue(settingItem7.getDefaultValue());
        }
        SettingItem settingItem8 = getSettingItem("pref_camera_antibanding_key");
        ListPreference listPreference8 = settingItem8.getListPreference();
        if (settingItem8.isEnable()) {
            listPreference8.setValue(settingItem8.getDefaultValue());
        }
        SettingItem settingItem9 = getSettingItem("capture_mode_key");
        if (settingItem9 != null) {
            settingItem9.setLastValue(null);
        }
        overrideSettingByIntent();
    }

    private void createSettingItems() {
        int numberOfCameras = this.mICameraDeviceManager.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            ArrayList<SettingItem> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < 64; i2++) {
                SettingItem settingItem = new SettingItem(i2);
                String settingKey = SettingConstants.getSettingKey(i2);
                int settingType = SettingConstants.getSettingType(i2);
                settingItem.setKey(settingKey);
                settingItem.setType(settingType);
                arrayList.add(settingItem);
            }
            this.mSettingItemsMap.put(Integer.valueOf(i), arrayList);
        }
    }

    private void createPreferences(PreferenceGroup preferenceGroup, int i) throws NumberFormatException {
        ArrayList<ListPreference> arrayList = this.mPreferencesMap.get(Integer.valueOf(i));
        this.mSupportedImageProperties = new ArrayList();
        this.mSupportedFaceBeautyProperties = new ArrayList();
        if (arrayList == null) {
            ArrayList<ListPreference> arrayList2 = new ArrayList<>();
            ArrayList<SettingItem> arrayList3 = this.mSettingItemsMap.get(Integer.valueOf(i));
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= 64) {
                    break;
                }
                ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(SettingConstants.getSettingKey(i3));
                arrayList2.add(listPreferenceFindPreference);
                arrayList3.get(i3).setListPreference(listPreferenceFindPreference);
                i2 = i3 + 1;
            }
            this.mPreferencesMap.put(Integer.valueOf(i), arrayList2);
            arrayList = arrayList2;
        }
        filterPreferences(arrayList, i);
    }

    private void filterPreferences(ArrayList<ListPreference> arrayList, int i) throws NumberFormatException {
        ListPreference listPreference;
        ArrayList<SettingItem> arrayList2 = this.mSettingItemsMap.get(Integer.valueOf(i));
        limitPreferencesByIntent();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < arrayList.size()) {
                ListPreference listPreference2 = arrayList.get(i3);
                if (filterPreference(listPreference2)) {
                    arrayList.set(i3, null);
                    listPreference = null;
                } else {
                    listPreference = listPreference2;
                }
                updateSettingItem(arrayList2.get(i3), listPreference);
                i2 = i3 + 1;
            } else {
                overrideSettingByIntent();
                return;
            }
        }
    }

    private void updateSettingItem(SettingItem settingItem, ListPreference listPreference) throws NumberFormatException {
        int settingId = settingItem.getSettingId();
        int settingType = SettingConstants.getSettingType(settingId);
        String defaultValue = settingItem.getDefaultValue();
        switch (settingType) {
            case 0:
            case 1:
                String defaultValue2 = SettingDataBase.getDefaultValue(settingId);
                if (!this.mIModuleCtrl.isNonePickIntent() && 37 == settingId) {
                    defaultValue2 = Integer.toString(0);
                }
                settingItem.setDefaultValue(defaultValue2);
                settingItem.setValue(defaultValue2);
                break;
            case 2:
            case 3:
                this.mICameraDevice.getParameters();
                if (listPreference != null) {
                    listPreference.reloadValue();
                    if (defaultValue == null) {
                        defaultValue = generateDefaultValue(settingItem.getKey(), this.mICameraDevice.getParameters(), listPreference);
                    }
                    settingItem.setDefaultValue(defaultValue);
                    settingItem.setValue(listPreference.getValue());
                    break;
                } else if (settingItem.getKey().equals("pref_camera_picturesize_ratio_key")) {
                    settingItem.setEnable(true);
                    break;
                } else {
                    settingItem.setEnable(false);
                    break;
                }
        }
    }

    private String generateDefaultValue(String str, Parameters parameters, ListPreference listPreference) throws NumberFormatException {
        ArrayList<String> supportedFaceBeautyMode;
        ArrayList<String> mTKSupportedVideoQuality;
        List<String> listBuildPreviewRatios;
        if ("pref_camera_picturesize_ratio_key".equals(str) && (listBuildPreviewRatios = SettingUtils.buildPreviewRatios(this.mContext, parameters)) != null && listBuildPreviewRatios.size() > 0) {
            return listBuildPreviewRatios.get(0);
        }
        if ("pref_camera_picturesize_key".equals(str)) {
            getSettingItem("pref_camera_picturesize_ratio_key");
            List<String> listBuildSupportedPictureSizeByRatio = SettingUtils.buildSupportedPictureSizeByRatio(parameters, "1.3333");
            if (listBuildSupportedPictureSizeByRatio != null && listBuildSupportedPictureSizeByRatio.size() > 0) {
                return listBuildSupportedPictureSizeByRatio.get(listBuildSupportedPictureSizeByRatio.size() - 1);
            }
        }
        if ("pref_video_quality_key".equals(str) && (mTKSupportedVideoQuality = getMTKSupportedVideoQuality()) != null && mTKSupportedVideoQuality.size() > 0) {
            return mTKSupportedVideoQuality.get(0);
        }
        if ("pref_face_beauty_multi_mode_key".equals(str) && (supportedFaceBeautyMode = getSupportedFaceBeautyMode()) != null && supportedFaceBeautyMode.size() > 0) {
            return supportedFaceBeautyMode.get(0);
        }
        if ("pref_camera_zsd_key".equals(str)) {
            return SettingUtils.getZsdDefaultValue();
        }
        if ("pref_camera_antibanding_key".equals(str)) {
            return SettingUtils.getAntiBandingDefaultValue();
        }
        return listPreference.getDefaultValue();
    }

    /* JADX WARN: Removed duplicated region for block: B:84:0x01f8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean filterPreference(com.mediatek.camera.setting.preference.ListPreference r9) throws android.content.res.Resources.NotFoundException, java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 784
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.SettingGenerator.filterPreference(com.mediatek.camera.setting.preference.ListPreference):boolean");
    }

    private void limitPreferencesByIntent() {
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            ArrayList<ListPreference> arrayList = this.mPreferencesMap.get(Integer.valueOf(this.mICameraDeviceManager.getCurrentCameraId()));
            for (int i : SettingConstants.UN_SUPPORT_BY_3RDPARTY) {
                arrayList.set(i, null);
            }
        }
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        if (currentCameraId == this.mICameraDeviceManager.getFrontCameraId()) {
            ArrayList<ListPreference> arrayList2 = this.mPreferencesMap.get(Integer.valueOf(currentCameraId));
            for (int i2 : SettingConstants.UN_SUPPORT_BY_FRONT_CAMERA) {
                arrayList2.set(i2, null);
            }
        }
    }

    private void overrideSettingByIntent() {
        if (!this.mIModuleCtrl.isNonePickIntent()) {
            for (int i : SettingConstants.SUPPORT_BY_3RDPARTY_BUT_HIDDEN) {
                SettingItem settingItem = getSettingItem(i);
                ListPreference listPreference = settingItem.getListPreference();
                if (listPreference != null) {
                    listPreference.setVisibled(false);
                }
                if (17 != i && 18 != i) {
                    settingItem.setValue(SettingDataBase.getDefaultValue(i));
                }
            }
        }
    }

    private void updateSettingItem(String str, ListPreference listPreference) {
        if (listPreference == null) {
            return;
        }
        CharSequence[] entries = listPreference.getEntries();
        CharSequence[] entryValues = listPreference.getEntryValues();
        int length = entries.length;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < length; i++) {
            arrayList.add(entries[i]);
            arrayList2.add(entryValues[i]);
        }
        int size = arrayList2.size();
        listPreference.setOriginalEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[size]));
        listPreference.setOriginalEntries((CharSequence[]) arrayList.toArray(new CharSequence[size]));
    }

    private boolean filterUnsupportedOptions(ListPreference listPreference, List<String> list, int i) {
        return filterUnsupportedOptions(listPreference, list, true, i);
    }

    private boolean filterUnsupportedOptions(ListPreference listPreference, List<String> list, boolean z, int i) {
        if (list != null) {
            listPreference.filterUnsupported(list);
        }
        if (listPreference.getEntryValues().length == 1) {
            SettingItem settingItem = getSettingItem(i);
            CharSequence[] entryValues = listPreference.getEntryValues();
            settingItem.setDefaultValue(entryValues[0].toString());
            settingItem.setValue(entryValues[0].toString());
        }
        if (list == null || list.size() <= 1 || listPreference.getEntries().length <= 1) {
            return true;
        }
        resetIfInvalid(listPreference, z);
        return false;
    }

    private boolean filterUnsupportedEntries(ListPreference listPreference, List<String> list, boolean z, int i) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        listPreference.filterUnsupportedEntries(list);
        if (listPreference.getEntries().length <= 0) {
            return true;
        }
        resetIfInvalid(listPreference, z);
        return false;
    }

    private void buildSupportedListperference(List<String> list, ListPreference listPreference) {
        if (listPreference != null && list != null) {
            list.add(listPreference.getKey());
        }
    }

    private boolean filterDisabledOptions(ListPreference listPreference, List<String> list, boolean z, int i) {
        if (list == null || list.size() < 1) {
            return true;
        }
        listPreference.filterDisabled(list);
        if (listPreference.getEntries().length < 1) {
            return true;
        }
        resetIfInvalid(listPreference, z);
        return false;
    }

    private void resetIfInvalid(ListPreference listPreference, boolean z) {
        if (listPreference.findIndexOfValue(listPreference.getValue()) == -1) {
            if (z) {
                listPreference.setValueIndex(0);
            } else if (listPreference.getEntryValues() != null && listPreference.getEntryValues().length > 0) {
                listPreference.setValueIndex(listPreference.getEntryValues().length - 1);
            }
        }
    }

    private static List<String> sizeListToStringList(List<Camera.Size> list) {
        ArrayList arrayList = new ArrayList();
        for (Camera.Size size : list) {
            arrayList.add(String.format(Locale.ENGLISH, "%dx%d", Integer.valueOf(size.width), Integer.valueOf(size.height)));
        }
        return arrayList;
    }

    private boolean buildCameraId(ListPreference listPreference, int i) {
        if (this.mCameraInfo.length < 2) {
            return true;
        }
        CharSequence[] charSequenceArr = new CharSequence[2];
        for (int i2 = 0; i2 < this.mCameraInfo.length; i2++) {
            char c = this.mCameraInfo[i2].facing == 1 ? (char) 1 : (char) 0;
            if (charSequenceArr[c] == null) {
                charSequenceArr[c] = "" + i2;
                if (charSequenceArr[c == 1 ? (char) 0 : (char) 1] != null) {
                    break;
                }
            }
        }
        listPreference.setEntryValues(charSequenceArr);
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x009b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.util.ArrayList<java.lang.String> getMTKSupportedVideoQuality() {
        /*
            r10 = this;
            r3 = 1
            r4 = 0
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            int r0 = r10.mCameraId
            java.lang.String[] r0 = com.mediatek.camera.setting.SettingGenerator.NORMAL_SUPPORT_QUALIYS_STRING
            int r7 = r0.length
            r0 = 4
            int r1 = r10.mCameraId
            if (r1 != r3) goto L9f
            r0 = 2
            r1 = r0
        L13:
            r2 = r4
            r5 = r4
        L15:
            if (r5 >= r7) goto L9a
            if (r2 >= r1) goto L9a
            int r0 = r10.mCameraId
            int[] r8 = com.mediatek.camera.setting.SettingGenerator.NORMAL_SUPPORT_QUALIYS
            r8 = r8[r5]
            boolean r0 = android.media.CamcorderProfile.hasProfile(r0, r8)
            if (r0 == 0) goto L9b
            if (r5 != 0) goto L58
            com.mediatek.camera.platform.ICameraDeviceManager$ICameraDevice r0 = r10.mICameraDevice
            com.mediatek.camera.platform.Parameters r0 = r0.getParameters()
            java.util.List r0 = r0.getSupportedVideoSizes()
            java.util.Iterator r8 = r0.iterator()
        L35:
            boolean r0 = r8.hasNext()
            if (r0 == 0) goto L9d
            java.lang.Object r0 = r8.next()
            android.hardware.Camera$Size r0 = (android.hardware.Camera.Size) r0
            int r0 = r0.width
            r9 = 3840(0xf00, float:5.381E-42)
            if (r0 < r9) goto L35
            r0 = r3
        L48:
            if (r0 == 0) goto L9b
            int r0 = r2 + 1
            java.lang.String[] r2 = com.mediatek.camera.setting.SettingGenerator.NORMAL_SUPPORT_QUALIYS_STRING
            r2 = r2[r5]
            r6.add(r2)
        L53:
            int r2 = r5 + 1
            r5 = r2
            r2 = r0
            goto L15
        L58:
            int r0 = r2 + 1
            java.lang.String r2 = "SettingGenerator"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "supportSize = "
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.StringBuilder r8 = r8.append(r0)
            java.lang.String r8 = r8.toString()
            com.mediatek.camera.util.Log.m34i(r2, r8)
            java.lang.String r2 = "SettingGenerator"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "NORMAL_SUPPORT_QUALIYS_STRING[i] = "
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String[] r9 = com.mediatek.camera.setting.SettingGenerator.NORMAL_SUPPORT_QUALIYS_STRING
            r9 = r9[r5]
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            com.mediatek.camera.util.Log.m34i(r2, r8)
            java.lang.String[] r2 = com.mediatek.camera.setting.SettingGenerator.NORMAL_SUPPORT_QUALIYS_STRING
            r2 = r2[r5]
            r6.add(r2)
            goto L53
        L9a:
            return r6
        L9b:
            r0 = r2
            goto L53
        L9d:
            r0 = r4
            goto L48
        L9f:
            r1 = r0
            goto L13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.setting.SettingGenerator.getMTKSupportedVideoQuality():java.util.ArrayList");
    }

    private ArrayList<String> getRefocusSupportedVideoQuality() {
        ArrayList<String> arrayList = new ArrayList<>();
        int i = this.mCameraId;
        if (CamcorderProfile.hasProfile(i, 3101)) {
            arrayList.add(VIDEO_QUALITY_VSDOF_MEDIUM);
        }
        if (CamcorderProfile.hasProfile(i, 3102)) {
            arrayList.add(VIDEO_QUALITY_VSDOF_HIGH);
        }
        return arrayList;
    }

    private boolean buildExposureCompensation(ListPreference listPreference, int i) {
        Parameters parameters = this.mICameraDevice.getParameters();
        int maxExposureCompensation = parameters.getMaxExposureCompensation();
        int minExposureCompensation = parameters.getMinExposureCompensation();
        if (maxExposureCompensation == 0 && minExposureCompensation == 0) {
            return true;
        }
        float exposureCompensationStep = parameters.getExposureCompensationStep();
        int iFloor = (int) FloatMath.floor(maxExposureCompensation * exposureCompensationStep);
        ArrayList arrayList = new ArrayList();
        for (int iCeil = (int) FloatMath.ceil(minExposureCompensation * exposureCompensationStep); iCeil <= iFloor; iCeil++) {
            arrayList.add(String.valueOf(Integer.toString(Math.round(iCeil / exposureCompensationStep))));
        }
        listPreference.filterUnsupported(arrayList);
        return false;
    }

    private boolean buildFaceBeautyPreference(String str, ListPreference listPreference, int i) {
        Parameters parameters = this.mICameraDevice.getParameters();
        int maxLevel = ParametersHelper.getMaxLevel(parameters, str);
        int minLevel = ParametersHelper.getMinLevel(parameters, str);
        if (maxLevel == 0 && minLevel == 0) {
            return true;
        }
        listPreference.setEntryValues(new CharSequence[]{String.valueOf(minLevel), String.valueOf(0), String.valueOf(maxLevel)});
        return false;
    }

    private ArrayList<String> getMTKSupportedSlowMotionVideoQuality() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (!this.mIModuleCtrl.isNonePickIntent() || (!this.mICameraContext.getFeatureConfig().isSlowMotionSupport())) {
            return arrayList;
        }
        List<SlowMotionParam> supportedPreviewSizesAndFps = getSupportedPreviewSizesAndFps();
        int length = SLOW_MOTION_SUPPORT_QUALIYS.length;
        for (int i = 0; i < length; i++) {
            if (CamcorderProfile.hasProfile(this.mCameraId, SLOW_MOTION_SUPPORT_QUALIYS[i]) && isParametersSupport(SLOW_MOTION_SUPPORT_QUALIYS[i], supportedPreviewSizesAndFps)) {
                arrayList.add(SLOW_MOTION_SUPPORT_QUALIYS_STRING[i]);
            }
        }
        if (arrayList.size() == 1) {
            SharedPreferences.Editor editorEdit = this.mPrefTransfer.getSharedPreferences("pref_slow_motion_video_quality_key").edit();
            editorEdit.putString("pref_slow_motion_video_quality_key", arrayList.get(0));
            editorEdit.apply();
            getSettingItem(SettingConstants.getSettingId("pref_slow_motion_video_quality_key")).setValue(arrayList.get(0));
        }
        Log.m31d("SettingGenerator", "supported slowMotion quality = " + arrayList);
        return arrayList;
    }

    private Integer getMaxPreviewFrameRate() {
        return (Integer) Collections.max(this.mICameraDevice.getParameters().getSupportedPreviewFrameRates());
    }

    private List<SlowMotionParam> getSupportedPreviewSizesAndFps() {
        return splitSize(this.mICameraDevice.getParameters().get("hsvr-size-fps-values"));
    }

    private ArrayList<SlowMotionParam> splitSize(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList<SlowMotionParam> arrayList = new ArrayList<>();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            SlowMotionParam slowMotionParamStrToSize = strToSize((String) it.next());
            if (slowMotionParamStrToSize != null) {
                arrayList.add(slowMotionParamStrToSize);
            }
        }
        if (arrayList.size() == 0) {
            return null;
        }
        return arrayList;
    }

    private SlowMotionParam strToSize(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(120);
        int iLastIndexOf = str.lastIndexOf(120);
        if (iIndexOf != -1 && iLastIndexOf != -1) {
            return new SlowMotionParam(Integer.parseInt(str.substring(0, iIndexOf)), Integer.parseInt(str.substring(iIndexOf + 1, iLastIndexOf)), Integer.parseInt(str.substring(iLastIndexOf + 1)));
        }
        Log.m32e("SettingGenerator", "Invalid size parameter string=" + str);
        return null;
    }

    public boolean isParametersSupport(int i, List<SlowMotionParam> list) {
        CamcorderProfile camcorderProfile = CamcorderProfile.get(this.mICameraDeviceManager.getCurrentCameraId(), i);
        if (list == null) {
            Log.m35v("SettingGenerator", "slowMotionParam = " + list);
            return false;
        }
        if (camcorderProfile != null) {
            for (SlowMotionParam slowMotionParam : list) {
                if (slowMotionParam.width == camcorderProfile.videoFrameWidth && slowMotionParam.height == camcorderProfile.videoFrameHeight && slowMotionParam.fps == camcorderProfile.videoFrameRate) {
                    return true;
                }
            }
        }
        return false;
    }

    private class SlowMotionParam {
        private int fps;
        private int height;
        private int width;

        public SlowMotionParam(int i, int i2, int i3) {
            this.width = i;
            this.height = i2;
            this.fps = i3;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SlowMotionParam)) {
                return false;
            }
            SlowMotionParam slowMotionParam = (SlowMotionParam) obj;
            return this.width == slowMotionParam.width && this.height == slowMotionParam.height && this.fps == slowMotionParam.fps;
        }

        public int hashCode() {
            return (this.width * 32713) + this.height;
        }
    }

    private ArrayList<String> getSupportedFaceBeautyMode() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (isCFBSupported() && this.mICameraContext.getFeatureConfig().isVfbEnable()) {
            if (!isOnlyMultiFaceBeautySupported()) {
                arrayList.add(this.mContext.getResources().getString(R.string.face_beauty_single_mode));
            }
            arrayList.add(this.mContext.getResources().getString(R.string.face_beauty_multi_mode));
            arrayList.add(this.mContext.getResources().getString(R.string.pref_face_beauty_mode_off));
        }
        return arrayList;
    }

    public boolean isOnlyMultiFaceBeautySupported() {
        return "false".equals(this.mICameraDevice.getParameters().get("fb-extreme-beauty-supported"));
    }

    private boolean isCFBSupported() {
        return this.mICameraContext.getFeatureConfig().isCfbEnable();
    }

    private boolean buildPictureSizeEntries(ListPreference listPreference, List<String> list) throws Resources.NotFoundException {
        DecimalFormat decimalFormat;
        String str;
        boolean z;
        if (list == null || list.size() == 0) {
            return true;
        }
        sortSizesInAscending(list);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (FeatureSwitcher.isTablet()) {
            decimalFormat = new DecimalFormat("##0.#");
        } else {
            decimalFormat = new DecimalFormat("##0");
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                Point size = SettingUtils.getSize(list.get(i2));
                String string = this.mContext.getResources().getString(R.string.setting_summary_megapixels, decimalFormat.format((size.x * size.y) / 1000000.0d));
                if (size.x * size.y == 307200) {
                    str = "VGA";
                } else {
                    str = size.x * size.y == 76800 ? "QVGA" : string;
                }
                int iIndexOf = arrayList2.indexOf(str);
                if (iIndexOf <= 0) {
                    z = false;
                } else {
                    for (int i3 = iIndexOf; i3 < arrayList2.size(); i3++) {
                        if (SettingUtils.toleranceRatio(size, SettingUtils.getSize((String) arrayList.get(i3)))) {
                            z = true;
                            iIndexOf = i3;
                            break;
                        }
                    }
                    z = false;
                }
                if (z) {
                    arrayList.set(iIndexOf, list.get(i2));
                } else {
                    arrayList.add(list.get(i2));
                    arrayList2.add(str);
                }
                i = i2 + 1;
            } else {
                CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
                CharSequence[] charSequenceArr2 = new CharSequence[arrayList.size()];
                listPreference.setOriginalEntryValues((CharSequence[]) arrayList.toArray(charSequenceArr));
                listPreference.setOriginalEntries((CharSequence[]) arrayList2.toArray(charSequenceArr2));
                listPreference.filterUnsupported(list);
                return false;
            }
        }
    }

    private void sortSizesInAscending(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            String str = list.get(0);
            int i2 = 0;
            Point size = SettingUtils.getSize(str);
            String str2 = str;
            String str3 = null;
            for (int i3 = 0; i3 < list.size() - i; i3++) {
                str3 = list.get(i3);
                Point size2 = SettingUtils.getSize(str3);
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
}
