package com.android.camera.bridge;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.view.SurfaceHolder;
import com.android.camera.CameraManager;
import java.util.List;

/* loaded from: classes.dex */
public class DummyCameraDevice implements ICameraDeviceExt {
    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Parameters getInitialParams() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Parameters getParameters() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public ParametersExt getParametersExt() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getCameraId() {
        return -1;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public CameraManager.CameraProxy getCameraDevice() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewDisplayAsync(SurfaceHolder surfaceHolder) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewTextureAsync(SurfaceTexture surfaceTexture) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPhotoModeParameters(boolean z) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewSize() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setErrorCallback(Camera.ErrorCallback errorCallback) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isHdrChanged() {
        return false;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isSceneModeChanged() {
        return false;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isPictureSizeChanged() {
        return false;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void applyParametersToServer() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public Camera.Size getPreviewSize() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFocusMode(String str) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isSupportFocusMode(String str) {
        return false;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void fetchParametersFromServer() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewFormat(int i) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setDisplayOrientation(boolean z) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getDisplayOrientation() {
        return 0;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getCameraDisplayOrientation() {
        return 0;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setJpegRotation(int i) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getJpegRotation() {
        return 0;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setGpsParameters(Location location) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setAutoExposureLock(boolean z) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setAutoWhiteBalanceLock(boolean z) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setFocusAreas(List<Camera.Area> list) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setMeteringAreas(List<Camera.Area> list) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setCapturePath(String str) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public boolean isZoomSupported() {
        return false;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public int getZoom() {
        return 0;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setZoom(int i) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void stopFaceDetection() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void updateDngState(String str) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public String getDngState() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void updateParameters() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public String getZsdMode() {
        return null;
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPhotoModeBasicParameters() {
    }

    @Override // com.android.camera.bridge.ICameraDeviceExt
    public void setPreviewSizeFull() {
    }
}
