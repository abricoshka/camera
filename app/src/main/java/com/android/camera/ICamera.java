package com.android.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import java.io.IOException;

/* loaded from: classes.dex */
public interface ICamera {
    void addCallbackBuffer(byte[] bArr);

    void autoFocus(Camera.AutoFocusCallback autoFocusCallback);

    void cancelAutoFocus();

    void cancelContinuousShot();

    Camera getInstance();

    Camera.Parameters getParameters();

    void lock();

    void reconnect() throws IOException;

    void release();

    void setAsdCallback(Camera.AsdCallback asdCallback);

    void setAutoFocusMoveCallback(Camera.AutoFocusMoveCallback autoFocusMoveCallback);

    void setAutoRamaCallback(Camera.AutoRamaCallback autoRamaCallback);

    void setAutoRamaMoveCallback(Camera.AutoRamaMoveCallback autoRamaMoveCallback);

    void setContinuousShotCallback(Camera.ContinuousShotCallback continuousShotCallback);

    void setContinuousShotSpeed(int i);

    void setDataCallback(Camera.StereoCameraDataCallback stereoCameraDataCallback);

    void setDisplayOrientation(int i);

    void setDistanceInfoCallback(Camera.DistanceInfoCallback distanceInfoCallback);

    void setErrorCallback(Camera.ErrorCallback errorCallback);

    void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener);

    void setFbOriginalCallback(Camera.FbOriginalCallback fbOriginalCallback);

    void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback);

    void setParameters(Camera.Parameters parameters);

    void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback);

    void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException;

    void setPreviewTexture(SurfaceTexture surfaceTexture) throws IOException;

    void setUncompressedImageCallback(Camera.PictureCallback pictureCallback);

    void setWarningCallback(Camera.StereoCameraWarningCallback stereoCameraWarningCallback);

    void setZoomChangeListener(Camera.OnZoomChangeListener onZoomChangeListener);

    void startAutoRama(int i);

    void startFaceDetection();

    void startPreview();

    void startSmoothZoom(int i);

    void stopAutoRama(int i);

    void stopFaceDetection();

    void stopPreview();

    void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3);

    void unlock();
}
