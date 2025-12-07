package com.android.camera.p002v2.uimanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.android.camera.p002v2.p003ui.PickerButton;
import com.android.camera.p002v2.uimanager.preference.IconListPreference;
import com.android.camera.p002v2.uimanager.preference.ListPreference;
import com.android.camera.p002v2.uimanager.preference.PreferenceManager;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class PickerManager extends AbstractUiManager implements PickerButton.Listener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PickerManager.class.getSimpleName());
    private static final Map<Integer, String> mButtonKeys = new HashMap(5);
    private static boolean[] sShowPosRecoder;
    private int[] mButtonPriority;
    private PickerButton mCameraPicker;
    private PickerButton mFlashPicker;
    private PickerButton mHdr;
    private OnPickedListener mOnPickedListener;
    private boolean mOrderDefined;
    private PickerButton[] mPickerButtons;
    private PreferenceManager mPreferenceManager;
    private PickerButton mSlowMotion;
    private PickerButton mStereoPicker;

    public interface OnPickedListener {
        void onPicked(String str, String str2);
    }

    static {
        mButtonKeys.put(0, "pref_hdr_key");
        mButtonKeys.put(1, "pref_camera_flashmode_key");
        mButtonKeys.put(2, "pref_camera_id_key");
        mButtonKeys.put(3, "pref_stereo3d_mode_key");
        mButtonKeys.put(4, "pref_slow_motion_key");
        sShowPosRecoder = new boolean[5];
        sShowPosRecoder[4] = false;
        sShowPosRecoder[0] = false;
        sShowPosRecoder[1] = false;
        sShowPosRecoder[2] = false;
        sShowPosRecoder[3] = false;
    }

    public PickerManager(Activity activity, ViewGroup viewGroup, PreferenceManager preferenceManager) {
        super(activity, viewGroup);
        this.mPickerButtons = new PickerButton[5];
        this.mOrderDefined = false;
        this.mButtonPriority = new int[]{4, 0, 1, 2, 3};
        this.mPreferenceManager = preferenceManager;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected View getView() {
        View viewInflate = inflate(R.layout.onscreen_pickers_v2);
        this.mSlowMotion = (PickerButton) viewInflate.findViewById(R.id.onscreen_slow_motion_picker);
        this.mHdr = (PickerButton) viewInflate.findViewById(R.id.onscreen_hdr_picker);
        this.mFlashPicker = (PickerButton) viewInflate.findViewById(R.id.onscreen_flash_picker);
        this.mCameraPicker = (PickerButton) viewInflate.findViewById(R.id.onscreen_camera_picker);
        this.mStereoPicker = (PickerButton) viewInflate.findViewById(R.id.onscreen_stereo3d_picker);
        this.mPickerButtons[4] = this.mSlowMotion;
        this.mPickerButtons[0] = this.mHdr;
        this.mPickerButtons[1] = this.mFlashPicker;
        this.mPickerButtons[2] = this.mCameraPicker;
        this.mPickerButtons[3] = this.mStereoPicker;
        applyListeners();
        return viewInflate;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    protected void onRefresh() {
        LogHelper.m23d(TAG, "[onRefresh], mOrderDefined:" + this.mOrderDefined);
        this.mSlowMotion.initialize((IconListPreference) this.mPreferenceManager.getListPreference("pref_slow_motion_key"));
        this.mHdr.initialize((IconListPreference) this.mPreferenceManager.getListPreference("pref_hdr_key"));
        this.mFlashPicker.initialize((IconListPreference) this.mPreferenceManager.getListPreference("pref_camera_flashmode_key"));
        this.mCameraPicker.initialize((IconListPreference) this.mPreferenceManager.getListPreference("pref_camera_id_key"));
        this.mStereoPicker.initialize((IconListPreference) this.mPreferenceManager.getListPreference("pref_stereo3d_mode_key"));
        for (PickerButton pickerButton : this.mPickerButtons) {
            if (pickerButton != null) {
                pickerButton.refresh();
            }
        }
    }

    @Override // com.android.camera.v2.ui.PickerButton.Listener
    public boolean onPicked(PickerButton pickerButton, ListPreference listPreference, String str) {
        if (this.mOnPickedListener != null) {
            this.mOnPickedListener.onPicked(listPreference.getKey(), str);
            return true;
        }
        return true;
    }

    @Override // com.android.camera.p002v2.uimanager.AbstractUiManager
    public void setEnable(boolean z) {
        super.setEnable(z);
        for (PickerButton pickerButton : this.mPickerButtons) {
            if (pickerButton != null) {
                pickerButton.setEnabled(z);
                pickerButton.setClickable(z);
            }
        }
    }

    public void notifyPreferenceReady() {
        LogHelper.m26i(TAG, "[notifyPreferenceReady]...");
        defineButtonOrder();
    }

    public void setOnPickedListener(OnPickedListener onPickedListener) {
        this.mOnPickedListener = onPickedListener;
    }

    public void performCameraPickerBtnClick() {
        int iFindIndexOfValue;
        ListPreference listPreference = this.mPreferenceManager.getListPreference("pref_camera_id_key");
        if (listPreference == null || (iFindIndexOfValue = listPreference.findIndexOfValue(listPreference.getValue())) == -1) {
            return;
        }
        CharSequence[] entryValues = listPreference.getEntryValues();
        int length = (iFindIndexOfValue + 1) % entryValues.length;
        String string = entryValues[length].toString();
        if (this.mOnPickedListener != null) {
            this.mOnPickedListener.onPicked("pref_camera_id_key", string);
        }
        listPreference.setValueIndex(length);
    }

    public void forceEnablePickerButton(String str) {
        int iIntValue;
        LogHelper.m23d(TAG, "[forceEnablePickerButton], key:" + str);
        ListPreference listPreference = this.mPreferenceManager.getListPreference(str);
        if (listPreference.isEnabled()) {
            return;
        }
        listPreference.setEnabled(true);
        Iterator<Integer> it = mButtonKeys.keySet().iterator();
        int i = -1;
        while (true) {
            if (!it.hasNext()) {
                iIntValue = i;
                break;
            }
            iIntValue = it.next().intValue();
            if (mButtonKeys.get(Integer.valueOf(iIntValue)).equals(str)) {
                break;
            } else {
                i = iIntValue;
            }
        }
        if (iIntValue > 0) {
            this.mPickerButtons[iIntValue].refresh();
        }
    }

    public void forceDisablePickerButton(String str) {
        int iIntValue;
        LogHelper.m23d(TAG, "[forceDisablePickerButton], key:" + str);
        ListPreference listPreference = this.mPreferenceManager.getListPreference(str);
        if (!listPreference.isEnabled()) {
            return;
        }
        listPreference.setEnabled(false);
        Iterator<Integer> it = mButtonKeys.keySet().iterator();
        int i = -1;
        while (true) {
            if (!it.hasNext()) {
                iIntValue = i;
                break;
            }
            iIntValue = it.next().intValue();
            if (mButtonKeys.get(Integer.valueOf(iIntValue)).equals(str)) {
                break;
            } else {
                i = iIntValue;
            }
        }
        if (iIntValue > 0) {
            this.mPickerButtons[iIntValue].refresh();
        }
    }

    private void applyListeners() {
        for (PickerButton pickerButton : this.mPickerButtons) {
            if (pickerButton != null) {
                pickerButton.setListener(this);
            }
        }
    }

    private void defineButtonOrder() {
        if (this.mOrderDefined) {
            for (int i = 0; i < this.mButtonPriority.length; i++) {
                int i2 = this.mButtonPriority[i];
                ListPreference listPreference = this.mPreferenceManager.getListPreference(mButtonKeys.get(Integer.valueOf(i2)));
                if (listPreference != null && listPreference.isVisibled()) {
                    listPreference.showInSetting(sShowPosRecoder[i2]);
                }
            }
            return;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.mButtonPriority.length; i4++) {
            int i5 = this.mButtonPriority[i4];
            ListPreference listPreference2 = this.mPreferenceManager.getListPreference(mButtonKeys.get(Integer.valueOf(i5)));
            if (listPreference2 != null && listPreference2.isVisibled()) {
                listPreference2.showInSetting(false);
                i3++;
                sShowPosRecoder[i5] = false;
            }
            if (i3 >= 4) {
                break;
            }
        }
        this.mOrderDefined = true;
    }
}
