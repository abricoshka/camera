package com.android.camera.bridge;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.media.CameraProfile;
import android.view.SurfaceHolder;
import com.android.camera.CameraManager;
import com.android.camera.ComboPreferences;
import com.android.camera.Log;
import com.android.camera.Util;
import java.util.List;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CameraDeviceExt implements ICameraDeviceExt {
    private final Activity mActivity;
    private final CameraManager.CameraProxy mCameraDevice;
    private int mCameraDisplayOrientation;
    private final int mCameraId;
    private int mDisplayOrientation;
    private int mDisplayRotation;
    private final Camera.Parameters mInitialParams;
    private String mLastHdrMode;
    private Camera.Size mLastPictureSize;
    private String mLastSceneMode;
    private Camera.Parameters mParameters;
    private final ParametersExt mParametersExt;
    private final ComboPreferences mPreferences;
    private String mLastDngState = "off";
    private int mJpegRotation = -1;

    CameraDeviceExt(Activity activity, CameraManager.CameraProxy cameraProxy, Camera.Parameters parameters, int i, ComboPreferences comboPreferences) {
        Assert.assertNotNull(cameraProxy);
        Assert.assertNotNull(parameters);
        this.mActivity = activity;
        this.mCameraDevice = cameraProxy;
        this.mInitialParams = parameters;
        this.mParameters = this.mInitialParams.copy();
        this.mCameraId = i;
        this.mPreferences = comboPreferences;
        this.mParametersExt = new ParametersExt(this.mCameraDevice, this.mParameters, i);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Parameters getInitialParams() {
        return this.mInitialParams;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Parameters getParameters() {
        return this.mParameters;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public ParametersExt getParametersExt() {
        return this.mParametersExt;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getCameraId() {
        return this.mCameraId;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public CameraManager.CameraProxy getCameraDevice() {
        return this.mCameraDevice;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewDisplayAsync(SurfaceHolder surfaceHolder) {
        this.mCameraDevice.setPreviewDisplayAsync(surfaceHolder);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewTextureAsync(SurfaceTexture surfaceTexture) {
        this.mCameraDevice.setPreviewTextureAsync(surfaceTexture);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void stopFaceDetection() {
        this.mCameraDevice.setFaceDetectionListener(null);
        this.mCameraDevice.stopFaceDetection();
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.mCameraDevice.setOneShotPreviewCallback(previewCallback);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setErrorCallback(Camera.ErrorCallback errorCallback) {
        this.mCameraDevice.setErrorCallback(errorCallback);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener) {
        this.mCameraDevice.setFaceDetectionListener(faceDetectionListener);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPhotoModeParameters(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.1
            @Override // java.lang.Runnable
            public void run() {
                if (z) {
                    CameraDeviceExt.this.mParametersExt.setCameraMode(1);
                } else {
                    CameraDeviceExt.this.mParametersExt.setCameraMode(0);
                }
                CameraDeviceExt.this.mParametersExt.setCaptureMode("normal");
                CameraDeviceExt.this.mParametersExt.setBurstShotNum(1);
                CameraDeviceExt.this.mParametersExt.setRecordingHint(false);
                CameraDeviceExt.this.mParametersExt.setJpegQuality(CameraProfile.getJpegEncodingQualityParameter(CameraDeviceExt.this.mCameraId, 2));
                CameraDeviceExt.this.mParametersExt.enableRecordingSound(String.valueOf(0));
                String str = CameraDeviceExt.this.mParametersExt.get("mtk-heartbeat-monitor-supported");
                if (str != null && Boolean.valueOf(str).booleanValue()) {
                    CameraDeviceExt.this.mParametersExt.set("mtk-heartbeat-monitor", "true");
                }
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x00a7  */
    @Override // com.android.camera.bridge.ICameraDeviceExt
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setPreviewSizeFull() throws java.lang.NumberFormatException {
        /*
            r6 = this;
            r1 = 0
            r5 = 0
            com.android.camera.ComboPreferences r0 = r6.mPreferences
            java.lang.String r2 = "pref_camera_picturesize_ratio_key"
            java.lang.String r0 = r0.getString(r2, r1)
            android.app.Activity r2 = r6.mActivity
            com.android.camera.bridge.ParametersExt r3 = r6.mParametersExt
            java.util.List r2 = com.mediatek.camera.setting.SettingUtils.buildPreviewRatios(r2, r3)
            if (r2 == 0) goto L36
            int r3 = r2.size()
            if (r3 <= 0) goto L36
            com.android.camera.ComboPreferences r0 = r6.mPreferences
            android.content.SharedPreferences$Editor r3 = r0.edit()
            int r0 = r2.size()
            int r0 = r0 + (-1)
            java.lang.Object r0 = r2.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "pref_camera_picturesize_ratio_key"
            r3.putString(r2, r0)
            r3.apply()
        L36:
            android.app.Activity r2 = r6.mActivity
            com.android.camera.bridge.ParametersExt r3 = r6.mParametersExt
            com.mediatek.camera.setting.SettingUtils.setPreviewSize(r2, r3, r0)
            com.android.camera.ComboPreferences r2 = r6.mPreferences
            java.lang.String r3 = "pref_camera_picturesize_key"
            java.lang.String r2 = r2.getString(r3, r1)
            int r3 = com.mediatek.camera.setting.SettingUtils.getLimitResolution()
            if (r3 <= 0) goto La7
            r4 = 120(0x78, float:1.68E-43)
            int r4 = r2.indexOf(r4)
            java.lang.String r5 = r2.substring(r5, r4)
            int r5 = java.lang.Integer.parseInt(r5)
            int r4 = r4 + 1
            java.lang.String r4 = r2.substring(r4)
            int r4 = java.lang.Integer.parseInt(r4)
            int r4 = r4 * r5
            if (r4 <= r3) goto La7
        L67:
            com.android.camera.bridge.ParametersExt r2 = r6.mParametersExt
            java.util.List r0 = com.mediatek.camera.setting.SettingUtils.buildSupportedPictureSizeByRatio(r2, r0)
            com.mediatek.camera.setting.SettingUtils.sortSizesInAscending(r0)
            if (r3 <= 0) goto L75
            com.mediatek.camera.setting.SettingUtils.filterLimitResolution(r0)
        L75:
            if (r0 == 0) goto L99
            int r2 = r0.size()
            if (r2 <= 0) goto L99
            int r1 = r0.size()
            int r1 = r1 + (-1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            com.android.camera.ComboPreferences r1 = r6.mPreferences
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = "pref_camera_picturesize_key"
            r1.putString(r2, r0)
            r1.apply()
            r1 = r0
        L99:
            android.graphics.Point r0 = com.mediatek.camera.setting.SettingUtils.getSize(r1)
            com.android.camera.bridge.ParametersExt r1 = r6.mParametersExt
            int r2 = r0.x
            int r0 = r0.y
            r1.setPictureSize(r2, r0)
            return
        La7:
            r1 = r2
            goto L67
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.bridge.CameraDeviceExt.setPreviewSizeFull():void");
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x00a1  */
    @Override // com.android.camera.bridge.ICameraDeviceExt
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setPreviewSize() throws java.lang.NumberFormatException {
        /*
            r6 = this;
            r1 = 0
            r5 = 0
            com.android.camera.ComboPreferences r0 = r6.mPreferences
            java.lang.String r2 = "pref_camera_picturesize_ratio_key"
            java.lang.String r0 = r0.getString(r2, r1)
            android.app.Activity r2 = r6.mActivity
            com.android.camera.bridge.ParametersExt r3 = r6.mParametersExt
            java.util.List r2 = com.mediatek.camera.setting.SettingUtils.buildPreviewRatios(r2, r3)
            if (r2 == 0) goto L30
            int r3 = r2.size()
            if (r3 <= 0) goto L30
            com.android.camera.ComboPreferences r0 = r6.mPreferences
            android.content.SharedPreferences$Editor r3 = r0.edit()
            java.lang.Object r0 = r2.get(r5)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r2 = "pref_camera_picturesize_ratio_key"
            r3.putString(r2, r0)
            r3.apply()
        L30:
            android.app.Activity r2 = r6.mActivity
            com.android.camera.bridge.ParametersExt r3 = r6.mParametersExt
            com.mediatek.camera.setting.SettingUtils.setPreviewSize(r2, r3, r0)
            com.android.camera.ComboPreferences r2 = r6.mPreferences
            java.lang.String r3 = "pref_camera_picturesize_key"
            java.lang.String r2 = r2.getString(r3, r1)
            int r3 = com.mediatek.camera.setting.SettingUtils.getLimitResolution()
            if (r3 <= 0) goto La1
            r4 = 120(0x78, float:1.68E-43)
            int r4 = r2.indexOf(r4)
            java.lang.String r5 = r2.substring(r5, r4)
            int r5 = java.lang.Integer.parseInt(r5)
            int r4 = r4 + 1
            java.lang.String r4 = r2.substring(r4)
            int r4 = java.lang.Integer.parseInt(r4)
            int r4 = r4 * r5
            if (r4 <= r3) goto La1
        L61:
            com.android.camera.bridge.ParametersExt r2 = r6.mParametersExt
            java.util.List r0 = com.mediatek.camera.setting.SettingUtils.buildSupportedPictureSizeByRatio(r2, r0)
            com.mediatek.camera.setting.SettingUtils.sortSizesInAscending(r0)
            if (r3 <= 0) goto L6f
            com.mediatek.camera.setting.SettingUtils.filterLimitResolution(r0)
        L6f:
            if (r0 == 0) goto L93
            int r2 = r0.size()
            if (r2 <= 0) goto L93
            int r1 = r0.size()
            int r1 = r1 + (-1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            com.android.camera.ComboPreferences r1 = r6.mPreferences
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r2 = "pref_camera_picturesize_key"
            r1.putString(r2, r0)
            r1.apply()
            r1 = r0
        L93:
            android.graphics.Point r0 = com.mediatek.camera.setting.SettingUtils.getSize(r1)
            com.android.camera.bridge.ParametersExt r1 = r6.mParametersExt
            int r2 = r0.x
            int r0 = r0.y
            r1.setPictureSize(r2, r0)
            return
        La1:
            r1 = r2
            goto L61
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.camera.bridge.CameraDeviceExt.setPreviewSize():void");
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isSceneModeChanged() {
        boolean z = false;
        String str = this.mParametersExt.get("scene-mode");
        if (str != null) {
            z = !str.equals(this.mLastSceneMode);
        } else if (this.mLastSceneMode != null) {
            z = true;
        }
        Log.m5d("CameraDeviceExt", "[isSceneModeChanged] curScene:" + str + ",mLastSceneMode:" + this.mLastSceneMode);
        return z;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isHdrChanged() {
        boolean z = false;
        String str = this.mParametersExt.get("video-hdr");
        if (str != null) {
            z = !str.equals(this.mLastHdrMode);
        } else if (this.mLastHdrMode != null) {
            z = true;
        }
        Log.m5d("CameraDeviceExt", "[ishdrChanged] hdr:" + str + ",oldHdr:" + this.mLastHdrMode);
        return z;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isPictureSizeChanged() {
        boolean z = false;
        Camera.Size pictureSize = this.mParametersExt.getPictureSize();
        if (!pictureSize.equals(this.mLastPictureSize)) {
            z = true;
        }
        Log.m5d("CameraDeviceExt", "[isPictureSizeChanged] size : " + pictureSize + ", oldsize : " + this.mLastPictureSize);
        return z;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void updateParameters() {
        this.mLastHdrMode = this.mParametersExt.get("video-hdr");
        this.mLastPictureSize = this.mParametersExt.getPictureSize();
        Log.m5d("CameraDeviceExt", "[mLastZsdMode]mLastPictureSize" + this.mLastPictureSize);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void applyParametersToServer() {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.2
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceExt.this.mCameraDevice.setParameters(CameraDeviceExt.this.mParameters);
            }
        });
        Log.m5d("CameraDeviceExt", "[applyParametersToServer]");
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Size getPreviewSize() {
        return this.mParametersExt.getPreviewSize();
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public String getZsdMode() {
        return this.mParametersExt.getZSDMode();
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFocusMode(String str) {
        this.mParametersExt.setFocusMode(str);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isSupportFocusMode(String str) {
        return isSupported(str, this.mParametersExt.getSupportedFocusModes());
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void fetchParametersFromServer() {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.3
            @Override // java.lang.Runnable
            public void run() {
                Log.m5d("CameraDeviceExt", "fetchParameterFromServer() mParameters=" + CameraDeviceExt.this.mParameters.flatten() + ", mCameraDevice=" + CameraDeviceExt.this.mCameraDevice);
                CameraDeviceExt.this.mParameters = CameraDeviceExt.this.mCameraDevice.getParameters();
                CameraDeviceExt.this.mParametersExt.setparameters(CameraDeviceExt.this.mParameters);
            }
        });
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewFormat(int i) {
        this.mParametersExt.setPreviewFormat(i);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setDisplayOrientation(boolean z) {
        this.mDisplayRotation = Util.getDisplayRotation(this.mActivity);
        this.mDisplayOrientation = Util.getDisplayOrientation(this.mDisplayRotation, this.mCameraId);
        this.mCameraDisplayOrientation = Util.getDisplayOrientation(0, this.mCameraId);
        this.mCameraDevice.setDisplayOrientation(z ? this.mDisplayOrientation : this.mCameraDisplayOrientation);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getDisplayOrientation() {
        return this.mDisplayOrientation;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getCameraDisplayOrientation() {
        return this.mCameraDisplayOrientation;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setJpegRotation(int i) {
        this.mJpegRotation = -1;
        this.mJpegRotation = Util.getJpegRotation(this.mCameraId, i);
        this.mParametersExt.setRotation(this.mJpegRotation);
        Log.m5d("CameraDeviceExt", "setRotationToParameters() mCameraId=" + this.mCameraId + ", mOrientation=" + i + ", jpegRotation = " + this.mJpegRotation);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getJpegRotation() {
        return this.mJpegRotation;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setGpsParameters(final Location location) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.4
            @Override // java.lang.Runnable
            public void run() {
                Util.setGpsParameters(CameraDeviceExt.this.mParameters, location);
            }
        });
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void updateDngState(String str) {
        this.mLastDngState = str;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public String getDngState() {
        return this.mLastDngState;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setAutoExposureLock(boolean z) {
        this.mParametersExt.setAutoExposureLock(z);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setAutoWhiteBalanceLock(boolean z) {
        this.mParametersExt.setAutoWhiteBalanceLock(z);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFocusAreas(List<Camera.Area> list) {
        this.mParametersExt.setFocusAreas(list);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setMeteringAreas(List<Camera.Area> list) {
        this.mParametersExt.setMeteringAreas(list);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setCapturePath(String str) {
        this.mParametersExt.setCapturePath(str);
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isZoomSupported() {
        return this.mParametersExt.isZoomSupported();
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getZoom() {
        return this.mParametersExt.getZoom();
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setZoom(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.5
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceExt.this.mParameters.setZoom(i);
                CameraDeviceExt.this.mCameraDevice.setParametersAsync(CameraDeviceExt.this.mParameters, i);
            }
        });
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPhotoModeBasicParameters() {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.CameraDeviceExt.6
            @Override // java.lang.Runnable
            public void run() {
                CameraDeviceExt.this.mParametersExt.setCameraMode(1);
                CameraDeviceExt.this.mParametersExt.setRecordingHint(false);
                CameraDeviceExt.this.mParametersExt.enableRecordingSound(String.valueOf(0));
            }
        });
    }

    private boolean isSupported(Object obj, List<?> list) {
        return list != null && list.indexOf(obj) >= 0;
    }

    private void lockRun(Runnable runnable) {
        Log.m5d("CameraDeviceExt", "lockRun(" + runnable + ") mCameraDevice=" + this.mCameraDevice);
        if (this.mCameraDevice != null) {
            this.mCameraDevice.lockParametersRun(runnable);
        }
    }
}
