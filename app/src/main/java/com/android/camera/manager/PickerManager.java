package com.android.camera.manager;

import android.view.View;
import android.widget.FrameLayout;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.p001ui.CameraSpecialEffectsLayout;
import com.android.camera.p001ui.EntrySpecialEffectsLayout;
import com.android.camera.p001ui.PickerButton;
import com.android.camera.p001ui.RotateImageView;
import com.android.camera.p001ui.ZZZFrameLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.setting.preference.IconListPreference;
import com.mediatek.camera.setting.preference.ListPreference;

/* loaded from: classes.dex */
public class PickerManager extends ViewManager implements PickerButton.Listener, CameraActivity.OnPreferenceReadyListener, CameraActivity.OnParametersReadyListener {
    private static boolean[] sShownStatusRecorder = new boolean[5];
    private int[] mButtonPriority;
    private PickerButton mCameraPicker;
    private RotateImageView mCameraSetting;
    private CameraActivity mContext;
    private boolean mDefineOrder;
    private PickerListener mListener;
    private PickerButton[] mPickerButtons;
    private View mPickersView;
    private boolean mPreferenceReady;

    public interface PickerListener {
        boolean onCameraPicked(int i);

        boolean onEffectPicked(String str);

        boolean onFlashPicked(String str);

        boolean onHdrPicked(String str);

        boolean onLiveFocusPicked(String str);

        boolean onSelfTimerPicked(String str);
    }

    static {
        sShownStatusRecorder[2] = false;
        sShownStatusRecorder[0] = false;
        sShownStatusRecorder[1] = false;
        sShownStatusRecorder[3] = false;
        sShownStatusRecorder[4] = false;
    }

    public PickerManager(CameraActivity cameraActivity) {
        super(cameraActivity);
        this.mPickerButtons = new PickerButton[5];
        this.mButtonPriority = new int[]{2, 0, 1, 3, 4};
        this.mDefineOrder = false;
        this.mContext = cameraActivity;
        cameraActivity.addOnPreferenceReadyListener(this);
        cameraActivity.addOnParametersReadyListener(this);
    }

    @Override // com.android.camera.manager.ViewManager
    protected View getView() {
        this.mPickersView = inflate(R.layout.tw_onscreen_pickers);
        applyListeners();
        return this.mPickersView;
    }

    private void applyListeners() {
        for (PickerButton pickerButton : this.mPickerButtons) {
            if (pickerButton != null) {
                pickerButton.setListener(this);
            }
        }
    }

    public void setListener(PickerListener pickerListener) {
        this.mListener = pickerListener;
    }

    @Override // com.android.camera.CameraActivity.OnPreferenceReadyListener
    public void onPreferenceReady() {
        Log.m8i("PickerManager", "onPreferenceReady()");
        this.mPreferenceReady = true;
    }

    @Override // com.android.camera.CameraActivity.OnParametersReadyListener
    public void onCameraParameterReady() {
        IconListPreference iconListPreference;
        IconListPreference iconListPreference2;
        Log.m8i("PickerManager", "onCameraParameterReady(), mDefineOrder:" + this.mDefineOrder + ", mPreferenceReady:" + this.mPreferenceReady);
        if (!this.mPreferenceReady) {
            return;
        }
        if (!this.mDefineOrder) {
            int i = 0;
            for (int i2 = 0; i2 < this.mButtonPriority.length; i2++) {
                int i3 = this.mButtonPriority[i2];
                switch (i3) {
                    case 0:
                        iconListPreference2 = (IconListPreference) getContext().getListPreference(8);
                        break;
                    case 1:
                        iconListPreference2 = (IconListPreference) getContext().getListPreference(40);
                        break;
                    case 2:
                        iconListPreference2 = (IconListPreference) getContext().getListPreference(14);
                        break;
                    case 3:
                        iconListPreference2 = (IconListPreference) getContext().getListPreference(63);
                        break;
                    case 4:
                        iconListPreference2 = (IconListPreference) getContext().getListPreference(15);
                        break;
                    default:
                        iconListPreference2 = null;
                        break;
                }
                if (iconListPreference2 != null && iconListPreference2.getEntries() != null && iconListPreference2.getEntries().length > 1) {
                    iconListPreference2.showInSetting(false);
                    i++;
                }
                Log.m8i("PickerManager", "count:" + i + ", buttonIndex:" + i3);
                if (i >= 5) {
                    this.mDefineOrder = true;
                }
            }
            this.mDefineOrder = true;
        } else {
            for (int i4 = 0; i4 < this.mButtonPriority.length; i4++) {
                int i5 = this.mButtonPriority[i4];
                switch (i5) {
                    case 0:
                        iconListPreference = (IconListPreference) getContext().getListPreference(8);
                        break;
                    case 1:
                        iconListPreference = (IconListPreference) getContext().getListPreference(40);
                        break;
                    case 2:
                        iconListPreference = (IconListPreference) getContext().getListPreference(14);
                        break;
                    case 3:
                        iconListPreference = (IconListPreference) getContext().getListPreference(63);
                        break;
                    case 4:
                        iconListPreference = (IconListPreference) getContext().getListPreference(15);
                        break;
                    default:
                        iconListPreference = null;
                        break;
                }
                if (iconListPreference != null) {
                    iconListPreference.showInSetting(sShownStatusRecorder[i5]);
                }
            }
        }
        refresh();
    }

    @Override // com.android.camera.manager.ViewManager
    public void hide() {
    }

    @Override // com.android.camera.ui.PickerButton.Listener
    public boolean onPicked(PickerButton pickerButton, ListPreference listPreference, String str) {
        boolean zOnLiveFocusPicked = false;
        String key = listPreference.getKey();
        if (this.mListener != null) {
            int i = 0;
            while (true) {
                if (i >= 5) {
                    i = -1;
                    break;
                }
                if (pickerButton.equals(this.mPickerButtons[i])) {
                    break;
                }
                i++;
            }
            switch (i) {
                case 0:
                    pickerButton.setValue(str);
                    zOnLiveFocusPicked = this.mListener.onHdrPicked(str);
                    break;
                case 1:
                    zOnLiveFocusPicked = this.mListener.onFlashPicked(str);
                    break;
                case 2:
                    zOnLiveFocusPicked = this.mListener.onEffectPicked(str);
                    break;
                case 3:
                    zOnLiveFocusPicked = this.mListener.onLiveFocusPicked(str);
                    break;
                case 4:
                    zOnLiveFocusPicked = this.mListener.onSelfTimerPicked(str);
                    break;
            }
        }
        Log.m8i("PickerManager", "onPicked(" + key + ", " + str + ") mListener=" + this.mListener + " return " + zOnLiveFocusPicked);
        return zOnLiveFocusPicked;
    }

    public void setCameraId(int i) {
        PickerButton pickerButton = this.mCameraPicker;
    }

    @Override // com.android.camera.manager.ViewManager
    public void onRefresh() {
        Log.m5d("PickerManager", "onRefresh(), mPreferenceReady:" + this.mPreferenceReady);
        if (!this.mPreferenceReady) {
            return;
        }
        FrameLayout frameLayout = (FrameLayout) this.mPickersView.getParent().getParent().getParent().getParent();
        EntrySpecialEffectsLayout entrySpecialEffectsLayout = (EntrySpecialEffectsLayout) frameLayout.findViewById(R.id.enteryeffects);
        CameraSpecialEffectsLayout cameraSpecialEffectsLayout = (CameraSpecialEffectsLayout) frameLayout.findViewById(R.id.specialeffectslayout);
        ZZZFrameLayout zZZFrameLayout = (ZZZFrameLayout) frameLayout.findViewById(R.id.fl_cameramode_swith);
        FrameLayout frameLayout2 = (FrameLayout) frameLayout.findViewById(R.id.camera_app_root);
        if (entrySpecialEffectsLayout != null) {
            entrySpecialEffectsLayout.setSettingCtrl(this.mContext.getISettingCtrl());
            entrySpecialEffectsLayout.setCameraSpecialEffectsLayout(cameraSpecialEffectsLayout, frameLayout2, zZZFrameLayout);
        }
        if (cameraSpecialEffectsLayout != null) {
            cameraSpecialEffectsLayout.setSettingCtrl(this.mContext.getISettingCtrl());
            cameraSpecialEffectsLayout.setEntrySpecialEffectsLayout(entrySpecialEffectsLayout, frameLayout2, zZZFrameLayout);
        }
    }

    @Override // com.android.camera.manager.ViewManager
    protected void onRelease() {
        super.onRelease();
    }

    @Override // com.android.camera.manager.ViewManager
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        for (PickerButton pickerButton : this.mPickerButtons) {
            if (pickerButton != null) {
                pickerButton.setEnabled(z);
                pickerButton.setClickable(z);
            }
        }
        if (this.mCameraSetting != null) {
            this.mCameraSetting.setEnabled(z);
            this.mCameraSetting.setClickable(z);
        }
    }

    public void forceEnable(String str) {
    }

    public void cancelForcedEnable(String str) {
    }
}
