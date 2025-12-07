package com.mediatek.camera.mode.pip;

import android.app.Activity;
import android.hardware.Camera;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class PipPictureSizeRule implements ISettingRule {
    private ICameraDeviceManager deviceManager;
    private Activity mActivity;
    private ICameraDeviceManager.ICameraDevice mBackCamDevice;
    private ICameraContext mCameraContext;
    private ISettingCtrl mISettingCtrl;
    private CharSequence[] mOriEntryValues;
    private Parameters mParameters;
    private ICameraDeviceManager.ICameraDevice mTopCamDevice;
    private Parameters mTopParameters;
    private long PICTURE_SIZE_3M = 3145728;
    private long PICTURE_SIZE_8M = 8388608;
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinders = new ArrayList();

    public PipPictureSizeRule(ICameraContext iCameraContext) {
        Log.m31d("PipPictureSizeRule", "[PreviewSizeRule]constructor...");
        this.mCameraContext = iCameraContext;
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() throws NumberFormatException {
        this.deviceManager = this.mCameraContext.getCameraDeviceManager();
        this.mBackCamDevice = this.deviceManager.getCameraDevice(this.deviceManager.getCurrentCameraId());
        if (this.mBackCamDevice == null) {
            Log.m36w("PipPictureSizeRule", "[execute] mBackCamDevice is null!");
            return;
        }
        this.mTopCamDevice = this.deviceManager.getCameraDevice(getTopCameraId());
        this.mISettingCtrl = this.mCameraContext.getSettingController();
        this.mActivity = this.mCameraContext.getActivity();
        this.mParameters = this.mBackCamDevice.getParameters();
        if (this.mParameters == null) {
            Log.m36w("PipPictureSizeRule", "[execute] mParameters is null!");
            return;
        }
        if (this.mTopCamDevice != null) {
            this.mTopParameters = this.mTopCamDevice.getParameters();
        }
        int iConditionSatisfied = conditionSatisfied(this.mISettingCtrl.getSettingValue("photo_pip_key"));
        pipPictureSizeRule(iConditionSatisfied);
        String settingValue = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_key");
        Log.m31d("PipPictureSizeRule", "[execute]index = " + iConditionSatisfied);
        if (iConditionSatisfied == -1) {
            SettingUtils.setCameraPictureSize(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_key"), this.mParameters.getSupportedPictureSizes(), this.mParameters, this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"), this.mActivity);
            return;
        }
        setPictureSize(settingValue);
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        Log.m31d("PipPictureSizeRule", "[addLimitation]condition = " + str);
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinders.add(mappingFinder);
    }

    private int conditionSatisfied(String str) {
        return this.mConditions.indexOf(str);
    }

    private void setPreviewFrameRate() {
        List<Integer> pIPFrameRateZSDOff;
        List<Integer> list;
        List<Integer> list2;
        if ("on".equals(this.mISettingCtrl.getSettingValue("pref_camera_zsd_key"))) {
            List<Integer> pIPFrameRateZSDOn = this.mParameters.getPIPFrameRateZSDOn();
            pIPFrameRateZSDOff = this.mTopParameters != null ? this.mTopParameters.getPIPFrameRateZSDOn() : null;
            Log.m31d("PipPictureSizeRule", "getPIPFrameRateZSDOn pipFrameRates " + pIPFrameRateZSDOn + " pipTopFrameRates = " + pIPFrameRateZSDOff);
            List<Integer> list3 = pIPFrameRateZSDOff;
            list = pIPFrameRateZSDOn;
            list2 = list3;
        } else {
            List<Integer> pIPFrameRateZSDOff2 = this.mParameters.getPIPFrameRateZSDOff();
            pIPFrameRateZSDOff = this.mTopParameters != null ? this.mTopParameters.getPIPFrameRateZSDOff() : null;
            Log.m31d("PipPictureSizeRule", "getPIPFrameRateZSDOff pipFrameRates = " + pIPFrameRateZSDOff2 + " pipTopFrameRates = " + pIPFrameRateZSDOff);
            List<Integer> list4 = pIPFrameRateZSDOff;
            list = pIPFrameRateZSDOff2;
            list2 = list4;
        }
        closeDynamicFrameRate(this.mParameters);
        closeDynamicFrameRate(this.mTopParameters);
        if (list != null) {
            this.mParameters.setPreviewFrameRate(((Integer) Collections.max(list)).intValue());
        }
        if (this.mTopParameters != null) {
            this.mTopParameters.setPreviewFrameRate(((Integer) Collections.max(list2)).intValue());
        }
    }

    private void closeDynamicFrameRate(Parameters parameters) {
        if (parameters == null) {
            Log.m36w("PipPictureSizeRule", "closeDynamicFrameRate but why parameters is null");
            return;
        }
        boolean zIsDynamicFrameRateSupported = parameters.isDynamicFrameRateSupported();
        if (zIsDynamicFrameRateSupported) {
            parameters.setDynamicFrameRate(false);
        }
        Log.m31d("PipPictureSizeRule", "closeDynamicFrameRate support = " + zIsDynamicFrameRateSupported);
    }

    private void setPictureSize(String str) {
        Log.m31d("PipPictureSizeRule", "setPictureSize(" + str + ")");
        SettingUtils.setCameraPictureSize(this.mISettingCtrl.getSettingValue("pref_camera_picturesize_key"), this.mParameters.getSupportedPictureSizes(), this.mParameters, this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"), this.mActivity);
        if (this.mTopParameters != null) {
            setTopCameraPictureSize(this.mParameters.getPictureSize());
        }
        setPreviewFrameRate();
    }

    private void setTopCameraPictureSize(Camera.Size size) {
        Log.m31d("PipPictureSizeRule", "setTopCameraPictureSize targetPictureSize width = " + size.width + " height = " + size.height);
        if (this.mTopParameters != null) {
            Camera.Size mininalPIPTopSize = getMininalPIPTopSize(this.mTopParameters.getSupportedPictureSizes(), size.width / size.height);
            if (mininalPIPTopSize != null) {
                size = mininalPIPTopSize;
            }
            this.mTopParameters.setPictureSize(size.width, size.height);
            Log.m31d("PipPictureSizeRule", "setTopCameraPictureSize miniPictureSize width = " + size.width + " height = " + size.height);
        }
    }

    private int getTopCameraId() {
        return this.deviceManager.getCurrentCameraId() == this.deviceManager.getBackCameraId() ? this.deviceManager.getFrontCameraId() : this.deviceManager.getBackCameraId();
    }

    public Camera.Size getMininalPIPTopSize(List<Camera.Size> list, double d) {
        Camera.Size size = null;
        if (list == null || d < 0.0d) {
            Log.m36w("PipPictureSizeRule", "getMininalPIPTopSize error sizes = " + list + " targetRatio = " + d);
            return null;
        }
        Iterator<T> it = list.iterator();
        while (true) {
            Camera.Size size2 = size;
            if (it.hasNext()) {
                size = (Camera.Size) it.next();
                double d2 = size.width / size.height;
                Log.m31d("PipPictureSizeRule", "getMininalPIPTopSize width = " + size.width + " height = " + size.height);
                if (Math.abs(d2 - d) > 0.02d) {
                    size = size2;
                } else if (size2 != null && size.width >= size2.width) {
                    size = size2;
                }
            } else {
                return size2;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x01f1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void pipPictureSizeRule(int r19) throws java.lang.NumberFormatException {
        /*
            Method dump skipped, instructions count: 503
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.pip.PipPictureSizeRule.pipPictureSizeRule(int):void");
    }

    private void setResultSettingValue(int i, String str, String str2, boolean z, SettingItem settingItem) {
        int currentCameraId = this.deviceManager.getCurrentCameraId();
        Parameters parameters = this.deviceManager.getCameraDevice(currentCameraId).getParameters();
        settingItem.setValue(str);
        ListPreference listPreference = settingItem.getListPreference();
        if ("disable-value".equals(str2)) {
            if (listPreference != null) {
                listPreference.setEnabled(false);
            }
        } else {
            if (listPreference != null) {
                listPreference.setOverrideValue(str2, z);
            }
            ParametersHelper.setParametersValue(parameters, currentCameraId, settingItem.getKey(), str);
        }
    }
}
