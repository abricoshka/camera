package com.mediatek.camera.mode.facebeauty;

import android.app.Activity;
import android.content.SharedPreferences;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.R;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class FaceBeautyParametersHelper {
    private Activity mActivity;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private IModuleCtrl mIModuleCtrl;
    private ISettingCtrl mISettingCtrl;
    private ParameterListener mParametersListener = new ParameterListener() { // from class: com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.1
        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public boolean canShowFbIcon(int i) {
            return FaceBeautyParametersHelper.this.canShowFbIcon(i);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public void setVFBSharedPrefences(int i, String str) {
            FaceBeautyParametersHelper.this.setVFBSharedPrefences(i, str);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public int getvFbSharedPreferences(int i) {
            return FaceBeautyParametersHelper.this.getvFbSharedPreferences(i);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public void setParameters(int i, String str) {
            FaceBeautyParametersHelper.this.setParameters(i, str);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public int getMaxLevel(int i) {
            return FaceBeautyParametersHelper.this.getMaxLevel(i);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public int getMinLevel(int i) {
            return FaceBeautyParametersHelper.this.getMinLevel(i);
        }

        @Override // com.mediatek.camera.mode.facebeauty.FaceBeautyParametersHelper.ParameterListener
        public boolean isMultiFbMode() {
            return FaceBeautyParametersHelper.this.isMultiFbMode();
        }
    };

    public interface ParameterListener {
        boolean canShowFbIcon(int i);

        int getMaxLevel(int i);

        int getMinLevel(int i);

        int getvFbSharedPreferences(int i);

        boolean isMultiFbMode();

        void setParameters(int i, String str);

        void setVFBSharedPrefences(int i, String str);
    }

    public FaceBeautyParametersHelper(ICameraContext iCameraContext) {
        this.mICameraContext = iCameraContext;
        this.mISettingCtrl = this.mICameraContext.getSettingController();
        this.mActivity = this.mICameraContext.getActivity();
        this.mIModuleCtrl = this.mICameraContext.getModuleController();
    }

    public void updateParameters(ICameraDeviceManager.ICameraDevice iCameraDevice) {
        this.mICameraDevice = iCameraDevice;
    }

    public ParameterListener getListener() {
        return this.mParametersListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMaxLevel(int i) {
        switch (i) {
            case 0:
                return getInt("fb-smooth-level-max", Integer.parseInt(getDefaultValue("fb-smooth-level-default")));
            case 1:
                return getInt("fb-skin-color-max", Integer.parseInt(getDefaultValue("fb-skin-color-default")));
            case 2:
                return getInt("fb-sharp-max", Integer.parseInt(getDefaultValue("fb-sharp-default")));
            case 3:
                return getInt("fb-slim-face-max", Integer.parseInt(getDefaultValue("fb-enlarge-eye-default")));
            case 4:
                return getInt("fb-slim-face-max", Integer.parseInt(getDefaultValue("fb-slim-face-default")));
            default:
                return Integer.parseInt("0");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinLevel(int i) {
        switch (i) {
            case 0:
                return getInt("fb-smooth-level-min", Integer.parseInt(getDefaultValue("fb-smooth-level-default")));
            case 1:
                return getInt("fb-skin-color-min", Integer.parseInt(getDefaultValue("fb-skin-color-default")));
            case 2:
                return getInt("fb-sharp-min", Integer.parseInt(getDefaultValue("fb-sharp-default")));
            case 3:
                return getInt("fb-slim-face-min", Integer.parseInt(getDefaultValue("fb-enlarge-eye-default")));
            case 4:
                return getInt("fb-slim-face-min", Integer.parseInt(getDefaultValue("fb-slim-face-default")));
            default:
                return Integer.parseInt("0");
        }
    }

    private int getInt(String str, int i) {
        if (this.mICameraDevice != null) {
            try {
                return Integer.parseInt(this.mICameraDevice.getParameter(str));
            } catch (NumberFormatException e) {
                return i;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setParameters(int i, String str) {
        if (this.mICameraDevice == null) {
            return;
        }
        switch (i) {
            case 0:
                this.mICameraDevice.setParameter("fb-smooth-level", str);
                break;
            case 1:
                this.mICameraDevice.setParameter("fb-skin-color", str);
                break;
            case 3:
                this.mICameraDevice.setParameter("fb-enlarge-eye", str);
                break;
            case 4:
                this.mICameraDevice.setParameter("fb-slim-face", str);
                break;
        }
        this.mICameraDevice.applyParameters();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getvFbSharedPreferences(int i) {
        String str;
        String str2 = null;
        switch (i) {
            case 0:
                str = "pref_facebeauty_smooth_key";
                str2 = "fb-smooth-level-default";
                break;
            case 1:
                str = "pref_facebeauty_skin_color_key";
                str2 = "fb-skin-color-default";
                break;
            case 2:
            default:
                Log.m34i("FaceBeautyParametersHelper", "getvFbSharedPreferences,the key is null please check the string");
                str = null;
                break;
            case 3:
                str = "pref_facebeauty_big_eyes_key";
                str2 = "fb-enlarge-eye-default";
                break;
            case 4:
                str = "pref_facebeauty_slim_key";
                str2 = "fb-slim-face-default";
                break;
        }
        return getVFBSharedPreference(str, getDefaultValue(str2));
    }

    private int getVFBSharedPreference(String str, String str2) {
        String string = this.mIModuleCtrl.getComboPreferences().getString(str, str2);
        Log.m31d("FaceBeautyParametersHelper", "[getVFBSharedPreference]get the effects value from sharedpreferences ,key = " + str + ",defalut value is :" + str2 + ",return value is :" + string);
        return Integer.parseInt(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVFBSharedPrefences(int i, String str) {
        Log.m31d("FaceBeautyParametersHelper", "[setVFBSharedPrefences],index = " + i + ",value = " + str);
        SharedPreferences.Editor editorEdit = this.mIModuleCtrl.getComboPreferences().getLocal().edit();
        switch (i) {
            case 0:
                editorEdit.putString("pref_facebeauty_smooth_key", str);
                break;
            case 1:
                editorEdit.putString("pref_facebeauty_skin_color_key", str);
                break;
            case 3:
                editorEdit.putString("pref_facebeauty_big_eyes_key", str);
                break;
            case 4:
                editorEdit.putString("pref_facebeauty_slim_key", str);
                break;
            case 5:
                editorEdit.putInt("face-beauty-normal", Integer.parseInt(str));
                break;
        }
        editorEdit.apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMultiFbMode() {
        String settingValue = this.mISettingCtrl.getSettingValue("pref_face_beauty_multi_mode_key");
        Log.m31d("FaceBeautyParametersHelper", "isMultiFbMode,getCurrentFbMode: " + settingValue);
        return this.mActivity.getResources().getString(R.string.face_beauty_multi_mode).equals(settingValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean canShowFbIcon(int i) {
        return i > 0;
    }

    private String getDefaultValue(String str) {
        String parameter = this.mICameraDevice.getParameter(str);
        Log.m31d("FaceBeautyParametersHelper", "[getDefaultValue] key = " + str + ",valaue = " + parameter);
        if (parameter == null) {
            Log.m31d("FaceBeautyParametersHelper", "[getDefaultValue] the key = " + str + " not exsit,so return the value to 0");
            return "0";
        }
        return parameter;
    }
}
