package com.android.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import com.mediatek.camera.util.ReflectUtil;
import java.io.IOException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class AndroidCamera implements ICamera {
    protected Camera mCamera;
    Method mSetUncompressedImageCallbackMethod;

    public AndroidCamera(Camera camera) {
        this.mCamera = null;
        Util.assertError(camera != null);
        this.mCamera = camera;
        this.mSetUncompressedImageCallbackMethod = ReflectUtil.getMethod(Camera.class, "setUncompressedImageCallback", Camera.PictureCallback.class);
    }

    @Override // com.android.camera.ICamera
    public Camera getInstance() {
        return this.mCamera;
    }

    @Override // com.android.camera.ICamera
    public void addCallbackBuffer(byte[] bArr) {
        this.mCamera.addCallbackBuffer(bArr);
    }

    @Override // com.android.camera.ICamera
    public void autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        this.mCamera.autoFocus(autoFocusCallback);
    }

    @Override // com.android.camera.ICamera
    public void cancelAutoFocus() {
        this.mCamera.cancelAutoFocus();
    }

    @Override // com.android.camera.ICamera
    public void cancelContinuousShot() {
        this.mCamera.cancelContinuousShot();
    }

    @Override // com.android.camera.ICamera
    public void lock() {
        this.mCamera.lock();
    }

    @Override // com.android.camera.ICamera
    public Camera.Parameters getParameters() {
        return this.mCamera.getParameters();
    }

    @Override // com.android.camera.ICamera
    public void release() {
        this.mCamera.release();
    }

    @Override // com.android.camera.ICamera
    public void reconnect() throws IOException {
        this.mCamera.reconnect();
    }

    @Override // com.android.camera.ICamera
    public void setAsdCallback(Camera.AsdCallback asdCallback) {
        this.mCamera.setAsdCallback(asdCallback);
    }

    @Override // com.android.camera.ICamera
    public void setAutoFocusMoveCallback(Camera.AutoFocusMoveCallback autoFocusMoveCallback) {
        this.mCamera.setAutoFocusMoveCallback(autoFocusMoveCallback);
    }

    @Override // com.android.camera.ICamera
    public void setUncompressedImageCallback(Camera.PictureCallback pictureCallback) {
        if (this.mSetUncompressedImageCallbackMethod != null) {
            ReflectUtil.callMethodOnObject(this.mCamera, this.mSetUncompressedImageCallbackMethod, pictureCallback);
        }
    }

    @Override // com.android.camera.ICamera
    public void setAutoRamaCallback(Camera.AutoRamaCallback autoRamaCallback) {
        this.mCamera.setAutoRamaCallback(autoRamaCallback);
    }

    @Override // com.android.camera.ICamera
    public void setAutoRamaMoveCallback(Camera.AutoRamaMoveCallback autoRamaMoveCallback) {
        this.mCamera.setAutoRamaMoveCallback(autoRamaMoveCallback);
    }

    @Override // com.android.camera.ICamera
    public void setFbOriginalCallback(Camera.FbOriginalCallback fbOriginalCallback) {
        this.mCamera.setFbOriginalCallback(fbOriginalCallback);
    }

    @Override // com.android.camera.ICamera
    public void setContinuousShotCallback(Camera.ContinuousShotCallback continuousShotCallback) {
        this.mCamera.setContinuousShotCallback(continuousShotCallback);
    }

    @Override // com.android.camera.ICamera
    public void setContinuousShotSpeed(int i) {
        this.mCamera.setContinuousShotSpeed(i);
    }

    @Override // com.android.camera.ICamera
    public void setDisplayOrientation(int i) {
        this.mCamera.setDisplayOrientation(i);
    }

    @Override // com.android.camera.ICamera
    public void setErrorCallback(Camera.ErrorCallback errorCallback) {
        this.mCamera.setErrorCallback(errorCallback);
    }

    @Override // com.android.camera.ICamera
    public void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener) {
        this.mCamera.setFaceDetectionListener(faceDetectionListener);
    }

    @Override // com.android.camera.ICamera
    public void setParameters(Camera.Parameters parameters) {
        this.mCamera.setParameters(parameters);
    }

    @Override // com.android.camera.ICamera
    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback) {
        this.mCamera.setPreviewCallbackWithBuffer(previewCallback);
    }

    @Override // com.android.camera.ICamera
    public void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException {
        this.mCamera.setPreviewTexture(surfaceTexture);
    }

    @Override // com.android.camera.ICamera
    public void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException {
        this.mCamera.setPreviewDisplay(surfaceHolder);
    }

    @Override // com.android.camera.ICamera
    public void setDataCallback(Camera.StereoCameraDataCallback stereoCameraDataCallback) {
        this.mCamera.setStereoCameraDataCallback(stereoCameraDataCallback);
    }

    @Override // com.android.camera.ICamera
    public void setWarningCallback(Camera.StereoCameraWarningCallback stereoCameraWarningCallback) {
        this.mCamera.setStereoCameraWarningCallback(stereoCameraWarningCallback);
    }

    @Override // com.android.camera.ICamera
    public void setDistanceInfoCallback(Camera.DistanceInfoCallback distanceInfoCallback) {
        this.mCamera.setDistanceInfoCallback(distanceInfoCallback);
    }

    @Override // com.android.camera.ICamera
    public void setZoomChangeListener(Camera.OnZoomChangeListener onZoomChangeListener) {
        this.mCamera.setZoomChangeListener(onZoomChangeListener);
    }

    @Override // com.android.camera.ICamera
    public void startAutoRama(int i) {
        this.mCamera.startAutoRama(i);
    }

    @Override // com.android.camera.ICamera
    public void startFaceDetection() {
        this.mCamera.startFaceDetection();
    }

    @Override // com.android.camera.ICamera
    public void startPreview() {
        this.mCamera.startPreview();
    }

    @Override // com.android.camera.ICamera
    public void startSmoothZoom(int i) {
        this.mCamera.startSmoothZoom(i);
    }

    @Override // com.android.camera.ICamera
    public void stopAutoRama(int i) {
        this.mCamera.stopAutoRama(i);
    }

    @Override // com.android.camera.ICamera
    public void stopFaceDetection() {
        this.mCamera.stopFaceDetection();
    }

    @Override // com.android.camera.ICamera
    public void stopPreview() {
        this.mCamera.stopPreview();
    }

    @Override // com.android.camera.ICamera
    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3) {
        this.mCamera.takePicture(shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
    }

    @Override // com.android.camera.ICamera
    public void unlock() {
        this.mCamera.unlock();
    }

    @Override // com.android.camera.ICamera
    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.mCamera.setOneShotPreviewCallback(previewCallback);
    }
}
