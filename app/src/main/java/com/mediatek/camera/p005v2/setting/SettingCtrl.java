package com.mediatek.camera.p005v2.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Size;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.rule.CommonRule;
import com.mediatek.camera.p005v2.setting.rule.ExtraRules;
import com.mediatek.camera.p005v2.util.SettingKeys;
import com.mediatek.camera.p005v2.util.Utils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class SettingCtrl {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(SettingCtrl.class.getSimpleName());
    private String[] mCameraIds;
    private Context mContext;
    private SharedPreferences mGlobalPreferences;
    private SettingServant mSettingServantForAll;
    private SettingServant mSettingServantForBack;
    private SettingServant mSettingServantForFront;
    private ISettingRule[][] mRuleMatrix = (ISettingRule[][]) Array.newInstance((Class<?>) ISettingRule.class, 49, 49);
    private Map<String, SharedPreferences> mPreferencesMap = new HashMap();
    private Map<String, SettingCharacteristics> mCharacteristicsMap = new HashMap();
    private List<ISettingFilterListener> mISettingFilterListeners = new ArrayList();
    private Map<ISettingFilterListener, Handler> mSettingFilterHandler = new HashMap();
    private SettingGenerator mSettingGenerator = null;
    private String mCurrentCameraId = "0";
    private boolean mConfigurationCompleted = false;
    private long mConfigurateThreadId = -1;

    public interface ISettingFilterListener {
        void onFilterResult(Map<String, String> map, Map<String, String> map2);
    }

    public SettingCtrl(Context context) {
        LogHelper.m26i(TAG, "[SettingCtrl], constructor...");
        this.mContext = context;
        CameraManager cameraManager = (CameraManager) context.getSystemService("camera");
        if (cameraManager == null) {
            LogHelper.m26i(TAG, "cameraManager is null");
            return;
        }
        String packageName = context.getPackageName();
        this.mGlobalPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        upgradeOldVersion(this.mGlobalPreferences);
        try {
            this.mCameraIds = cameraManager.getCameraIdList();
            for (String str : this.mCameraIds) {
                this.mPreferencesMap.put(str, context.getSharedPreferences(packageName + "_preferences_" + str, 0));
            }
            for (String str2 : this.mCameraIds) {
                this.mCharacteristicsMap.put(str2, new SettingCharacteristics(cameraManager.getCameraCharacteristics(str2), str2, context));
            }
        } catch (CameraAccessException e) {
            LogHelper.m24e(TAG, "camera access exception" + e.getMessage());
        }
        initializeSettings();
        this.mSettingServantForAll = new SettingServant(this);
        this.mSettingServantForBack = new SettingServant(this, "0");
        this.mSettingServantForFront = new SettingServant(this, "1");
    }

    private void initializeSettings() {
        LogHelper.m26i(TAG, "[initializeSettings], begin...");
        this.mSettingGenerator = new SettingGenerator();
        this.mSettingGenerator.initializeSettingItem(SettingKeys.KEYS_FOR_SETTING, this.mCameraIds, this.mCharacteristicsMap);
        createRules();
        LogHelper.m26i(TAG, "[initializeSettings], end");
    }

    public void registerSettingFilterListener(ISettingFilterListener iSettingFilterListener, Handler handler) {
        LogHelper.m26i(TAG, "[registerSettingFilterListener], listener:" + iSettingFilterListener + ", handler:" + handler);
        if (iSettingFilterListener != null && (!this.mISettingFilterListeners.contains(iSettingFilterListener))) {
            this.mISettingFilterListeners.add(iSettingFilterListener);
            this.mSettingFilterHandler.put(iSettingFilterListener, handler);
        }
    }

    public void configurateSetting(Map<String, String> map) {
        LogHelper.m23d(TAG, "[configurateSetting], configureSettings:" + map.toString());
        this.mConfigurateThreadId = Thread.currentThread().getId();
        this.mConfigurationCompleted = false;
        this.mSettingGenerator.configureSettingItems(this.mCurrentCameraId);
        SettingItem settingItem = this.mSettingGenerator.getSettingItem("capture_mode_key");
        settingItem.setLastValue(null);
        map.put("capture_mode_key", settingItem.getValue());
        writeInIfSharedPreferenceValueIsNull(map, this.mCurrentCameraId);
        doSettingChange(map, false);
        this.mConfigurationCompleted = true;
        synchronized (this) {
            try {
                LogHelper.m26i(TAG, "[configurateSetting], notify all thread");
                notifyAll();
            } catch (Exception e) {
                LogHelper.m24e(TAG, "[configurateSetting], exception");
            }
        }
        LogHelper.m23d(TAG, "[configurateSetting], done");
    }

    public void doSettingChange(String str, String str2) {
        doSettingChange(str, str2, true);
    }

    public void doSettingChange(String str, String str2, boolean z) {
        int i = 0;
        if (str == null) {
            LogHelper.m26i(TAG, "[doSettingChange] key is null, return.");
        }
        if ("pref_camera_id_key".equals(str)) {
            String string = this.mGlobalPreferences.getString("pref_camera_id_key", null);
            LogHelper.m26i(TAG, "[doSettingChange], do camera switch, value:" + str2 + ", cameaId:" + string);
            if (str2.equals(string)) {
                return;
            }
            SharedPreferences.Editor editorEdit = this.mGlobalPreferences.edit();
            editorEdit.putString("pref_camera_id_key", str2);
            editorEdit.apply();
            this.mCurrentCameraId = str2;
            this.mSettingGenerator.updateCameraId(str2);
            this.mConfigurateThreadId = Thread.currentThread().getId();
            this.mSettingGenerator.getSettingItem("pref_camera_id_key").setValue(str2);
            this.mConfigurationCompleted = false;
            HashMap map = new HashMap();
            map.put(str, str2);
            this.mSettingServantForBack.postResultToListeners(map);
            this.mSettingServantForFront.postResultToListeners(map);
            this.mSettingServantForAll.postResultToListeners(map);
            return;
        }
        SettingItem settingItem = this.mSettingGenerator.getSettingItem(str);
        if (settingItem == null) {
            LogHelper.m26i(TAG, "[doSettingChange], item:" + settingItem + ", key:" + str);
            return;
        }
        String lastValue = settingItem.getLastValue();
        boolean zIsEnable = settingItem.isEnable();
        LogHelper.m26i(TAG, "[doSettingChange], key:" + str + ", value:" + str2 + ", lastValue:" + lastValue + ", isEnabled:" + zIsEnable + ", saved:" + z);
        if (str2 == null || str2.equals(lastValue) || (!zIsEnable)) {
            LogHelper.m26i(TAG, "[doSettingChange], do not need to change, return.");
            return;
        }
        if (z) {
            setSharedPreferencesValue(str, str2, this.mCurrentCameraId);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(settingItem);
        arrayList2.add(str);
        gatherAffectedItems(arrayList, arrayList2);
        HashMap map2 = new HashMap();
        while (true) {
            int i2 = i;
            if (i2 >= arrayList.size()) {
                break;
            }
            SettingItem settingItem2 = arrayList.get(i2);
            map2.put(settingItem2.getKey(), settingItem2.getValue());
            i = i2 + 1;
        }
        if ("pref_hdr_key".equals(str)) {
            settingItem.setOverrideValue(null);
            settingItem.clearAllOverrideRecord();
        }
        doSettingChanged2(str, str2);
        postResultToModule(arrayList, map2);
        postResultToUI(arrayList);
    }

    public void doSettingChange(Map<String, String> map, boolean z) {
        LogHelper.m23d(TAG, "[doSettingChange], changedSettings:" + map.toString());
        if (map.size() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (String str : map.keySet()) {
            SettingItem settingItem = this.mSettingGenerator.getSettingItem(str);
            if (settingItem.isEnable()) {
                arrayList2.add(settingItem);
                arrayList3.add(str);
                arrayList.add(settingItem);
            }
        }
        gatherAffectedItems(arrayList2, arrayList3);
        HashMap map2 = new HashMap();
        for (int i = 0; i < arrayList2.size(); i++) {
            SettingItem settingItem2 = arrayList2.get(i);
            map2.put(settingItem2.getKey(), settingItem2.getValue());
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            SettingItem settingItem3 = (SettingItem) arrayList.get(i2);
            settingItem3.setValue(map.get(settingItem3.getKey()));
        }
        if (map.containsKey("pref_camera_picturesize_ratio_key")) {
            this.mSettingGenerator.getSettingItem("pref_camera_picturesize_ratio_key").setLastValue(null);
        }
        if (z) {
            setSharedPreferencesValue(map, this.mCurrentCameraId);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            SettingItem settingItem4 = (SettingItem) arrayList.get(i3);
            doSettingChanged2(settingItem4.getKey(), settingItem4.getValue());
        }
        postResultToModule(arrayList2, map2);
        postResultToUI(arrayList2);
    }

    public void doSettingChange(Map<String, String> map) {
        doSettingChange(map, true);
    }

    public ISettingServant getSettingServant(String str) {
        if (str == null) {
            return getAllSettingServant();
        }
        if ("0".equals(str)) {
            return getBackSettingServant();
        }
        if ("1".equals(str)) {
            return getFrontSettingServant();
        }
        return getAllSettingServant();
    }

    public void addRule(String str, String str2, ISettingRule iSettingRule) {
        LogHelper.m26i(TAG, "[addRule], conditionKey:" + str + ", resultKey:" + str2 + ", rule:" + iSettingRule);
        this.mRuleMatrix[SettingKeys.getSettingId(str)][SettingKeys.getSettingId(str2)] = iSettingRule;
    }

    public String getCurrentCameraId() {
        return this.mCurrentCameraId;
    }

    public List<String> getSupportedValues(String str) {
        return getSupportedValues(str, this.mCurrentCameraId);
    }

    public List<String> getSupportedValues(String str, String str2) {
        if ("pref_camera_id_key".equals(str)) {
            ArrayList arrayList = new ArrayList();
            for (String str3 : this.mCameraIds) {
                arrayList.add(str3);
            }
            return arrayList;
        }
        return this.mCharacteristicsMap.get(str2).getSupportedValues(str);
    }

    public String getSettingValue(String str) {
        return getSettingValue(str, this.mCurrentCameraId);
    }

    public String getSettingValue(String str, String str2) {
        synchronized (this) {
            long id = Thread.currentThread().getId();
            if (!this.mConfigurationCompleted && id != this.mConfigurateThreadId) {
                try {
                    LogHelper.m26i(TAG, "[getSettingValue], waiting..., thread:" + Thread.currentThread());
                    wait();
                } catch (Exception e) {
                    LogHelper.m24e(TAG, "[getSettingValue], exception");
                }
            }
        }
        return this.mSettingGenerator.getSettingItem(str, str2).getValue();
    }

    public SettingItem getSettingItem(String str) {
        return getSettingItem(str, this.mCurrentCameraId);
    }

    public SettingItem getSettingItem(String str, String str2) {
        synchronized (this) {
            long id = Thread.currentThread().getId();
            if (!this.mConfigurationCompleted && id != this.mConfigurateThreadId) {
                try {
                    LogHelper.m26i(TAG, "[getSettingItem], waiting..., thread:" + Thread.currentThread());
                    wait();
                } catch (Exception e) {
                    LogHelper.m24e(TAG, "[getSettingValue], exception");
                }
            }
        }
        return this.mSettingGenerator.getSettingItem(str, str2);
    }

    public String getSharePreferenceValue(String str) {
        return getSharePreferenceValue(str, this.mCurrentCameraId);
    }

    public String getSharePreferenceValue(String str, String str2) {
        if (isGlobalPref(str)) {
            return this.mGlobalPreferences.getString(str, null);
        }
        return this.mPreferencesMap.get(str2).getString(str, null);
    }

    public void setSharedPreferencesValue(String str, String str2) {
        setSharedPreferencesValue(str, str2, this.mCurrentCameraId);
    }

    public void setSharedPreferencesValue(String str, String str2, String str3) {
        SharedPreferences.Editor editorEdit;
        if (isGlobalPref(str)) {
            editorEdit = this.mGlobalPreferences.edit();
        } else {
            editorEdit = this.mPreferencesMap.get(str3).edit();
        }
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    public void setSharedPreferencesValue(Map<String, String> map, String str) {
        Set<String> setKeySet = map.keySet();
        SharedPreferences.Editor editorEdit = this.mPreferencesMap.get(this.mCurrentCameraId).edit();
        SharedPreferences.Editor editorEdit2 = this.mGlobalPreferences.edit();
        for (String str2 : (String[]) setKeySet.toArray(new String[setKeySet.size()])) {
            String str3 = map.get(str2);
            if (isGlobalPref(str2)) {
                editorEdit2.putString(str2, str3);
            } else {
                editorEdit.putString(str2, str3);
            }
        }
        editorEdit.apply();
        editorEdit2.apply();
    }

    public void clearSharedPreferencesValue(String[] strArr, String str) {
        SharedPreferences.Editor editorEdit = this.mPreferencesMap.get(str).edit();
        SharedPreferences.Editor editorEdit2 = this.mGlobalPreferences.edit();
        for (String str2 : strArr) {
            if (isGlobalPref(str2)) {
                editorEdit2.remove(str2);
            } else {
                editorEdit.remove(str2);
            }
        }
        editorEdit2.apply();
        editorEdit.apply();
    }

    public Size getPreviewSize(String str) throws NumberFormatException {
        double dFindFullscreenRatio;
        if (str == null) {
            str = this.mCurrentCameraId;
        }
        String value = this.mSettingGenerator.getSettingItem("pref_camera_picturesize_ratio_key", str).getValue();
        if (value == null) {
            value = this.mPreferencesMap.get(str).getString("pref_camera_picturesize_ratio_key", null);
        }
        if (value != null) {
            dFindFullscreenRatio = Double.parseDouble(value);
        } else {
            dFindFullscreenRatio = Utils.findFullscreenRatio(this.mContext);
        }
        return Utils.getOptimalPreviewSize(this.mContext, this.mCharacteristicsMap.get(str).getSupportedPreviewSize(), dFindFullscreenRatio);
    }

    private void writeInIfSharedPreferenceValueIsNull(Map<String, String> map, String str) {
        Set<String> setKeySet = map.keySet();
        SharedPreferences sharedPreferences = this.mPreferencesMap.get(str);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        SharedPreferences.Editor editorEdit2 = this.mGlobalPreferences.edit();
        for (String str2 : (String[]) setKeySet.toArray(new String[setKeySet.size()])) {
            String str3 = map.get(str2);
            if (isGlobalPref(str2) && this.mGlobalPreferences.getString(str2, null) == null) {
                editorEdit2.putString(str2, str3);
            } else if (sharedPreferences.getString(str2, null) == null) {
                editorEdit.putString(str2, str3);
            }
        }
        editorEdit.apply();
        editorEdit2.apply();
    }

    private ISettingServant getAllSettingServant() {
        return this.mSettingServantForAll;
    }

    private ISettingServant getBackSettingServant() {
        return this.mSettingServantForBack;
    }

    private ISettingServant getFrontSettingServant() {
        return this.mSettingServantForFront;
    }

    private void createRules() {
        createRuleFromResctrictionMatrix();
        createRuleFromRestrictions();
        createRuleFromScene();
        createExtraRule();
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
            String settingKey = SettingKeys.getSettingKey(settingIndex);
            SettingItem settingItem = this.mSettingGenerator.getSettingItem(settingKey);
            for (int i3 = 0; i3 < length2; i3++) {
                if (restrictionMatrix[i3] != null && (settingResetValue = SettingDataBase.getSettingResetValue(i3, restrictionMatrix[i3][i2])) != null) {
                    String settingKey2 = SettingKeys.getSettingKey(i3);
                    settingItem.addEffectdSetting(this.mSettingGenerator.getSettingItem(settingKey2));
                    CommonRule commonRule = new CommonRule(settingKey, settingKey2, this);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(settingResetValue);
                    commonRule.addLimitation("on", arrayList);
                    this.mRuleMatrix[settingIndex][i3] = commonRule;
                }
            }
        }
    }

    private void createRuleFromRestrictions() {
        ISettingRule iSettingRule;
        for (Restriction restriction : SettingDataBase.getRestrictions()) {
            int index = restriction.getIndex();
            String settingKey = SettingKeys.getSettingKey(index);
            SettingItem settingItem = this.mSettingGenerator.getSettingItem(settingKey);
            List<String> values = restriction.getValues();
            List<Restriction> restrictioins = restriction.getRestrictioins();
            for (int i = 0; i < restrictioins.size(); i++) {
                Restriction restriction2 = restrictioins.get(i);
                int index2 = restriction2.getIndex();
                String settingKey2 = SettingKeys.getSettingKey(index2);
                settingItem.addEffectdSetting(this.mSettingGenerator.getSettingItem(settingKey2));
                List<String> values2 = restriction2.getValues();
                if (this.mRuleMatrix[index][index2] == null) {
                    CommonRule commonRule = new CommonRule(settingKey, settingKey2, this);
                    this.mRuleMatrix[index][index2] = commonRule;
                    iSettingRule = commonRule;
                } else {
                    iSettingRule = this.mRuleMatrix[index][index2];
                }
                for (int i2 = 0; i2 < values.size(); i2++) {
                    iSettingRule.addLimitation(values.get(i2), values2);
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
        String settingKey = SettingKeys.getSettingKey(11);
        SettingItem settingItem = this.mSettingGenerator.getSettingItem(settingKey);
        for (int i2 = 0; i2 < length; i2++) {
            String sceneMode = SettingDataBase.getSceneMode(i2);
            for (int i3 = 0; i3 < length2; i3++) {
                if (sceneRestrictionMatrix[i3] != null && (settingResetValue = SettingDataBase.getSettingResetValue(i3, sceneRestrictionMatrix[i3][i2])) != null) {
                    String settingKey2 = SettingKeys.getSettingKey(i3);
                    settingItem.addEffectdSetting(this.mSettingGenerator.getSettingItem(settingKey2));
                    if (this.mRuleMatrix[11][i3] != null) {
                        commonRule = this.mRuleMatrix[11][i3];
                    } else {
                        commonRule = new CommonRule(settingKey, settingKey2, this);
                        this.mRuleMatrix[11][i3] = commonRule;
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(settingResetValue);
                    commonRule.addLimitation(sceneMode, arrayList);
                }
            }
        }
    }

    private void createExtraRule() {
        new ExtraRules(this).createRules();
    }

    private void doSettingChanged2(String str, String str2) {
        boolean z = false;
        SettingItem settingItem = getSettingItem(str);
        String lastValue = settingItem.getLastValue();
        boolean zIsEnable = settingItem.isEnable();
        LogHelper.m23d(TAG, "[doSettingChanged2], key:" + str + ", value:" + str2 + ", lastValue:" + lastValue + ", isEnabled:" + zIsEnable);
        if (str2 == null || str2.equals(lastValue) || (!zIsEnable)) {
            LogHelper.m26i(TAG, "[doSettingChanged2], key:" + str + ", do not need to change, return");
            return;
        }
        settingItem.setValue(str2);
        settingItem.setLastValue(str2);
        int settingId = settingItem.getSettingId();
        if (isNeedQueryByYAxis(str)) {
            LogHelper.m23d(TAG, "[doSettingChanged2], query rule by Y axis. key:" + str);
            List<SettingItem> listQueryConditionSettings = queryConditionSettings(str);
            int i = 0;
            while (true) {
                if (i >= listQueryConditionSettings.size()) {
                    break;
                }
                SettingItem settingItem2 = listQueryConditionSettings.get(i);
                ISettingRule iSettingRule = this.mRuleMatrix[settingItem2.getSettingId()][settingId];
                if (iSettingRule != null) {
                    String defaultValue = settingItem2.getDefaultValue();
                    if (settingItem2.getValue() != null && (!r0.equals(defaultValue))) {
                        iSettingRule.execute();
                        z = true;
                        break;
                    }
                }
                i++;
            }
        }
        LogHelper.m23d(TAG, "[doSettingChanged2], isExecutedByRule:" + z);
        executeRule(str);
    }

    private void executeRule(String str) {
        SettingItem settingItem = getSettingItem(str);
        List<SettingItem> listQueryResultSettings = queryResultSettings(str);
        int settingId = settingItem.getSettingId();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < listQueryResultSettings.size()) {
                SettingItem settingItem2 = listQueryResultSettings.get(i2);
                this.mRuleMatrix[settingId][settingItem2.getSettingId()].execute();
                doSettingChanged2(settingItem2.getKey(), settingItem2.getValue());
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private boolean isNeedQueryByYAxis(String str) {
        if ("pref_camera_picturesize_ratio_key".equals(str) || "pref_camera_zsd_key".equals(str) || "pref_camera_picturesize_key".equals(str)) {
            return true;
        }
        return "pref_camera_antibanding_key".equals(str);
    }

    private List<SettingItem> queryConditionSettings(String str) {
        ArrayList arrayList = new ArrayList();
        int settingId = SettingKeys.getSettingId(str);
        int length = this.mRuleMatrix.length;
        for (int i = 0; i < length; i++) {
            if (this.mRuleMatrix[i][settingId] != null) {
                arrayList.add(getSettingItem(i));
            }
        }
        return arrayList;
    }

    private List<SettingItem> queryResultSettings(String str) {
        ArrayList arrayList = new ArrayList();
        int settingId = SettingKeys.getSettingId(str);
        int length = this.mRuleMatrix[settingId].length;
        for (int i = 0; i < length; i++) {
            if (this.mRuleMatrix[settingId][i] != null) {
                arrayList.add(getSettingItem(i));
            }
        }
        return arrayList;
    }

    private void gatherAffectedItems(List<SettingItem> list, List<String> list2) {
        if (list2.size() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list2.size(); i++) {
            List<SettingItem> listQueryResultSettings = queryResultSettings(list2.get(i));
            for (int i2 = 0; i2 < listQueryResultSettings.size(); i2++) {
                SettingItem settingItem = listQueryResultSettings.get(i2);
                if (!list.contains(settingItem)) {
                    list.add(settingItem);
                    arrayList.add(settingItem.getKey());
                }
            }
        }
        gatherAffectedItems(list, arrayList);
    }

    private void postResultToModule(List<SettingItem> list, Map<String, String> map) {
        int i = 0;
        HashMap map2 = new HashMap();
        while (true) {
            int i2 = i;
            if (i2 < list.size()) {
                SettingItem settingItem = list.get(i2);
                String key = settingItem.getKey();
                String value = settingItem.getValue();
                if (value != null && !value.equals(map.get(key)) && settingItem.getType() == 0) {
                    map2.put(key, value);
                }
                i = i2 + 1;
            } else {
                LogHelper.m23d(TAG, "[postResultToModule], changedSettings:" + map2.toString());
                this.mSettingServantForBack.postResultToListeners(map2);
                this.mSettingServantForFront.postResultToListeners(map2);
                this.mSettingServantForAll.postResultToListeners(map2);
                return;
            }
        }
    }

    private void postResultToUI(List<SettingItem> list) {
        final HashMap map = new HashMap();
        final HashMap map2 = new HashMap();
        HashMap map3 = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            SettingItem settingItem = list.get(i);
            if (settingItem.isEnable()) {
                String key = settingItem.getKey();
                String value = settingItem.getValue();
                String overrideValue = settingItem.getOverrideValue();
                map.put(key, value);
                map2.put(key, overrideValue);
                map3.put(key, value + "/" + overrideValue);
            }
        }
        LogHelper.m23d(TAG, "[postResultToUI], override value:" + map3.toString());
        for (int i2 = 0; i2 < this.mISettingFilterListeners.size(); i2++) {
            final ISettingFilterListener iSettingFilterListener = this.mISettingFilterListeners.get(i2);
            this.mSettingFilterHandler.get(iSettingFilterListener).post(new Runnable() { // from class: com.mediatek.camera.v2.setting.SettingCtrl.1
                @Override // java.lang.Runnable
                public void run() {
                    LogHelper.m23d(SettingCtrl.TAG, "[postResultToUI], onFilterResult");
                    iSettingFilterListener.onFilterResult(map, map2);
                }
            });
        }
    }

    private SettingItem getSettingItem(int i) {
        return getSettingItem(SettingKeys.getSettingKey(i));
    }

    private void upgradeOldVersion(SharedPreferences sharedPreferences) {
        int i;
        String str;
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
                str = "normal";
            } else if (string.equals("75")) {
                str = "fine";
            } else {
                str = "superfine";
            }
            editorEdit.putString("pref_camera_jpegquality_key", str);
            i = 2;
        }
        if (i == 2) {
            editorEdit.putString("pref_camera_recordlocation_key", sharedPreferences.getBoolean("pref_camera_recordlocation_key", false) ? "on" : "none");
            i = 3;
        }
        if (i == 3) {
            editorEdit.remove("pref_camera_videoquality_key");
            editorEdit.remove("pref_camera_video_duration_key");
        }
        editorEdit.putInt("pref_version_key", 5);
        editorEdit.apply();
    }

    private boolean isGlobalPref(String str) {
        if ("pref_camera_id_key".equals(str) || "pref_camera_recordlocation_key".equals(str) || "photo_pip_key".equals(str)) {
            return true;
        }
        return "video_pip_key".equals(str);
    }

    private class SettingServant implements ISettingServant {
        private String mConcernedCamera;
        private Map<ISettingServant.ISettingChangedListener, List<String>> mListenerConcern;
        private Map<ISettingServant.ISettingChangedListener, Handler> mListenerHandler;
        private Map<ISettingServant.ISettingChangedListener, Integer> mListenerPriority;
        private LinkedList<ISettingServant.ISettingChangedListener> mListeners;
        private SettingCtrl mSettingCtrl;

        public SettingServant(SettingCtrl settingCtrl) {
            this.mConcernedCamera = null;
            this.mListeners = new LinkedList<>();
            this.mListenerConcern = new HashMap();
            this.mListenerPriority = new HashMap();
            this.mListenerHandler = new HashMap();
            this.mSettingCtrl = settingCtrl;
        }

        public SettingServant(SettingCtrl settingCtrl, String str) {
            this.mConcernedCamera = null;
            this.mListeners = new LinkedList<>();
            this.mListenerConcern = new HashMap();
            this.mListenerPriority = new HashMap();
            this.mListenerHandler = new HashMap();
            this.mSettingCtrl = settingCtrl;
            this.mConcernedCamera = str;
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public void registerSettingChangedListener(ISettingServant.ISettingChangedListener iSettingChangedListener, List<String> list, int i) {
            Handler handler = null;
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper != null) {
                handler = new Handler(looperMyLooper);
            } else {
                Looper mainLooper = Looper.getMainLooper();
                if (mainLooper != null) {
                    handler = new Handler(mainLooper);
                } else {
                    LogHelper.m24e(SettingCtrl.TAG, "[registerSettingChangedListener], the caller's looper is null. listener:" + iSettingChangedListener);
                }
            }
            registerSettingChangedListener(iSettingChangedListener, list, handler, i);
        }

        public void registerSettingChangedListener(ISettingServant.ISettingChangedListener iSettingChangedListener, List<String> list, Handler handler, int i) {
            LogHelper.m26i(SettingCtrl.TAG, "[registerSettingChangedListener], listener:" + iSettingChangedListener + ", priority:" + i);
            this.mListenerConcern.put(iSettingChangedListener, list);
            this.mListenerPriority.put(iSettingChangedListener, Integer.valueOf(i));
            this.mListenerHandler.put(iSettingChangedListener, handler);
            if (this.mListeners.contains(iSettingChangedListener)) {
                return;
            }
            if (this.mListeners.isEmpty()) {
                this.mListeners.add(iSettingChangedListener);
                return;
            }
            ISettingServant.ISettingChangedListener first = this.mListeners.getFirst();
            int size = this.mListeners.size();
            int i2 = 0;
            ISettingServant.ISettingChangedListener iSettingChangedListener2 = first;
            while (i2 < size && this.mListenerPriority.get(iSettingChangedListener2).intValue() >= i) {
                i2++;
                iSettingChangedListener2 = i2 < size ? this.mListeners.get(i2) : iSettingChangedListener2;
            }
            this.mListeners.add(i2, iSettingChangedListener);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public void unRegisterSettingChangedListener(ISettingServant.ISettingChangedListener iSettingChangedListener) {
            if (this.mListeners.contains(iSettingChangedListener)) {
                this.mListeners.remove(iSettingChangedListener);
                this.mListenerConcern.remove(iSettingChangedListener);
                this.mListenerPriority.remove(iSettingChangedListener);
                this.mListenerHandler.remove(iSettingChangedListener);
            }
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public String getCameraId() {
            if (this.mConcernedCamera == null) {
                return SettingCtrl.this.mCurrentCameraId;
            }
            return this.mConcernedCamera;
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public void doSettingChange(String str, String str2, boolean z) {
            this.mSettingCtrl.doSettingChange(str, str2, z);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public String getSettingValue(String str) {
            if (this.mConcernedCamera == null) {
                return this.mSettingCtrl.getSettingValue(str);
            }
            return this.mSettingCtrl.getSettingValue(str, this.mConcernedCamera);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public SettingItem getSettingItem(String str) {
            if (this.mConcernedCamera == null) {
                return this.mSettingCtrl.getSettingItem(str);
            }
            return this.mSettingCtrl.getSettingItem(str, this.mConcernedCamera);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public Size getPreviewSize() {
            return this.mSettingCtrl.getPreviewSize(this.mConcernedCamera);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public List<Size> getSupportedPreviewSizes() {
            return ((SettingCharacteristics) SettingCtrl.this.mCharacteristicsMap.get(this.mConcernedCamera)).getSupportedPreviewSize();
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public String getSharedPreferencesValue(String str) {
            if (this.mConcernedCamera == null) {
                return this.mSettingCtrl.getSharePreferenceValue(str);
            }
            return this.mSettingCtrl.getSharePreferenceValue(str, this.mConcernedCamera);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public void setSharedPreferencesValue(String str, String str2) {
            if (this.mConcernedCamera == null) {
                this.mSettingCtrl.setSharedPreferencesValue(str, str2);
            } else {
                this.mSettingCtrl.setSharedPreferencesValue(str, str2, this.mConcernedCamera);
            }
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingServant
        public List<String> getSupportedValues(String str) {
            return this.mSettingCtrl.getSupportedValues(str, getCameraId());
        }

        public void postResultToListeners(final Map<String, String> map) {
            if (this.mConcernedCamera != null && (!this.mConcernedCamera.equals(SettingCtrl.this.mCurrentCameraId)) && (!isIncludeCameraId(map))) {
                LogHelper.m26i(SettingCtrl.TAG, "do not need post result to listeners, mConcernedCamera:" + this.mConcernedCamera + ", mCurrentCameraId:" + SettingCtrl.this.mCurrentCameraId);
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.mListeners.size(); i++) {
                arrayList.add(this.mListeners.get(i));
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                final ISettingServant.ISettingChangedListener iSettingChangedListener = (ISettingServant.ISettingChangedListener) arrayList.get(i2);
                Handler handler = this.mListenerHandler.get(iSettingChangedListener);
                if (handler != null) {
                    handler.post(new Runnable() { // from class: com.mediatek.camera.v2.setting.SettingCtrl.SettingServant.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SettingServant.this.postResultToListener(iSettingChangedListener, map);
                        }
                    });
                } else {
                    postResultToListener(iSettingChangedListener, map);
                }
            }
        }

        private boolean isIncludeCameraId(Map<String, String> map) {
            if (map == null) {
                return false;
            }
            return map.containsKey("pref_camera_id_key");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void postResultToListener(ISettingServant.ISettingChangedListener iSettingChangedListener, Map<String, String> map) {
            HashMap map2 = new HashMap();
            List<String> list = this.mListenerConcern.get(iSettingChangedListener);
            if (list == null && (!map.isEmpty())) {
                LogHelper.m26i(SettingCtrl.TAG, "[postResultToListener], all result:" + map.toString() + ", listener:" + iSettingChangedListener);
                iSettingChangedListener.onSettingChanged(map);
                return;
            }
            for (String str : map.keySet()) {
                if (list != null && list.contains(str)) {
                    map2.put(str, map.get(str));
                }
            }
            if (map2.isEmpty()) {
                return;
            }
            LogHelper.m26i(SettingCtrl.TAG, "[postResultToListener], part result:" + map2.toString() + ", listener:" + iSettingChangedListener);
            iSettingChangedListener.onSettingChanged(map2);
        }
    }
}
