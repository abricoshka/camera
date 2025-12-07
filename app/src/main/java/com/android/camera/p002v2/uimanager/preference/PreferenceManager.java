package com.android.camera.p002v2.uimanager.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Size;
import com.android.camera.p002v2.app.SettingAgent;
import com.android.camera.p002v2.util.CameraUtil;
import com.android.camera.p002v2.util.SettingKeys;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class PreferenceManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PreferenceManager.class.getSimpleName());
    private Activity mActivity;
    private Context mContext;
    private boolean mIsNonePickIntent;
    private SettingAgent mSettingAgent;
    private Map<Integer, PreferenceGroup> mPreferenceGroupMap = new HashMap();
    private int mCameraId = 0;

    public PreferenceManager(Activity activity, SettingAgent settingAgent) {
        this.mContext = null;
        this.mSettingAgent = null;
        this.mIsNonePickIntent = true;
        LogHelper.m26i(TAG, "[PreferenceManager], instructor");
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        this.mSettingAgent = settingAgent;
        Intent intent = this.mActivity.getIntent();
        String action = intent != null ? intent.getAction() : null;
        LogHelper.m26i(TAG, "[PreferenceManager], action:" + action);
        if ("android.media.action.IMAGE_CAPTURE".equals(action) || "android.media.action.VIDEO_CAPTURE".equals(action)) {
            this.mIsNonePickIntent = false;
        }
    }

    public void initializePreferences(int i, int i2) throws Resources.NotFoundException {
        LogHelper.m26i(TAG, "[initializePreferences], start, cameraId:" + i2);
        String[] strArr = new String[SettingKeys.KEYS_FOR_SETTING.length];
        for (int i3 = 0; i3 < SettingKeys.KEYS_FOR_SETTING.length; i3++) {
            strArr[i3] = SettingKeys.KEYS_FOR_SETTING[i3];
        }
        if (!this.mIsNonePickIntent) {
            for (int i4 = 0; i4 < SettingKeys.UN_SUPPORT_BY_3RDPARTY.length; i4++) {
                strArr[SettingKeys.UN_SUPPORT_BY_3RDPARTY[i4]] = null;
            }
        }
        this.mCameraId = i2;
        if (this.mPreferenceGroupMap.get(Integer.valueOf(i2)) == null) {
            this.mPreferenceGroupMap.put(Integer.valueOf(i2), (PreferenceGroup) new PreferenceInflater(this.mContext).inflate(i));
            filterPreferences(strArr, i2);
        }
        this.mSettingAgent.doSettingChange("pref_camera_id_key", String.valueOf(i2));
        configureSettings(strArr, i2);
        LogHelper.m26i(TAG, "[initializePreferences], end");
    }

    public ListPreference getListPreference(String str) {
        if (str == null) {
            return null;
        }
        if (!this.mIsNonePickIntent) {
            int settingId = SettingKeys.getSettingId(str);
            for (int i = 0; i < SettingKeys.UN_SUPPORT_BY_3RDPARTY.length; i++) {
                if (settingId == SettingKeys.UN_SUPPORT_BY_3RDPARTY[i]) {
                    return null;
                }
            }
        }
        return this.mPreferenceGroupMap.get(Integer.valueOf(this.mCameraId)).findPreference(str);
    }

    public void updateSettingResult(Map<String, String> map, Map<String, String> map2) {
        LogHelper.m26i(TAG, "[updateSettingResult]");
        if (map != null) {
            PreferenceGroup preferenceGroup = this.mPreferenceGroupMap.get(Integer.valueOf(this.mCameraId));
            for (String str : map.keySet()) {
                String str2 = map.get(str);
                String strBuildEnabledList = CameraUtil.buildEnabledList(map2.get(str), str2);
                ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(str);
                if (listPreferenceFindPreference != null && listPreferenceFindPreference.isVisibled()) {
                    listPreferenceFindPreference.setValue(str2);
                    listPreferenceFindPreference.setOverrideValue(strBuildEnabledList);
                }
            }
        }
    }

    public void restoreSetting() {
        String[] strArr = new String[SettingKeys.KEYS_FOR_SETTING.length];
        for (int i = 0; i < SettingKeys.KEYS_FOR_SETTING.length; i++) {
            strArr[i] = SettingKeys.KEYS_FOR_SETTING[i];
        }
        if (!this.mIsNonePickIntent) {
            for (int i2 = 0; i2 < SettingKeys.UN_SUPPORT_BY_3RDPARTY.length; i2++) {
                strArr[SettingKeys.UN_SUPPORT_BY_3RDPARTY[i2]] = null;
            }
            for (int i3 = 0; i3 < SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN.length; i3++) {
                strArr[SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN[i3]] = null;
            }
        }
        Iterator<Integer> it = this.mPreferenceGroupMap.keySet().iterator();
        while (it.hasNext()) {
            this.mSettingAgent.clearSharedPreferencesValue(strArr, it.next().toString());
        }
        HashMap map = new HashMap();
        PreferenceGroup preferenceGroup = this.mPreferenceGroupMap.get(Integer.valueOf(this.mCameraId));
        for (String str : strArr) {
            ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(str);
            if (listPreferenceFindPreference != null) {
                String defaultValue = listPreferenceFindPreference.getDefaultValue();
                listPreferenceFindPreference.setValue(defaultValue);
                map.put(str, defaultValue);
            }
        }
        map.remove("pref_camera_id_key");
        this.mSettingAgent.doSettingChange(map);
    }

    public void clearSharedPreferencesValue() {
        int[] iArr;
        Iterator<Integer> it = this.mPreferenceGroupMap.keySet().iterator();
        if (this.mIsNonePickIntent) {
            iArr = SettingKeys.RESET_SETTING_ITEMS;
        } else {
            iArr = SettingKeys.THIRDPART_RESET_SETTING_ITEMS;
        }
        String[] strArr = new String[iArr.length];
        for (int i = 0; i < iArr.length; i++) {
            strArr[i] = SettingKeys.getSettingKey(iArr[i]);
        }
        while (it.hasNext()) {
            this.mSettingAgent.clearSharedPreferencesValue(strArr, it.next().toString());
        }
    }

    private void filterPreferences(String[] strArr, int i) throws Resources.NotFoundException {
        String str;
        PreferenceGroup preferenceGroup = this.mPreferenceGroupMap.get(Integer.valueOf(i));
        for (String str2 : strArr) {
            ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(str2);
            if (listPreferenceFindPreference != null && SettingKeys.getSettingType(str2) != 0) {
                List<String> supportedValues = this.mSettingAgent.getSupportedValues(str2, String.valueOf(this.mCameraId));
                if (supportedValues != null) {
                    str = null;
                    for (int i2 = 0; i2 < supportedValues.size(); i2++) {
                        str = str + supportedValues.get(i2) + ",";
                    }
                } else {
                    str = null;
                }
                LogHelper.m23d(TAG, "key:" + str2 + ", supportedValue:" + str);
                if ("pref_camera_picturesize_key".equals(str2)) {
                    buildPictureSizeEntries(listPreferenceFindPreference, supportedValues);
                } else {
                    filterUnSupportedValues(listPreferenceFindPreference, supportedValues);
                }
            }
        }
        filterGroupListPrference(preferenceGroup, "pref_camera_image_properties_key");
        if (!this.mIsNonePickIntent) {
            for (int i3 = 0; i3 < SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN.length; i3++) {
                ListPreference listPreferenceFindPreference2 = preferenceGroup.findPreference(SettingKeys.getSettingKey(SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN[i3]));
                if (listPreferenceFindPreference2 != null) {
                    listPreferenceFindPreference2.setVisibled(false);
                }
            }
        }
    }

    private void configureSettings(String[] strArr, int i) {
        ArrayList arrayList = new ArrayList();
        if (!this.mIsNonePickIntent) {
            for (int i2 = 0; i2 < SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN.length; i2++) {
                String settingKey = SettingKeys.getSettingKey(SettingKeys.SUPPORT_BY_3RDPARTY_BUT_HIDDEN[i2]);
                if (!"pref_camera_recordlocation_key".equals(settingKey) && (!"pref_video_quality_key".equals(settingKey))) {
                    arrayList.add(settingKey);
                }
            }
        }
        PreferenceGroup preferenceGroup = this.mPreferenceGroupMap.get(Integer.valueOf(i));
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (String str : strArr) {
            ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(str);
            if (listPreferenceFindPreference != null) {
                listPreferenceFindPreference.setOverrideValue(null);
                String defaultValue = listPreferenceFindPreference.getDefaultValue();
                if (defaultValue == null) {
                    List<String> supportedValues = this.mSettingAgent.getSupportedValues(str, String.valueOf(i));
                    if (supportedValues != null) {
                        defaultValue = supportedValues.get(0);
                    }
                    listPreferenceFindPreference.setDefaultValue(defaultValue);
                }
                linkedHashMap.put(str, defaultValue);
                String sharedPreferencesValue = this.mSettingAgent.getSharedPreferencesValue(str, String.valueOf(i));
                if (sharedPreferencesValue != null && (!arrayList.contains(str))) {
                    linkedHashMap.put(str, sharedPreferencesValue);
                }
            }
        }
        linkedHashMap.remove("pref_camera_id_key");
        this.mSettingAgent.configurateSetting(linkedHashMap);
    }

    private void buildPictureSizeEntries(ListPreference listPreference, List<String> list) throws Resources.NotFoundException {
        String str;
        boolean z;
        if (list == null || list.size() == 0) {
            return;
        }
        sortSizesInAscending(list);
        DecimalFormat decimalFormat = new DecimalFormat("##0");
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                Size size = CameraUtil.getSize(list.get(i2));
                if (size != null) {
                    String string = this.mContext.getResources().getString(R.string.setting_summary_megapixels, decimalFormat.format((size.getWidth() * size.getHeight()) / 1000000.0d));
                    if (size.getWidth() * size.getHeight() == 307200) {
                        str = "VGA";
                    } else {
                        str = size.getWidth() * size.getHeight() == 76800 ? "QVGA" : string;
                    }
                    int iIndexOf = arrayList2.indexOf(str);
                    if (iIndexOf <= 0 || !CameraUtil.toleranceRatio(size, CameraUtil.getSize((String) arrayList.get(iIndexOf)))) {
                        z = false;
                    } else {
                        z = true;
                    }
                    if (z) {
                        arrayList.set(iIndexOf, list.get(i2));
                    } else {
                        arrayList.add(list.get(i2));
                        arrayList2.add(str);
                    }
                }
                i = i2 + 1;
            } else {
                CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
                CharSequence[] charSequenceArr2 = new CharSequence[arrayList.size()];
                listPreference.setOriginalEntryValues((CharSequence[]) arrayList.toArray(charSequenceArr));
                listPreference.setOriginalEntries((CharSequence[]) arrayList2.toArray(charSequenceArr2));
                listPreference.filterUnsupported(list);
                return;
            }
        }
    }

    private void sortSizesInAscending(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            String str = list.get(0);
            Size size = CameraUtil.getSize(str);
            if (size != null) {
                int i2 = 0;
                Size size2 = size;
                String str2 = str;
                String str3 = null;
                for (int i3 = 0; i3 < list.size() - i; i3++) {
                    str3 = list.get(i3);
                    Size size3 = CameraUtil.getSize(str3);
                    if (size3 != null && size3.getWidth() * size3.getHeight() > size2.getWidth() * size2.getHeight()) {
                        i2 = i3;
                        size2 = size3;
                        str2 = str3;
                    }
                }
                list.set(i2, str3);
                list.set((list.size() - 1) - i, str2);
            }
        }
    }

    private void filterUnSupportedValues(ListPreference listPreference, List<String> list) {
        if (list != null) {
            listPreference.filterUnsupported(list);
        }
        if (list == null || list.size() <= 1) {
            listPreference.setVisibled(false);
        } else if (listPreference.getEntries().length <= 1) {
            listPreference.setVisibled(false);
        } else {
            resetIfInvalid(listPreference, true);
        }
    }

    private void filterGroupListPrference(PreferenceGroup preferenceGroup, String str) {
        ListPreference listPreferenceFindPreference = preferenceGroup.findPreference(str);
        if (listPreferenceFindPreference == null) {
            return;
        }
        CharSequence[] originalEntries = listPreferenceFindPreference.getOriginalEntries();
        if (originalEntries == null) {
            listPreferenceFindPreference.setVisibled(false);
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (CharSequence charSequence : originalEntries) {
            ListPreference listPreferenceFindPreference2 = preferenceGroup.findPreference(charSequence.toString());
            if (listPreferenceFindPreference2 != null && listPreferenceFindPreference2.isVisibled()) {
                arrayList.add(listPreferenceFindPreference2);
            }
        }
        if (arrayList.size() <= 0) {
            listPreferenceFindPreference.setVisibled(false);
        } else {
            listPreferenceFindPreference.setChildPreferences((ListPreference[]) arrayList.toArray(new ListPreference[arrayList.size()]));
        }
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
}
