package com.mediatek.camera.platform;

import android.hardware.Camera;
import java.util.List;

/* loaded from: classes.dex */
public interface Parameters {
    void enableRecordingSound(String str);

    String get(String str);

    String getAntibanding();

    String getBrightnessMode();

    String getCaptureMode();

    String getColorEffect();

    String getContrastMode();

    String getDepthAFMode();

    String getDistanceMode();

    String getEdgeMode();

    int getExposureCompensation();

    float getExposureCompensationStep();

    String getFlashMode();

    String getFocusMode();

    String getHueMode();

    String getISOSpeed();

    int getJpegQuality();

    int getMaxExposureCompensation();

    int getMaxNumFocusAreas();

    int getMinExposureCompensation();

    List<Integer> getPIPFrameRateZSDOff();

    List<Integer> getPIPFrameRateZSDOn();

    Camera.Size getPictureSize();

    Camera.Size getPreferredPreviewSizeForVideo();

    int getPreviewFormat();

    Camera.Size getPreviewSize();

    String getSaturationMode();

    String getSceneMode();

    List<String> getSupportedAntibanding();

    List<String> getSupportedBrightnessMode();

    List<String> getSupportedCaptureMode();

    List<String> getSupportedColorEffects();

    List<String> getSupportedContrastMode();

    List<String> getSupportedEdgeMode();

    List<String> getSupportedFlashModes();

    List<String> getSupportedFocusModes();

    List<String> getSupportedHueMode();

    List<String> getSupportedISOSpeed();

    List<Camera.Size> getSupportedPictureSizes();

    List<Integer> getSupportedPreviewFrameRates();

    List<Camera.Size> getSupportedPreviewSizes();

    List<String> getSupportedSaturationMode();

    List<String> getSupportedSceneModes();

    List<Camera.Size> getSupportedVideoSizes();

    List<String> getSupportedWhiteBalance();

    List<String> getSupportedZSDMode();

    String getWhiteBalance();

    String getZSDMode();

    boolean isDynamicFrameRateSupported();

    boolean isVideoSnapshotSupported();

    boolean isVideoStabilizationSupported();

    void set(String str, int i);

    void set(String str, String str2);

    void setAntibanding(String str);

    void setBrightnessMode(String str);

    void setBurstShotNum(int i);

    void setCameraMode(int i);

    void setCaptureMode(String str);

    void setColorEffect(String str);

    void setContrastMode(String str);

    void setDepthAFMode(boolean z);

    void setDistanceMode(boolean z);

    void setDynamicFrameRate(boolean z);

    void setEdgeMode(String str);

    void setExposureCompensation(int i);

    void setFlashMode(String str);

    void setFocusMode(String str);

    void setHueMode(String str);

    void setISOSpeed(String str);

    void setJpegQuality(int i);

    void setPictureSize(int i, int i2);

    void setPreviewFormat(int i);

    void setPreviewFrameRate(int i);

    void setPreviewSize(int i, int i2);

    void setRecordingHint(boolean z);

    void setRefocusJpsFileName(String str);

    void setSaturationMode(String str);

    void setSceneMode(String str);

    void setVideoStabilization(boolean z);

    void setWhiteBalance(String str);

    void setZSDMode(String str);
}
