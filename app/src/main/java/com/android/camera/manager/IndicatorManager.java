package com.android.camera.manager;

import android.content.res.TypedArray;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.SettingUtils;
import com.android.camera.p001ui.RotateImageView;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.SettingConstants;
import com.mediatek.camera.setting.preference.IconListPreference;
import com.mediatek.camera.setting.preference.ListPreference;
import java.util.List;

/* loaded from: classes.dex */
public class IndicatorManager extends ViewManager implements CameraActivity.OnParametersReadyListener, CameraActivity.OnPreferenceReadyListener {
    private String[] mDefaults;
    private View mIndicatorGroup;
    private String[] mOverrides;
    private boolean mPreferenceReady;
    private ListPreference[] mPrefs;
    private RotateImageView[] mViews;
    private boolean[] mVisibles;
    private static final int[] VIEW_IDS = {R.id.onscreen_dng_indicator, R.id.onscreen_white_balance_indicator, R.id.onscreen_scene_indicator, R.id.onscreen_voice_indicator};
    private static final int INDICATOR_COUNT = VIEW_IDS.length;
    private static final String[] SETTING_KEYS = {"pref_dng_key", "pref_camera_whitebalance_key", "pref_camera_scenemode_key", "pref_voice_key"};
    private static final boolean[] FROM_PARAMETERS = {false, true, true, true, false, false, false};

    public IndicatorManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mViews = new RotateImageView[INDICATOR_COUNT];
        this.mPrefs = new ListPreference[INDICATOR_COUNT];
        this.mDefaults = new String[INDICATOR_COUNT];
        this.mOverrides = new String[INDICATOR_COUNT];
        cameraActivity.addOnParametersReadyListener(this);
        cameraActivity.addOnPreferenceReadyListener(this);
        setAnimationEnabled(true, false);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_indicators);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < INDICATOR_COUNT) {
                this.mViews[i2] = (RotateImageView) viewInflate.findViewById(VIEW_IDS[i2]);
                i = i2 + 1;
            } else {
                this.mIndicatorGroup = viewInflate.findViewById(R.id.on_screen_group);
                return viewInflate;
            }
        }
    }

    @Override // com.android.camera.CameraActivity.OnPreferenceReadyListener
    public void onPreferenceReady() {
        for (int i = 0; i < INDICATOR_COUNT; i++) {
            String str = SETTING_KEYS[i];
            this.mPrefs[i] = getContext().getListPreference(str);
            this.mDefaults[i] = getContext().getISettingCtrl().getDefaultValue(str);
        }
        this.mPreferenceReady = true;
    }

    @Override // com.android.camera.CameraActivity.OnParametersReadyListener
    public void onCameraParameterReady() {
        refreshModeIndicator(true);
        refresh();
    }

    @Override // com.android.camera.manager.ViewManager
    public void onRefresh() {
        String str;
        if (!this.mPreferenceReady || getContext().isSwitchingCamera()) {
            Log.m12w("IndicatorManager", "onRefresh() why refresh before preference ready? ", new Throwable());
            return;
        }
        refreshModeIndicator(false);
        int i = 0;
        for (int i2 = 0; i2 < INDICATOR_COUNT; i2++) {
            String str2 = SETTING_KEYS[i2];
            if (this.mOverrides[i2] != null) {
                str = this.mOverrides[i2];
            } else {
                String settingValue = getContext().getISettingCtrl().getSettingValue(str2);
                str = ("pref_camera_scenemode_key".equals(str2) && "hdr".equals(settingValue)) ? "auto" : settingValue;
            }
            if (this.mPrefs[i2] instanceof IconListPreference) {
                if (!this.mVisibles[i2] || str == null || (this.mDefaults[i2] != null && this.mDefaults[i2].equals(str))) {
                    this.mViews[i2].setVisibility(8);
                } else {
                    this.mViews[i2].setVisibility(0);
                    IconListPreference iconListPreference = (IconListPreference) this.mPrefs[i2];
                    if (iconListPreference.getOriginalIconIds() != null) {
                        this.mViews[i2].setImageResource(iconListPreference.getOriginalIconIds()[SettingUtils.index(iconListPreference.getOriginalEntryValues(), str)]);
                    }
                    i++;
                }
            } else {
                this.mViews[i2].setVisibility(8);
            }
        }
        if (i > 0) {
            this.mIndicatorGroup.setBackgroundResource(R.drawable.bg_indicator_background);
        } else {
            this.mIndicatorGroup.setBackgroundDrawable(null);
        }
    }

    public synchronized void refreshModeIndicator(boolean z) {
        if (this.mVisibles == null || z) {
            this.mVisibles = new boolean[INDICATOR_COUNT];
            for (int i = 0; i < INDICATOR_COUNT; i++) {
                boolean zContains = true;
                int settingId = SettingConstants.getSettingId(SETTING_KEYS[i]);
                if (getContext().isImageCaptureIntent()) {
                    zContains = SettingUtils.contains(SettingConstants.SETTING_GROUP_CAMERA_FOR_UI, settingId);
                } else if (getContext().isVideoMode()) {
                    zContains = SettingUtils.contains(SettingConstants.SETTING_GROUP_VIDEO_FOR_UI, settingId);
                }
                this.mVisibles[i] = zContains;
            }
        }
    }

    public void restoreSceneMode() {
        int length = this.mOverrides.length;
        for (int i = 0; i < length; i++) {
            this.mOverrides[i] = null;
        }
    }

    public void onDetectedSceneMode(int i) {
        TypedArray typedArrayObtainTypedArray = getContext().getResources().obtainTypedArray(R.array.scenemode_native_mapping_entryvalues);
        String string = typedArrayObtainTypedArray.getString(i);
        typedArrayObtainTypedArray.recycle();
        if (!string.equals(this.mOverrides[2])) {
            this.mOverrides[2] = string;
            List<String> supportedSceneModes = getContext().getParameters().getSupportedSceneModes();
            if (supportedSceneModes == null || !supportedSceneModes.contains(string)) {
                string = "auto";
            }
            getContext().getISettingCtrl().onSettingChanged("pref_camera_scenemode_key", string);
            getContext().notifyPreferenceChanged(null);
            refresh();
        }
    }
}
