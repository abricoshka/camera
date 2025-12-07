package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.android.camera.p002v2.p003ui.RotateImageView;
import com.android.camera.p002v2.uimanager.preference.IconListPreference;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.android.camera.p002v2.util.CameraUtil;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class IndicatorManager extends AbstractUiManager {
    private String mAsdDetectedScene;
    private View mIndicatorGroup;
    private PreferenceManager mPreferenceManager;
    private RotateImageView[] mViews;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(IndicatorManager.class.getSimpleName());
    private static final int[] VIEW_IDS = {R.id.onscreen_dng_indicator, R.id.onscreen_white_balance_indicator, R.id.onscreen_scene_indicator, R.id.onscreen_exposure_indicator, R.id.onscreen_selftimer_indicator, R.id.onscreen_voice_indicator};
    private static final int INDICATOR_COUNT = VIEW_IDS.length;
    private static final String[] SETTING_KEYS = {"pref_dng_key", "pref_camera_whitebalance_key", "pref_camera_scenemode_key", "pref_camera_exposure_key", "pref_camera_self_timer_key", "pref_voice_key"};

    public IndicatorManager(Activity activity, ViewGroup viewGroup, PreferenceManager preferenceManager) {
        super(activity, viewGroup);
        this.mViews = new RotateImageView[INDICATOR_COUNT];
        this.mPreferenceManager = preferenceManager;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_indicators_v2);
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

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        int i = 0;
        for (int i2 = 0; i2 < INDICATOR_COUNT; i2++) {
            String str = SETTING_KEYS[i2];
            String overrideValue = "pref_camera_scenemode_key".equals(str) ? this.mAsdDetectedScene : null;
            ListPreference listPreference = this.mPreferenceManager.getListPreference(str);
            String defaultValue = listPreference != null ? listPreference.getDefaultValue() : null;
            if (listPreference != null && overrideValue == null && (overrideValue = listPreference.getOverrideValue()) == null) {
                overrideValue = listPreference.getValue();
            }
            if ("pref_camera_scenemode_key".equals(str) && "hdr".equals(overrideValue)) {
                overrideValue = overrideValue.equals(this.mAsdDetectedScene) ? "hdr-detection" : null;
            }
            if (overrideValue == null || (defaultValue != null && defaultValue.equals(overrideValue))) {
                this.mViews[i2].setVisibility(8);
            } else {
                this.mViews[i2].setVisibility(0);
                IconListPreference iconListPreference = (IconListPreference) listPreference;
                if (iconListPreference != null && iconListPreference.getOriginalIconIds() != null) {
                    this.mViews[i2].setImageResource(iconListPreference.getOriginalIconIds()[CameraUtil.index(iconListPreference.getOriginalEntryValues(), overrideValue)]);
                }
                i++;
            }
            LogHelper.m23d(TAG, "[onRefresh], i:" + i2 + ", key:" + str + ", value:" + overrideValue);
        }
        if (i > 0) {
            this.mIndicatorGroup.setBackgroundResource(R.drawable.bg_indicator_background);
        } else {
            this.mIndicatorGroup.setBackgroundDrawable(null);
        }
    }

    public void updateAsdDetectedScene(String str) {
        LogHelper.m26i(TAG, "[updateAsdDetectedScene], scene:" + str);
        this.mAsdDetectedScene = str;
        refresh();
    }
}
