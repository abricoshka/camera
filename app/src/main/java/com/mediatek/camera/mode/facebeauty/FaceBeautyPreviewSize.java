package com.mediatek.camera.mode.facebeauty;

import android.app.Activity;
import android.hardware.Camera;
import com.mediatek.camera.ICameraContext;
import com.mediatek.camera.ISettingCtrl;
import com.mediatek.camera.ISettingRule;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.setting.SettingUtils;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FaceBeautyPreviewSize implements ISettingRule {
    private Activity mActivity;
    private int mCameraId;
    private ICameraContext mICameraContext;
    private ICameraDeviceManager.ICameraDevice mICameraDevice;
    private ICameraDeviceManager mICameraDeviceManager;
    private ISettingCtrl mISettingCtrl;
    private List<Camera.Size> mSupportedPreviewSizes = null;
    private Camera.Size mCurrentPreviewSize = null;
    private List<String> mConditions = new ArrayList();

    public FaceBeautyPreviewSize(ICameraContext iCameraContext) {
        this.mICameraContext = iCameraContext;
        this.mActivity = this.mICameraContext.getActivity();
        this.mISettingCtrl = this.mICameraContext.getSettingController();
        Log.m34i("FaceBeautyPreviewSize", "[FaceBeautyPreviewSize]");
    }

    @Override // com.mediatek.camera.ISettingRule
    public void execute() throws NumberFormatException {
        int iIndexOf = this.mConditions.indexOf(this.mISettingCtrl.getSettingValue("face_beauty_key"));
        Log.m34i("FaceBeautyPreviewSize", "[execute],index = " + iIndexOf);
        initizeParameters();
        if (-1 == iIndexOf) {
            SettingUtils.setPreviewSize(this.mActivity, this.mICameraDevice.getParameters(), this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"));
        } else {
            setVFBPreviewSize();
        }
    }

    @Override // com.mediatek.camera.ISettingRule
    public void addLimitation(String str, List<String> list, ISettingRule.MappingFinder mappingFinder) {
        this.mConditions.add(str);
    }

    private void initizeParameters() {
        if (this.mICameraDeviceManager == null) {
            this.mICameraDeviceManager = this.mICameraContext.getCameraDeviceManager();
        }
        this.mCameraId = this.mICameraDeviceManager.getCurrentCameraId();
        this.mICameraDevice = this.mICameraDeviceManager.getCameraDevice(this.mCameraId);
        if (this.mICameraDevice == null) {
            Log.m32e("FaceBeautyPreviewSize", "[initizeParameters] current mICameraDevice is null");
            return;
        }
        this.mSupportedPreviewSizes = this.mICameraDevice.getSupportedPreviewSizes();
        this.mCurrentPreviewSize = this.mICameraDevice.getPreviewSize();
        Log.m34i("FaceBeautyPreviewSize", "[initizeParameters] mCurrentPreviewSize : " + this.mCurrentPreviewSize.width + " X ,width = " + this.mCurrentPreviewSize.height);
    }

    private void setVFBPreviewSize() throws NumberFormatException {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.mSupportedPreviewSizes.size()) {
                leftSupportedPreviewSize(this.mSupportedPreviewSizes);
                Camera.Size optimalPreviewSize = SettingUtils.getOptimalPreviewSize(this.mActivity, this.mICameraDevice.getParameters(), this.mISettingCtrl.getSettingValue("pref_camera_picturesize_ratio_key"));
                Log.m31d("FaceBeautyPreviewSize", "[setVFBPreviewSize] will set preview width = " + optimalPreviewSize.width + ",height = " + optimalPreviewSize.height);
                this.mICameraDevice.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                return;
            }
            if (this.mSupportedPreviewSizes.get(i2).width > 1920 || this.mSupportedPreviewSizes.get(i2).height > 1088) {
                Log.m35v("FaceBeautyPreviewSize", "will remove VFB not supported preview size[" + i2 + "],Width = " + this.mSupportedPreviewSizes.get(i2).width + ",Height = " + this.mSupportedPreviewSizes.get(i2).height);
                this.mSupportedPreviewSizes.remove(i2);
                i2--;
            }
            i = i2 + 1;
        }
    }

    private void leftSupportedPreviewSize(List<Camera.Size> list) {
        String str = "[leftSupportedPreviewSize] ";
        int i = 0;
        while (i < this.mSupportedPreviewSizes.size()) {
            String str2 = str + this.mSupportedPreviewSizes.get(i).width + "X" + this.mSupportedPreviewSizes.get(i).height + "; ";
            i++;
            str = str2;
        }
        Log.m31d("FaceBeautyPreviewSize", "[leftSupportedPreviewSize] is : " + str);
    }
}
