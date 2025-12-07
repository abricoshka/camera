package com.mediatek.camera.platform;

import android.hardware.Camera;
import java.util.List;

/* loaded from: classes.dex */
public interface ICameraDeviceManager {

    public interface ICameraDevice {

        public interface AsdListener {
            void onDeviceCallback(int i);
        }

        public interface AutoFocusMvCallback {
            void onAutoFocusMoving(boolean z, Camera camera);
        }

        public interface ContinuousShotListener {
            void onConinuousShotDone(int i);
        }

        public interface PanoramaListener {
            void onCapture(byte[] bArr);
        }

        public interface PanoramaMvListener {
            void onFrame(int i, int i2);
        }

        public interface StereoDataCallback {
            void onClearImageCapture(byte[] bArr);

            void onDepthMapCapture(byte[] bArr);

            void onDepthWrapperCapture(byte[] bArr);

            void onJpsCapture(byte[] bArr);

            void onLdcCapture(byte[] bArr);

            void onMaskCapture(byte[] bArr);

            void onN3dCapture(byte[] bArr);
        }

        public interface StereoDistanceCallback {
            void onInfo(String str);
        }

        public interface StereoWarningCallback {
            void onWarning(int i);
        }

        public interface cFbOriginalCallback {
            void onOriginalCallback(byte[] bArr);
        }

        void addCallbackBuffer(byte[] bArr);

        void applyParameters();

        void autoFocus(Camera.AutoFocusCallback autoFocusCallback);

        void cancelAutoFocus();

        void cancelContinuousShot();

        void fetchParametersFromServer();

        Camera getCamera();

        int getCameraId();

        List<Integer> getPIPFrameRateZSDOff();

        String getParameter(String str);

        Parameters getParameters();

        Camera.Size getPreviewSize();

        List<Camera.Size> getSupportedPreviewSizes();

        boolean isDynamicFrameRateSupported();

        void lock();

        void setAsdCallback(AsdListener asdListener);

        boolean setAutoFocusMoveCallback(AutoFocusMvCallback autoFocusMvCallback);

        boolean setAutoRamaCallback(PanoramaListener panoramaListener);

        boolean setAutoRamaMoveCallback(PanoramaMvListener panoramaMvListener);

        void setContinuousShotCallback(ContinuousShotListener continuousShotListener);

        void setContinuousShotSpeed(int i);

        void setDynamicFrameRate(boolean z);

        void setParameter(String str, String str2);

        void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback);

        void setPreviewSize(int i, int i2);

        void setStereoDataCallback(StereoDataCallback stereoDataCallback);

        void setStereoDistanceCallback(StereoDistanceCallback stereoDistanceCallback);

        void setStereoWarningCallback(StereoWarningCallback stereoWarningCallback);

        boolean setUncompressedImageCallback(Camera.PictureCallback pictureCallback);

        boolean setcFBOrignalCallback(cFbOriginalCallback cfboriginalcallback);

        boolean startAutoRama(int i);

        void startPreview();

        boolean stopAutoRama(boolean z);

        void stopPreview();

        void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3);

        void takePictureAsync(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3);

        void unlock();
    }

    int getBackCameraId();

    ICameraDevice getCameraDevice(int i);

    Camera.CameraInfo getCameraInfo(int i);

    Camera.CameraInfo[] getCameraInfo();

    int getCurrentCameraId();

    int getFrontCameraId();

    int getNumberOfCameras();

    void onCameraCloseDone();
}
