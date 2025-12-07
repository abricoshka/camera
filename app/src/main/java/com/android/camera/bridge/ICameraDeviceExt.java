package com.android.camera.bridge;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.view.SurfaceHolder;
import com.android.camera.CameraManager;
import java.util.List;

/* loaded from: classes.dex */
public interface ICameraDeviceExt {
    void applyParametersToServer();

    void fetchParametersFromServer();

    CameraManager.CameraProxy getCameraDevice();

    int getCameraDisplayOrientation();

    int getCameraId();

    int getDisplayOrientation();

    String getDngState();

    Camera.Parameters getInitialParams();

    int getJpegRotation();

    Camera.Parameters getParameters();

    ParametersExt getParametersExt();

    Camera.Size getPreviewSize();

    int getZoom();

    String getZsdMode();

    boolean isHdrChanged();

    boolean isPictureSizeChanged();

    boolean isSceneModeChanged();

    boolean isSupportFocusMode(String str);

    boolean isZoomSupported();

    void setAutoExposureLock(boolean z);

    void setAutoWhiteBalanceLock(boolean z);

    void setCapturePath(String str);

    void setDisplayOrientation(boolean z);

    void setErrorCallback(Camera.ErrorCallback errorCallback);

    void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener);

    void setFocusAreas(List<Camera.Area> list);

    void setFocusMode(String str);

    void setGpsParameters(Location location);

    void setJpegRotation(int i);

    void setMeteringAreas(List<Camera.Area> list);

    void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback);

    void setPhotoModeBasicParameters();

    void setPhotoModeParameters(boolean z);

    void setPreviewDisplayAsync(SurfaceHolder surfaceHolder);

    void setPreviewFormat(int i);

    void setPreviewSize();

    void setPreviewSizeFull();

    void setPreviewTextureAsync(SurfaceTexture surfaceTexture);

    void setZoom(int i);

    void stopFaceDetection();

    void updateDngState(String str);

    void updateParameters();
}
