package com.mediatek.camera.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.setting.preference.PreferenceGroup;
import com.mediatek.camera.setting.preference.SharedPreferencesTransfer;
import com.mediatek.camera.setting.rule.CommonRule;
import com.mediatek.camera.setting.rule.RuleContainer;
import com.mediatek.camera.util.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class SettingCtrl implements ISettingCtrl {
    private Context mContext;
    private SharedPreferences mGlobalPref;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private HashMap<Integer, SharedPreferences> mLocalPrefs;
    private SharedPreferencesTransfer mPrefTransfer;
    private SettingGenerator mSettingGenerator;
    private boolean mIsInitializedSettings = false;
    private ISettingRule[][] mRuleMatrix = (ISettingRule[][]) Array.newInstance((Class<?>) ISettingRule.class, 64, 64);

    public SettingCtrl(ICameraContext iCameraContext) {
        this.mICameraContext = iCameraContext;
        this.mContext = iCameraContext.getActivity();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mLocalPrefs = new HashMap<>(this.mICameraDeviceManager.getNumberOfCameras());
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void initializeSettings(int i, SharedPreferences sharedPreferences, SharedPreferences sharedPreferences2) throws NumberFormatException {
        this.mGlobalPref = sharedPreferences;
        this.mLocalPrefs.put(Integer.valueOf(this.mICameraDeviceManager.getCurrentCameraId()), sharedPreferences2);
        this.mPrefTransfer = new SharedPreferencesTransfer(sharedPreferences, sharedPreferences2);
        this.mSettingGenerator = new SettingGenerator(this.mICameraContext, this.mPrefTransfer);
        this.mSettingGenerator.createSettings(i);
        createRules();
        this.mIsInitializedSettings = true;
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public boolean isSettingsInitialized() {
        return this.mIsInitializedSettings;
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void onSettingChanged(String str, String str2) throws NumberFormatException {
        if ("pref_video_stabilization_key".equals(str)) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), str, "on".equals(str2) ? 1 : 0);
        }
        if (!this.mIsInitializedSettings) {
            Log.m36w("SettingCtrl", "[onSettingChanged] mIsInitializedSettings is false, return.");
            return;
        }
        if ("normal_key".equals(str)) {
            return;
        }
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(currentCameraId);
        if (this.mICameraDevice == null) {
            return;
        }
        Parameters parameters = this.mICameraDevice.getParameters();
        if ("pref_hdr_key".equals(str)) {
            getSetting(SettingConstants.getSettingId(str)).clearAllOverrideRecord();
        }
        onSettingChanged(parameters, currentCameraId, str, str2);
        if ("pref_camera_scenemode_key".equals(str)) {
            SettingItem setting = getSetting(4);
            ISettingRule iSettingRule = this.mRuleMatrix[4][24];
            if (iSettingRule != null && "on".equals(setting.getValue())) {
                iSettingRule.execute();
            }
        }
    }

    private void onSettingChanged(Parameters parameters, int i, String str, String str2) throws NumberFormatException {
        boolean z = false;
        int settingId = SettingConstants.getSettingId(str);
        SettingItem setting = getSetting(settingId);
        String lastValue = setting.getLastValue();
        if (str2 != null) {
            if (str2.equals(lastValue) && (!"pref_camera_picturesize_key".equals(str))) {
                return;
            }
            setting.setValue(str2);
            setting.setLastValue(str2);
            if (isNeedQueryByYAxis(str)) {
                List<SettingItem> listQueryConditionSettings = queryConditionSettings(str);
                int i2 = 0;
                while (true) {
                    if (i2 >= listQueryConditionSettings.size()) {
                        break;
                    }
                    SettingItem settingItem = listQueryConditionSettings.get(i2);
                    ISettingRule iSettingRule = this.mRuleMatrix[settingItem.getSettingId()][settingId];
                    if (iSettingRule != null) {
                        String defaultValue = settingItem.getDefaultValue();
                        String value = settingItem.getValue();
                        if (defaultValue != null && value != null && (!defaultValue.equals(value))) {
                            iSettingRule.execute();
                            z = true;
                            break;
                        }
                    }
                    i2++;
                }
            }
            if (!z) {
                if ("pref_camera_picturesize_ratio_key".equals(str)) {
                    SettingUtils.setPreviewSize(this.mContext, parameters, str2);
                } else {
                    switch (setting.getType()) {
                        case 1:
                        case 3:
                            if (setting.isEnable()) {
                                ParametersHelper.setParametersValue(parameters, i, str, str2);
                                break;
                            } else {
                                Log.m31d("SettingCtrl", "[onSettingChanged], setting is disable, key:" + str);
                                break;
                            }
                    }
                }
            }
            executeRule(parameters, i, str);
        }
    }

    private void executeRule(Parameters parameters, int i, String str) throws NumberFormatException {
        int i2 = 0;
        SettingItem settingItem = this.mSettingGenerator.getSettingItem(str);
        List<SettingItem> listQueryResultSettings = queryResultSettings(str);
        int settingId = SettingConstants.getSettingId(str);
        String value = settingItem.getValue();
        if (value != null && value.equals(settingItem.getDefaultValue())) {
            for (int i3 = 0; i3 < listQueryResultSettings.size(); i3++) {
                this.mRuleMatrix[settingId][listQueryResultSettings.get(i3).getSettingId()].execute();
            }
            while (i2 < listQueryResultSettings.size()) {
                SettingItem settingItem2 = listQueryResultSettings.get(i2);
                onSettingChanged(parameters, i, settingItem2.getKey(), settingItem2.getValue());
                i2++;
            }
            return;
        }
        while (i2 < listQueryResultSettings.size()) {
            SettingItem settingItem3 = listQueryResultSettings.get(i2);
            this.mRuleMatrix[settingId][settingItem3.getSettingId()].execute();
            onSettingChanged(parameters, i, settingItem3.getKey(), settingItem3.getValue());
            i2++;
        }
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void addRule(String str, String str2, ISettingRule iSettingRule) {
        int settingId = SettingConstants.getSettingId(str);
        this.mRuleMatrix[settingId][SettingConstants.getSettingId(str2)] = iSettingRule;
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void executeRule(String str, String str2) {
        int settingId = SettingConstants.getSettingId(str);
        ISettingRule iSettingRule = this.mRuleMatrix[settingId][SettingConstants.getSettingId(str2)];
        if (iSettingRule != null) {
            iSettingRule.execute();
        }
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public String getSettingValue(String str) {
        if (!this.mIsInitializedSettings) {
            return null;
        }
        SettingItem setting = getSetting(str);
        String value = setting.getValue();
        if (value == null) {
            return setting.getDefaultValue();
        }
        return value;
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public SettingItem getSetting(String str) {
        return getSetting(SettingConstants.getSettingId(str));
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public String getDefaultValue(String str) {
        if (this.mIsInitializedSettings) {
            return getSetting(str).getDefaultValue();
        }
        return null;
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void setSettingValue(String str, String str2, int i) {
        getSetting(SettingConstants.getSettingId(str), i).setValue(str2);
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public ListPreference getListPreference(String str) {
        if (!this.mIsInitializedSettings) {
            return null;
        }
        return this.mSettingGenerator.getListPreference(SettingConstants.getSettingId(str));
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public PreferenceGroup getPreferenceGroup() {
        return this.mSettingGenerator.getPreferenceGroup();
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void updateSetting(SharedPreferences sharedPreferences) throws NumberFormatException {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        this.mLocalPrefs.put(Integer.valueOf(currentCameraId), sharedPreferences);
        this.mPrefTransfer.updateLocalPreferences(sharedPreferences);
        this.mSettingGenerator.updatePreferences();
        synchronizeSetting();
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(currentCameraId);
        if (this.mICameraDevice == null) {
            return;
        }
        Parameters parameters = this.mICameraDevice.getParameters();
        for (int i = 0; i < 64; i++) {
            SettingItem settingItem = this.mSettingGenerator.getSettingItem(i);
            String key = settingItem.getKey();
            String value = settingItem.getValue();
            String lastValue = settingItem.getLastValue();
            if (settingItem.isEnable() && value != null && (!value.equals(lastValue))) {
                onSettingChanged(parameters, currentCameraId, key, value);
            }
        }
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void restoreSetting(int i) throws NumberFormatException {
        this.mSettingGenerator.restoreSetting(i);
        ICameraDeviceManager.ICameraDevice cameraDevice = this.mICameraDeviceManager.getCameraDevice(i);
        if ((cameraDevice != null ? cameraDevice.getParameters() : null) != null) {
            for (int i2 = 0; i2 < 64; i2++) {
                SettingItem settingItem = this.mSettingGenerator.getSettingItem(i2);
                String key = settingItem.getKey();
                String value = settingItem.getValue();
                String lastValue = settingItem.getLastValue();
                if (settingItem.getDefaultValue() != null && value != null && (!value.equals(lastValue))) {
                    onSettingChanged(key, value);
                }
            }
        }
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public String getCameraMode(String str) {
        return SettingDataBase.getSettingResetValue(37, SettingDataBase.getRestrictionMatrix()[37][SettingDataBase.getSettingColumn(SettingConstants.getSettingId(str))]);
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public void resetSetting() {
        System.currentTimeMillis();
        resetSettings(this.mGlobalPref);
        int numberOfCameras = this.mICameraDeviceManager.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            SharedPreferences sharedPreferences = this.mLocalPrefs.get(Integer.valueOf(i));
            if (sharedPreferences != null) {
                resetSettings(sharedPreferences);
            }
        }
        System.currentTimeMillis();
    }

    @Override // com.mediatek.camera.ISettingCtrl
    public SettingItem getSetting(String str, int i) {
        return this.mSettingGenerator.getSettingItem(SettingConstants.getSettingId(str), i);
    }

    private void resetSettings(SharedPreferences sharedPreferences) {
        int i = 0;
        if (sharedPreferences == null) {
            return;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        if (this.mICameraContext.getModuleController().isNonePickIntent()) {
            int[] iArr = SettingConstants.RESET_SETTING_ITEMS;
            int length = iArr.length;
            while (i < length) {
                editorEdit.remove(SettingConstants.getSettingKey(iArr[i]));
                i++;
            }
        } else {
            int[] iArr2 = SettingConstants.THIRDPART_RESET_SETTING_ITEMS;
            int length2 = iArr2.length;
            while (i < length2) {
                editorEdit.remove(SettingConstants.getSettingKey(iArr2[i]));
                i++;
            }
        }
        editorEdit.apply();
    }

    private SettingItem getSetting(int i) {
        return this.mSettingGenerator.getSettingItem(i);
    }

    private SettingItem getSetting(int i, int i2) {
        return this.mSettingGenerator.getSettingItem(i, i2);
    }

    private void createRules() {
        createRuleFromResctrictionMatrix();
        createRuleFromRestrictions();
        createRuleFromScene();
        new RuleContainer(this, this.mICameraContext).addRule();
    }

    private void createRuleFromResctrictionMatrix() {
        int length;
        String settingResetValue;
        int[][] restrictionMatrix = SettingDataBase.getRestrictionMatrix();
        if (restrictionMatrix == null) {
            return;
        }
        int length2 = restrictionMatrix.length;
        int i = 0;
        while (true) {
            if (i < length2) {
                if (restrictionMatrix[i] == null) {
                    i++;
                } else {
                    length = restrictionMatrix[i].length;
                    break;
                }
            } else {
                length = 0;
                break;
            }
        }
        for (int i2 = 0; i2 < length; i2++) {
            int settingIndex = SettingDataBase.getSettingIndex(i2);
            String settingKey = SettingConstants.getSettingKey(settingIndex);
            for (int i3 = 0; i3 < length2; i3++) {
                if (restrictionMatrix[i3] != null && (settingResetValue = SettingDataBase.getSettingResetValue(i3, restrictionMatrix[i3][i2])) != null) {
                    CommonRule commonRule = new CommonRule(settingKey, SettingConstants.getSettingKey(i3), this.mICameraDeviceManager, this.mSettingGenerator);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(settingResetValue);
                    commonRule.addLimitation("on", arrayList, null);
                    this.mRuleMatrix[settingIndex][i3] = commonRule;
                }
            }
        }
    }

    private void createRuleFromRestrictions() {
        ISettingRule iSettingRule;
        for (Restriction restriction : SettingDataBase.getRestrictions()) {
            int index = restriction.getIndex();
            if ((index != 0 || !(!this.mICameraContext.getFeatureConfig().isVfbEnable())) && (index != 0 || !(!SettingGenerator.isSupport4K2K))) {
                String settingKey = SettingConstants.getSettingKey(index);
                List<String> values = restriction.getValues();
                List<Restriction> restrictioins = restriction.getRestrictioins();
                for (int i = 0; i < restrictioins.size(); i++) {
                    Restriction restriction2 = restrictioins.get(i);
                    int index2 = restriction2.getIndex();
                    String settingKey2 = SettingConstants.getSettingKey(index2);
                    List<String> values2 = restriction2.getValues();
                    ISettingRule.MappingFinder mappingFinder = restriction2.getMappingFinder();
                    if (this.mRuleMatrix[index][index2] == null) {
                        CommonRule commonRule = new CommonRule(settingKey, settingKey2, this.mICameraDeviceManager, this.mSettingGenerator);
                        this.mRuleMatrix[index][index2] = commonRule;
                        iSettingRule = commonRule;
                    } else {
                        iSettingRule = this.mRuleMatrix[index][index2];
                    }
                    for (int i2 = 0; i2 < values.size(); i2++) {
                        iSettingRule.addLimitation(values.get(i2), values2, mappingFinder);
                    }
                }
            }
        }
    }

    private void createRuleFromScene() {
        int length;
        String settingResetValue;
        ISettingRule commonRule;
        int[][] sceneRestrictionMatrix = SettingDataBase.getSceneRestrictionMatrix();
        if (sceneRestrictionMatrix == null) {
            return;
        }
        int length2 = sceneRestrictionMatrix.length;
        int i = 0;
        while (true) {
            if (i < length2) {
                if (sceneRestrictionMatrix[i] == null) {
                    i++;
                } else {
                    length = sceneRestrictionMatrix[i].length;
                    break;
                }
            } else {
                length = 0;
                break;
            }
        }
        String settingKey = SettingConstants.getSettingKey(12);
        for (int i2 = 0; i2 < length; i2++) {
            String sceneMode = SettingDataBase.getSceneMode(i2);
            for (int i3 = 0; i3 < length2; i3++) {
                if (sceneRestrictionMatrix[i3] != null && (settingResetValue = SettingDataBase.getSettingResetValue(i3, sceneRestrictionMatrix[i3][i2])) != null) {
                    String settingKey2 = SettingConstants.getSettingKey(i3);
                    if (this.mRuleMatrix[12][i3] != null) {
                        commonRule = this.mRuleMatrix[12][i3];
                    } else {
                        commonRule = new CommonRule(settingKey, settingKey2, this.mICameraDeviceManager, this.mSettingGenerator);
                        this.mRuleMatrix[12][i3] = commonRule;
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(settingResetValue);
                    commonRule.addLimitation(sceneMode, arrayList, null);
                }
            }
        }
    }

    private List<SettingItem> queryConditionSettings(String str) {
        ArrayList arrayList = new ArrayList();
        int settingId = SettingConstants.getSettingId(str);
        int length = this.mRuleMatrix.length;
        for (int i = 0; i < length; i++) {
            if (this.mRuleMatrix[i][settingId] != null) {
                arrayList.add(getSetting(i));
            }
        }
        return arrayList;
    }

    private List<SettingItem> queryResultSettings(String str) {
        ArrayList arrayList = new ArrayList();
        int settingId = SettingConstants.getSettingId(str);
        int length = this.mRuleMatrix[settingId].length;
        for (int i = 0; i < length; i++) {
            if (this.mRuleMatrix[settingId][i] != null) {
                arrayList.add(getSetting(i));
            }
        }
        return arrayList;
    }

    private boolean isNeedQueryByYAxis(String str) {
        if ("pref_camera_picturesize_ratio_key".equals(str) || "pref_camera_zsd_key".equals(str) || "pref_camera_picturesize_key".equals(str)) {
            return true;
        }
        return "pref_camera_antibanding_key".equals(str);
    }

    private void synchronizeSetting() {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        int numberOfCameras = this.mICameraDeviceManager.getNumberOfCameras();
        int settingId = SettingConstants.getSettingId("face_beauty_key");
        SettingItem setting = getSetting(settingId, currentCameraId);
        SettingItem setting2 = getSetting(settingId, (currentCameraId + 1) % numberOfCameras);
        setting.setValue(setting2.getValue());
        setting2.setValue("off");
        int settingId2 = SettingConstants.getSettingId("photo_pip_key");
        SettingItem setting3 = getSetting(settingId2, currentCameraId);
        SettingItem setting4 = getSetting(settingId2, (currentCameraId + 1) % numberOfCameras);
        setting3.setValue(setting4.getValue());
        setting4.setValue("off");
        int settingId3 = SettingConstants.getSettingId("video_pip_key");
        SettingItem setting5 = getSetting(settingId3, currentCameraId);
        SettingItem setting6 = getSetting(settingId3, (currentCameraId + 1) % numberOfCameras);
        setting5.setValue(setting6.getValue());
        setting6.setValue("off");
    }
}
