package com.mediatek.camera.mode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ICameraMode;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraAppUi;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.IModuleCtrl;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.setting.ParametersHelper;
import com.mediatek.camera.setting.SettingItem;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.setting.preference.ListPreference;
import com.mediatek.camera.util.Log;
import com.mediatek.camera.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class VideoPreviewRule implements ISettingRule {
    private Activity mActivity;
    private String mConditionSettingKey;
    private String mCurrentVideoQuality;
    private ICameraAppUi mICameraAppUI;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private IModuleCtrl mIMoudleCtrl;
    private ISettingCtrl mISettingCtrl;
    private ICameraMode.CameraModeType mMode;
    private Parameters mParameters;
    private CamcorderProfile mProfile;
    private ICameraDeviceManager.ICameraDevice mTopICameraDevice;
    private Parameters mTopParameters;
    private Point previewSize;
    private String TAG = "VideoPreviewRule";
    private List<String> mConditions = new ArrayList();
    private List<List<String>> mResults = new ArrayList();
    private List<ISettingRule.MappingFinder> mMappingFinder = new ArrayList();
    private boolean mSwitchingPip = false;
    private boolean mHasOverride = false;

    public VideoPreviewRule(ICameraContext iCameraContext, ICameraMode.CameraModeType cameraModeType) {
        this.mConditionSettingKey = "video_key";
        Log.m34i(this.TAG, "[VideoPreviewRule]constructor...");
        this.mActivity = iCameraContext.getActivity();
        this.mISettingCtrl = iCameraContext.getSettingController();
        this.mICameraDeviceManager = iCameraContext.getCameraDeviceManager();
        this.mICameraAppUI = iCameraContext.getCameraAppUi();
        this.mIMoudleCtrl = iCameraContext.getModuleController();
        this.mMode = cameraModeType;
        this.mICameraContext = iCameraContext;
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            this.mConditionSettingKey = "video_pip_key";
        } else if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_STEREO) {
            this.mConditionSettingKey = "video_stereo_key";
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() throws NumberFormatException {
        String settingValue = this.mISettingCtrl.getSettingValue(this.mConditionSettingKey);
        getCameraDevice();
        int iConditionSatisfied = conditionSatisfied(settingValue);
        if ("0321".equals(this.mICameraContext.getFeatureConfig().whichDeanliChip())) {
            pipDenaliZSDRule(iConditionSatisfied);
        }
        Log.m34i(this.TAG, "execute index = " + iConditionSatisfied);
        if (iConditionSatisfied == -1) {
            if (!this.mHasOverride) {
                return;
            }
            this.mHasOverride = false;
            if (!this.mIMoudleCtrl.isNonePickIntent()) {
                return;
            }
            String settingValue2 = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key");
            this.mParameters = this.mICameraDevice.getParameters();
            if (this.mParameters == null) {
                return;
            }
            SettingUtils.setPreviewSize(this.mActivity, this.mParameters, settingValue2);
            String settingValue3 = this.mISettingCtrl.getSettingValue("pref_camera_picturesize_key");
            int iIndexOf = settingValue3.indexOf(120);
            this.mParameters.setPictureSize(Integer.parseInt(settingValue3.substring(0, iIndexOf)), Integer.parseInt(settingValue3.substring(iIndexOf + 1)));
            return;
        }
        setVideoPreviewSize();
        this.mHasOverride = true;
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
        this.mResults.add(list);
        this.mMappingFinder.add(mappingFinder);
    }

    public void updateProfile() {
        fetchProfile(getQuality(), this.mICameraDeviceManager.getCurrentCameraId());
    }

    public CamcorderProfile getProfile() {
        return this.mProfile;
    }

    public String getConditionKey() {
        return this.mConditionSettingKey;
    }

    private void setVideoPreviewSize() {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            currentCameraId = this.mICameraDeviceManager.getBackCameraId();
        }
        fetchProfile(getQuality(), currentCameraId);
        this.mParameters = this.mICameraDevice.getParameters();
        setPreviewSize(this.mProfile, this.mParameters);
        reviseVideoCapability(this.mParameters, this.mProfile);
        setPreviewFrameRate(this.mParameters, this.mProfile.videoFrameRate);
        setPictureSize(this.mProfile, this.mParameters);
        if (this.mMode != ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            setVideoSize(this.mProfile, this.mParameters);
        }
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            this.mTopICameraDevice.applyParameters();
        }
    }

    private int getQuality() {
        String settingValue;
        if ("on".equals(this.mISettingCtrl.getSettingValue("pref_slow_motion_key")) && (!this.mSwitchingPip)) {
            settingValue = this.mISettingCtrl.getSettingValue("pref_slow_motion_video_quality_key");
        } else if (this.mSwitchingPip) {
            settingValue = this.mCurrentVideoQuality;
            this.mISettingCtrl.setSettingValue("pref_video_quality_key", settingValue, this.mICameraDeviceManager.getCurrentCameraId());
        } else if ("on".equals(this.mISettingCtrl.getSettingValue("video_stereo_key"))) {
            settingValue = this.mISettingCtrl.getSettingValue("pref_refocus_video_quality_key");
        } else {
            settingValue = this.mISettingCtrl.getSettingValue("pref_video_quality_key");
        }
        this.mCurrentVideoQuality = settingValue;
        int iIntValue = Integer.valueOf(settingValue).intValue();
        Intent intent = this.mIMoudleCtrl.getIntent();
        if (intent.hasExtra("android.intent.extra.videoQuality")) {
            iIntValue = intent.getIntExtra("android.intent.extra.videoQuality", 0);
            if (iIntValue <= 0) {
                iIntValue = 0;
            } else if (!CamcorderProfile.hasProfile(this.mICameraDeviceManager.getCurrentCameraId(), iIntValue)) {
                iIntValue = CamcorderProfile.hasProfile(this.mICameraDeviceManager.getCurrentCameraId(), 5) ? 5 : 0;
            }
        }
        Log.m34i(this.TAG, "[getQuality] quality = " + iIntValue);
        return iIntValue;
    }

    private CamcorderProfile fetchProfile(int i, int i2) {
        Log.m34i(this.TAG, "[fetchProfile](" + i + ",  cameraId = " + i2 + ")");
        this.mProfile = CamcorderProfile.get(i2, i);
        if (this.mProfile != null) {
            Log.m34i(this.TAG, "[fetchProfile()] mProfile.videoFrameRate=" + this.mProfile.videoFrameRate + ", mProfile.videoFrameWidth=" + this.mProfile.videoFrameWidth + ", mProfile.videoFrameHeight=" + this.mProfile.videoFrameHeight + ", mProfile.audioBitRate=" + this.mProfile.audioBitRate + ", mProfile.videoBitRate=" + this.mProfile.videoBitRate + ", mProfile.quality=" + this.mProfile.quality + ", mProfile.duration=" + this.mProfile.duration);
        }
        this.mICameraAppUI.setCamcorderProfile(this.mProfile);
        return this.mProfile;
    }

    private void setPictureSize(CamcorderProfile camcorderProfile, Parameters parameters) {
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            parameters.setPictureSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
            updateTopParameters();
            setTopCameraPictureSize(this.mTopParameters, parameters.getPictureSize());
            Log.m34i(this.TAG, "[setPictureSize] width " + camcorderProfile.videoFrameWidth + " * height " + camcorderProfile.videoFrameHeight);
            return;
        }
        if (parameters.isVideoSnapshotSupported()) {
            Camera.Size optimalVideoSnapshotPictureSize = Util.getOptimalVideoSnapshotPictureSize(parameters.getSupportedPictureSizes(), this.previewSize.x / this.previewSize.y);
            Camera.Size pictureSize = parameters.getPictureSize();
            if (optimalVideoSnapshotPictureSize != null) {
                if (!pictureSize.equals(optimalVideoSnapshotPictureSize)) {
                    parameters.setPictureSize(optimalVideoSnapshotPictureSize.width, optimalVideoSnapshotPictureSize.height);
                    return;
                }
                return;
            }
            Log.m34i(this.TAG, "error optimalSize is null");
            return;
        }
        parameters.setPictureSize(this.previewSize.x, this.previewSize.y);
        Log.m34i(this.TAG, "[[setPictureSize] width " + this.previewSize.x + " *  height =" + this.previewSize.y);
    }

    private void setVideoSize(CamcorderProfile camcorderProfile, Parameters parameters) {
        if (camcorderProfile != null && parameters != null) {
            String str = Integer.toString(this.mProfile.videoFrameWidth) + "x" + Integer.toString(this.mProfile.videoFrameHeight);
            Log.m34i(this.TAG, "video-size = " + str);
            parameters.set("video-size", str);
        }
    }

    private void setTopCameraPictureSize(Parameters parameters, Camera.Size size) {
        Log.m34i(this.TAG, "[setTopCameraPictureSize] targetPictureSize width = " + size.width + " height = " + size.height);
        if (parameters != null) {
            Camera.Size mininalPIPTopSize = getMininalPIPTopSize(parameters.getSupportedPictureSizes(), size.width / size.height);
            if (mininalPIPTopSize != null) {
                size = mininalPIPTopSize;
            }
            parameters.setPictureSize(size.width, size.height);
            Log.m34i(this.TAG, "[setTopCameraPictureSize] miniPictureSize width = " + size.width + " height = " + size.height);
        }
    }

    private void setPreviewSize(CamcorderProfile camcorderProfile, Parameters parameters) {
        if (this.mMode != ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            this.previewSize = computeDesiredPreviewSize(camcorderProfile, parameters);
            parameters.setPreviewSize(this.previewSize.x, this.previewSize.y);
            Log.m34i(this.TAG, "[setPreviewSize] width " + this.previewSize.x + " *  height =" + this.previewSize.y);
        } else {
            parameters.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
            updateTopParameters();
            if (this.mTopParameters != null) {
                this.mTopParameters.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                Log.m34i(this.TAG, "[setPreviewSize] width " + camcorderProfile.videoFrameWidth + " *  height " + camcorderProfile.videoFrameHeight);
            }
        }
    }

    private void getCameraDevice() {
        if (this.mICameraDeviceManager != null) {
            this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mICameraDeviceManager.getCurrentCameraId());
            if (this.mTopICameraDevice != null) {
                this.mSwitchingPip = this.mTopICameraDevice.getCameraId() == this.mICameraDeviceManager.getCurrentCameraId();
            } else {
                this.mSwitchingPip = false;
            }
            updateTopParameters();
        }
    }

    private void updateTopParameters() {
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            this.mTopICameraDevice = this.mICameraDeviceManager.getCameraDevice(getTopCameraId());
            if (this.mTopICameraDevice != null) {
                this.mTopParameters = this.mTopICameraDevice.getParameters();
            }
        }
    }

    private int getTopCameraId() {
        if (this.mICameraDeviceManager.getCurrentCameraId() == this.mICameraDeviceManager.getBackCameraId()) {
            return this.mICameraDeviceManager.getFrontCameraId();
        }
        return this.mICameraDeviceManager.getBackCameraId();
    }

    private int conditionSatisfied(String str) {
        int iIndexOf = this.mConditions.indexOf(str);
        Log.m34i(this.TAG, "[conditionSatisfied]limitation index:" + iIndexOf + " conditionValue = " + str);
        return iIndexOf;
    }

    private Camera.Size getMininalPIPTopSize(List<Camera.Size> list, double d) {
        Camera.Size size = null;
        if (list == null || d < 0.0d) {
            Log.m34i(this.TAG, "[getMininalPIPTopSize] error sizes = " + list + " targetRatio = " + d);
            return null;
        }
        Iterator<T> it = list.iterator();
        while (true) {
            Camera.Size size2 = size;
            if (it.hasNext()) {
                size = (Camera.Size) it.next();
                double d2 = size.width / size.height;
                Log.m34i(this.TAG, "[getMininalPIPTopSize] (" + size.width + " ," + size.height + " )");
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

    private Point computeDesiredPreviewSize(CamcorderProfile camcorderProfile, Parameters parameters) throws NumberFormatException {
        int i;
        int i2;
        int i3;
        if (parameters.getSupportedVideoSizes() == null) {
            i2 = camcorderProfile.videoFrameWidth;
            i3 = camcorderProfile.videoFrameHeight;
        } else {
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            if ("on".equals(this.mISettingCtrl.getSettingValue("pref_slow_motion_key"))) {
                i = camcorderProfile.videoFrameWidth * camcorderProfile.videoFrameHeight;
            } else {
                Camera.Size preferredPreviewSizeForVideo = parameters.getPreferredPreviewSizeForVideo();
                i = preferredPreviewSizeForVideo.height * preferredPreviewSizeForVideo.width;
            }
            Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
            while (it.hasNext()) {
                Camera.Size next = it.next();
                if (next.height * next.width > i) {
                    it.remove();
                }
            }
            Camera.Size optimalPreviewSize = Util.getOptimalPreviewSize(this.mActivity, supportedPreviewSizes, camcorderProfile.videoFrameWidth / camcorderProfile.videoFrameHeight, true, false);
            if (optimalPreviewSize != null) {
                i2 = optimalPreviewSize.width;
                i3 = optimalPreviewSize.height;
            } else {
                i2 = camcorderProfile.videoFrameWidth;
                i3 = camcorderProfile.videoFrameHeight;
            }
        }
        return new Point(i2, i3);
    }

    private void reviseVideoCapability(Parameters parameters, CamcorderProfile camcorderProfile) {
        Log.m31d(this.TAG, "[reviseVideoCapability()] begin with profile.videoFrameRate = " + camcorderProfile.videoFrameRate);
        List<Integer> supportedPreviewFrameRates = parameters.getSupportedPreviewFrameRates();
        if (!isSupported(Integer.valueOf(camcorderProfile.videoFrameRate), supportedPreviewFrameRates)) {
            int maxSupportedPreviewFrameRate = getMaxSupportedPreviewFrameRate(supportedPreviewFrameRates);
            camcorderProfile.videoBitRate = (camcorderProfile.videoBitRate / camcorderProfile.videoFrameRate) * maxSupportedPreviewFrameRate;
            camcorderProfile.videoFrameRate = maxSupportedPreviewFrameRate;
        }
        if ("night".equals(this.mISettingCtrl.getSettingValue("pref_camera_scenemode_key"))) {
            camcorderProfile.videoFrameRate /= 2;
            camcorderProfile.videoBitRate /= 2;
        }
        Log.m31d(this.TAG, "[reviseVideoCapability()] end with profile.videoFrameRate = " + camcorderProfile.videoFrameRate);
    }

    private void setPreviewFrameRate(Parameters parameters, int i) {
        List<Integer> arrayList;
        List<Integer> supportedPreviewFrameRates;
        updateTopParameters();
        if (i > 0) {
            arrayList = new ArrayList<>();
            arrayList.add(Integer.valueOf(i));
        } else {
            arrayList = null;
        }
        if (this.mMode == ICameraMode.CameraModeType.EXT_MODE_VIDEO_PIP) {
            List<Integer> pIPFrameRateZSDOff = this.mICameraDevice.getPIPFrameRateZSDOff();
            List<Integer> pIPFrameRateZSDOff2 = this.mTopICameraDevice.getPIPFrameRateZSDOff();
            Log.m34i(this.TAG, "[setPreviewFrameRate getPIPFrameRateZSDOff] pipFrameRates = " + pIPFrameRateZSDOff + " pipTopFrameRates = " + pIPFrameRateZSDOff2);
            if (pIPFrameRateZSDOff != null) {
                arrayList = pIPFrameRateZSDOff;
            }
            List<Integer> list = pIPFrameRateZSDOff2 != null ? pIPFrameRateZSDOff2 : null;
            closeDynamicFrameRate(this.mICameraDevice);
            closeDynamicFrameRate(this.mTopICameraDevice);
            supportedPreviewFrameRates = list;
        } else {
            supportedPreviewFrameRates = null;
        }
        List<Integer> supportedPreviewFrameRates2 = arrayList == null ? parameters.getSupportedPreviewFrameRates() : arrayList;
        if (this.mTopParameters != null && supportedPreviewFrameRates == null) {
            supportedPreviewFrameRates = this.mTopParameters.getSupportedPreviewFrameRates();
        }
        if (supportedPreviewFrameRates2 != null) {
            Integer num = (Integer) Collections.max(supportedPreviewFrameRates2);
            parameters.setPreviewFrameRate(num.intValue());
            Log.m34i(this.TAG, "[setPreviewFrameRate] max = " + num + " frameRates = " + supportedPreviewFrameRates2);
        }
        if (this.mTopParameters != null) {
            Integer num2 = (Integer) Collections.max(supportedPreviewFrameRates);
            this.mTopParameters.setPreviewFrameRate(num2.intValue());
            Log.m34i(this.TAG, "[top graphic] setPreviewFrameRate max = " + num2 + " topFrameRates = " + supportedPreviewFrameRates);
        }
    }

    private void closeDynamicFrameRate(ICameraDeviceManager.ICameraDevice iCameraDevice) {
        if (iCameraDevice == null) {
            Log.m34i(this.TAG, "[closeDynamicFrameRate] but why parameters is null");
            return;
        }
        boolean zIsDynamicFrameRateSupported = iCameraDevice.isDynamicFrameRateSupported();
        if (zIsDynamicFrameRateSupported) {
            iCameraDevice.setDynamicFrameRate(false);
        }
        Log.m34i(this.TAG, "[closeDynamicFrameRate] support = " + zIsDynamicFrameRateSupported);
    }

    private boolean isSupported(Object obj, List<?> list) {
        return list != null && list.indexOf(obj) >= 0;
    }

    private int getMaxSupportedPreviewFrameRate(List<Integer> list) {
        int iIntValue = 0;
        Iterator<T> it = list.iterator();
        while (true) {
            int i = iIntValue;
            if (it.hasNext()) {
                iIntValue = ((Integer) it.next()).intValue();
                if (iIntValue <= i) {
                    iIntValue = i;
                }
            } else {
                Log.m31d(this.TAG, "[getMaxSupportedPreviewFrameRate()] return " + i);
                return i;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:48:0x0113  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void pipDenaliZSDRule(int r10) {
        /*
            Method dump skipped, instructions count: 278
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.mode.VideoPreviewRule.pipDenaliZSDRule(int):void");
    }

    private void setResultSettingValue(int i, String str, String str2, boolean z, SettingItem settingItem) {
        int currentCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        Parameters parameters = this.mICameraDeviceManager.getCameraDevice(currentCameraId).getParameters();
        settingItem.setValue(str);
        ListPreference listPreference = settingItem.getListPreference();
        if (listPreference != null) {
            listPreference.setOverrideValue(str2, z);
        }
        ParametersHelper.setParametersValue(parameters, currentCameraId, settingItem.getKey(), str);
    }
}
