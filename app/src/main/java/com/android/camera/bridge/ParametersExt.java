package com.android.camera.bridge;

import android.hardware.Camera;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.camera.CameraManager;
import com.android.camera.Log;
import com.mediatek.camera.platform.Parameters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ParametersExt implements Parameters {
    private static final String TAG = ParametersExt.class.getSimpleName();
    private final CameraManager.CameraProxy mCameraDevice;
    private int mCameraId;
    private Camera.Parameters mParameters;

    public ParametersExt(CameraManager.CameraProxy cameraProxy, Camera.Parameters parameters, int i) {
        this.mCameraDevice = cameraProxy;
        this.mParameters = parameters;
        this.mCameraId = i;
    }

    public void setparameters(Camera.Parameters parameters) {
        this.mParameters = parameters;
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void set(final String str, final String str2) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.1
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set(str, str2);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void set(final String str, final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.2
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set(str, i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String get(String str) {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String str2 = this.mParameters.get(str);
                this.mCameraDevice.unlockParameters();
                return str2;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.get(str);
            }
        } catch (Throwable th) {
            this.mParameters.get(str);
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setPreviewSize(final int i, final int i2) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.3
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setPreviewSize(i, i2);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public Camera.Size getPreviewSize() {
        Camera.Size previewSize = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    previewSize = this.mParameters.getPreviewSize();
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    previewSize = this.mParameters.getPreviewSize();
                }
            }
            Log.m5d(TAG, "getPreviewSize, size:" + previewSize);
            android.util.Log.d("zbx", "getPreviewSize: size=" + previewSize.width + "x" + previewSize.height);
            return previewSize;
        } catch (Throwable th) {
            this.mParameters.getPreviewSize();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Camera.Size> getSupportedPreviewSizes() {
        return this.mParameters.getSupportedPreviewSizes();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Camera.Size> getSupportedVideoSizes() {
        return this.mParameters.getSupportedVideoSizes();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public Camera.Size getPreferredPreviewSizeForVideo() {
        return this.mParameters.getPreferredPreviewSizeForVideo();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setJpegQuality(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.4
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setJpegQuality(i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getJpegQuality() {
        if (this.mCameraDevice == null) {
            return 0;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                int jpegQuality = this.mParameters.getJpegQuality();
                this.mCameraDevice.unlockParameters();
                return jpegQuality;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getJpegQuality();
            }
        } catch (Throwable th) {
            this.mParameters.getJpegQuality();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setPreviewFrameRate(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.5
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setPreviewFrameRate(i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Integer> getSupportedPreviewFrameRates() {
        return this.mParameters.getSupportedPreviewFrameRates();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setPreviewFormat(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.6
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setPreviewFormat(i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getPreviewFormat() {
        if (this.mCameraDevice == null) {
            return 0;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                int previewFormat = this.mParameters.getPreviewFormat();
                this.mCameraDevice.unlockParameters();
                return previewFormat;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getPreviewFormat();
            }
        } catch (Throwable th) {
            this.mParameters.getPreviewFormat();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setPictureSize(final int i, final int i2) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.7
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setPictureSize(i, i2);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public Camera.Size getPictureSize() {
        Camera.Size pictureSize = null;
        if (this.mCameraDevice != null) {
            try {
                try {
                    this.mCameraDevice.lockParameters();
                    pictureSize = this.mParameters.getPictureSize();
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    pictureSize = this.mParameters.getPictureSize();
                }
            } catch (Throwable th) {
                this.mParameters.getPictureSize();
                throw th;
            }
        }
        Log.m5d(TAG, "getPictureSize, size:" + pictureSize);
        return pictureSize;
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Camera.Size> getSupportedPictureSizes() {
        if ((this.mCameraId != 0 || SystemProperties.get("persist.sys.backcam", "0").equals("0")) && (this.mCameraId != 1 || SystemProperties.get("persist.sys.frontcam", "0").equals("0"))) {
            return this.mParameters.getSupportedPictureSizes();
        }
        new ArrayList();
        return reWriteFakePictureSizes();
    }

    public void setRotation(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.8
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setRotation(i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getWhiteBalance() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String whiteBalance = this.mParameters.getWhiteBalance();
                this.mCameraDevice.unlockParameters();
                return whiteBalance;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getWhiteBalance();
            }
        } catch (Throwable th) {
            this.mParameters.getWhiteBalance();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setWhiteBalance(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.9
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setWhiteBalance(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedWhiteBalance() {
        return this.mParameters.getSupportedWhiteBalance();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getColorEffect() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String colorEffect = this.mParameters.getColorEffect();
                this.mCameraDevice.unlockParameters();
                return colorEffect;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getColorEffect();
            }
        } catch (Throwable th) {
            this.mParameters.getColorEffect();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setColorEffect(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.10
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setColorEffect(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedColorEffects() {
        return this.mParameters.getSupportedColorEffects();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getAntibanding() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String antibanding = this.mParameters.getAntibanding();
                this.mCameraDevice.unlockParameters();
                return antibanding;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getAntibanding();
            }
        } catch (Throwable th) {
            this.mParameters.getAntibanding();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setAntibanding(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.11
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setAntibanding(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedAntibanding() {
        return this.mParameters.getSupportedAntibanding();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getSceneMode() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String sceneMode = this.mParameters.getSceneMode();
                this.mCameraDevice.unlockParameters();
                return sceneMode;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getSceneMode();
            }
        } catch (Throwable th) {
            this.mParameters.getSceneMode();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setSceneMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.12
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setSceneMode(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedSceneModes() {
        return this.mParameters.getSupportedSceneModes();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getFlashMode() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String flashMode = this.mParameters.getFlashMode();
                this.mCameraDevice.unlockParameters();
                return flashMode;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getFlashMode();
            }
        } catch (Throwable th) {
            this.mParameters.getFlashMode();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setFlashMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.13
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setFlashMode(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedFlashModes() {
        return this.mParameters.getSupportedFlashModes();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getFocusMode() {
        if (this.mCameraDevice == null) {
            return null;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                String focusMode = this.mParameters.getFocusMode();
                this.mCameraDevice.unlockParameters();
                return focusMode;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getFocusMode();
            }
        } catch (Throwable th) {
            this.mParameters.getFocusMode();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setFocusMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.14
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setFocusMode(str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedFocusModes() {
        return this.mParameters.getSupportedFocusModes();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getExposureCompensation() {
        if (this.mCameraDevice == null) {
            return 0;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                int exposureCompensation = this.mParameters.getExposureCompensation();
                this.mCameraDevice.unlockParameters();
                return exposureCompensation;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getExposureCompensation();
            }
        } catch (Throwable th) {
            this.mParameters.getExposureCompensation();
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setExposureCompensation(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.15
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setExposureCompensation(i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getMaxExposureCompensation() {
        return this.mParameters.getMaxExposureCompensation();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getMinExposureCompensation() {
        return this.mParameters.getMinExposureCompensation();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public float getExposureCompensationStep() {
        return this.mParameters.getExposureCompensationStep();
    }

    public void setAutoExposureLock(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.16
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setAutoExposureLock(z);
            }
        });
    }

    public void setAutoWhiteBalanceLock(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.17
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setAutoWhiteBalanceLock(z);
            }
        });
    }

    public int getZoom() {
        if (this.mCameraDevice == null) {
            return 0;
        }
        try {
            try {
                this.mCameraDevice.lockParameters();
                int zoom = this.mParameters.getZoom();
                this.mCameraDevice.unlockParameters();
                return zoom;
            } catch (InterruptedException e) {
                Log.m7e(TAG, "lockParameters() not successfull.", e);
                return this.mParameters.getZoom();
            }
        } catch (Throwable th) {
            this.mParameters.getZoom();
            throw th;
        }
    }

    public boolean isZoomSupported() {
        return this.mParameters.isZoomSupported();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setCameraMode(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.18
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("mtk-cam-mode", i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getISOSpeed() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("iso-speed");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("iso-speed");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("iso-speed");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setISOSpeed(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.19
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("iso-speed", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedISOSpeed() {
        return split(get("iso-speed-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getEdgeMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("edge");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("edge");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("edge");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setEdgeMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.20
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("edge", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedEdgeMode() {
        return split(get("edge-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getHueMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("hue");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("hue");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("hue");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setHueMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.21
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("hue", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedHueMode() {
        return split(get("hue-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getSaturationMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("saturation");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("saturation");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("saturation");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setSaturationMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.22
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("saturation", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedSaturationMode() {
        return split(get("saturation-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getBrightnessMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("brightness");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("brightness");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("brightness");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setBrightnessMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.23
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("brightness", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedBrightnessMode() {
        return split(get("brightness-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getContrastMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("contrast");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("contrast");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("contrast");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setContrastMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.24
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("contrast", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedContrastMode() {
        return split(get("contrast-values"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getCaptureMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("cap-mode");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParameters() not successfull.", e);
                    str = this.mParameters.get("cap-mode");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("cap-mode");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setCaptureMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.25
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("cap-mode", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedCaptureMode() {
        ArrayList<String> arrayListSplit = split(get("cap-mode-values"));
        if (this.mCameraId > 0 && arrayListSplit.indexOf("continuousshot") >= 0) {
            arrayListSplit.remove("continuousshot");
        }
        return arrayListSplit;
    }

    public void setCapturePath(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.26
            @Override // java.lang.Runnable
            public void run() {
                if (str == null) {
                    ParametersExt.this.mParameters.remove("capfname");
                } else {
                    ParametersExt.this.mParameters.set("capfname", str);
                }
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setBurstShotNum(final int i) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.27
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("burst-num", i);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getZSDMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("zsd-mode");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParametersRun() not successfull.", e);
                    str = this.mParameters.get("zsd-mode");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("zsd-mode");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setZSDMode(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.28
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("zsd-mode", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<String> getSupportedZSDMode() {
        return split(get("zsd-mode-values"));
    }

    public void setFocusAreas(final List<Camera.Area> list) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.29
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setFocusAreas(list);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public int getMaxNumFocusAreas() {
        return this.mParameters.getMaxNumFocusAreas();
    }

    public void setMeteringAreas(final List<Camera.Area> list) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.30
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setMeteringAreas(list);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setRecordingHint(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.31
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setRecordingHint(z);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public boolean isVideoSnapshotSupported() {
        return this.mParameters.isVideoSnapshotSupported();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void enableRecordingSound(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.32
            @Override // java.lang.Runnable
            public void run() {
                if (str.equals("1") || str.equals("0")) {
                    ParametersExt.this.mParameters.set("rec-mute-ogg", str);
                }
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setVideoStabilization(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.33
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.setVideoStabilization(z);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public boolean isVideoStabilizationSupported() {
        return this.mParameters.isVideoStabilizationSupported();
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Integer> getPIPFrameRateZSDOn() {
        return splitInt(get("pip-fps-zsd-on"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public List<Integer> getPIPFrameRateZSDOff() {
        return splitInt(get("pip-fps-zsd-off"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setDynamicFrameRate(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.34
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("dynamic-frame-rate", z ? "true" : "false");
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public boolean isDynamicFrameRateSupported() {
        return "true".equals(this.mParameters.get("dynamic-frame-rate-supported"));
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setRefocusJpsFileName(final String str) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.35
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("refocus-jps-file-name", str);
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getDepthAFMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("stereo-depth-af");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParametersRun() not successfull.", e);
                    str = this.mParameters.get("stereo-depth-af");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("stereo-depth-af");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public String getDistanceMode() {
        String str = null;
        try {
            if (this.mCameraDevice != null) {
                try {
                    this.mCameraDevice.lockParameters();
                    str = this.mParameters.get("stereo-distance-measurement");
                    this.mCameraDevice.unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e(TAG, "lockParametersRun() not successfull.", e);
                    str = this.mParameters.get("stereo-distance-measurement");
                }
            }
            return str;
        } catch (Throwable th) {
            this.mParameters.get("stereo-distance-measurement");
            throw th;
        }
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setDepthAFMode(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.36
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("stereo-depth-af", z ? "on" : "off");
            }
        });
    }

    @Override // com.mediatek.camera.platform.Parameters
    public void setDistanceMode(final boolean z) {
        lockRun(new Runnable() { // from class: com.android.camera.bridge.ParametersExt.37
            @Override // java.lang.Runnable
            public void run() {
                ParametersExt.this.mParameters.set("stereo-distance-measurement", z ? "on" : "off");
            }
        });
    }

    private void lockRun(Runnable runnable) {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.lockParametersRun(runnable);
        }
    }

    private ArrayList<String> split(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            arrayList.add((String) it.next());
        }
        return arrayList;
    }

    private ArrayList<Integer> splitInt(String str) {
        if (str == null) {
            return null;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(str);
        ArrayList<Integer> arrayList = new ArrayList<>();
        Iterator it = simpleStringSplitter.iterator();
        while (it.hasNext()) {
            arrayList.add(Integer.valueOf(Integer.parseInt((String) it.next())));
        }
        if (arrayList.size() == 0) {
            return null;
        }
        return arrayList;
    }

    public List<Camera.Size> reWriteFakePictureSizes() throws NumberFormatException {
        List<Camera.Size> supportedPictureSizes = this.mParameters.getSupportedPictureSizes();
        ArrayList arrayList = new ArrayList();
        int size = supportedPictureSizes.size();
        String str = SystemProperties.get("persist.sys.backcam", "0");
        String str2 = SystemProperties.get("persist.sys.frontcam", "0");
        int i = Integer.parseInt(str);
        int i2 = Integer.parseInt(str2);
        if (this.mCameraId == 0) {
            if (size >= 14 && i > 2) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(5));
                arrayList.add(supportedPictureSizes.get(6));
                arrayList.add(supportedPictureSizes.get(8));
                arrayList.add(supportedPictureSizes.get(11));
                arrayList.add(supportedPictureSizes.get(12));
                arrayList.add(supportedPictureSizes.get(13));
                return arrayList;
            }
            if (size >= 14 && i == 2) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(5));
                arrayList.add(supportedPictureSizes.get(8));
                arrayList.add(supportedPictureSizes.get(11));
                arrayList.add(supportedPictureSizes.get(12));
                return arrayList;
            }
            if (size >= 14 && i == 1) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(8));
                arrayList.add(supportedPictureSizes.get(10));
                return arrayList;
            }
        } else {
            if (size >= 13 && i2 > 2) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(5));
                arrayList.add(supportedPictureSizes.get(6));
                arrayList.add(supportedPictureSizes.get(8));
                arrayList.add(supportedPictureSizes.get(9));
                arrayList.add(supportedPictureSizes.get(11));
                arrayList.add(supportedPictureSizes.get(12));
                return arrayList;
            }
            if (size >= 13 && i2 == 2) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(5));
                arrayList.add(supportedPictureSizes.get(9));
                arrayList.add(supportedPictureSizes.get(11));
                arrayList.add(supportedPictureSizes.get(12));
                return arrayList;
            }
            if (size >= 13 && i2 == 1) {
                arrayList.add(supportedPictureSizes.get(3));
                arrayList.add(supportedPictureSizes.get(4));
                arrayList.add(supportedPictureSizes.get(9));
                arrayList.add(supportedPictureSizes.get(11));
                return arrayList;
            }
        }
        return this.mParameters.getSupportedPictureSizes();
    }
}
