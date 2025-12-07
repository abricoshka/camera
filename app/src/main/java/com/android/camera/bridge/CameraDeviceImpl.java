package com.android.camera.bridge;

import android.app.Activity;
import android.hardware.Camera;
import com.android.camera.CameraManager;
import com.mediatek.camera.platform.ICameraDeviceManager;
import com.mediatek.camera.platform.Parameters;
import com.mediatek.camera.util.Log;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class CameraDeviceImpl implements ICameraDeviceManager.ICameraDevice {
    private CameraManager.CameraProxy mCameraDevice;
    private ReentrantLock mLock = new ReentrantLock();
    private ICameraDeviceExt mMyCameraDevice;

    public CameraDeviceImpl(Activity activity, ICameraDeviceExt iCameraDeviceExt) {
        Assert.assertNotNull(activity);
        Assert.assertNotNull(iCameraDeviceExt);
        this.mMyCameraDevice = iCameraDeviceExt;
        this.mCameraDevice = iCameraDeviceExt.getCameraDevice();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public int getCameraId() {
        return this.mMyCameraDevice.getCameraId();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean setAutoRamaCallback(ICameraDeviceManager.ICameraDevice.PanoramaListener panoramaListener) {
        this.mCameraDevice.setAutoRamaCallback(panoramaListener != null ? new AutoRamaListenerImpl(panoramaListener) : null);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean setAutoRamaMoveCallback(ICameraDeviceManager.ICameraDevice.PanoramaMvListener panoramaMvListener) {
        this.mCameraDevice.setAutoRamaMoveCallback(panoramaMvListener != null ? new AutoRamaMvListenerImpl(panoramaMvListener) : null);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean setAutoFocusMoveCallback(ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback autoFocusMvCallback) {
        this.mCameraDevice.setAutoFocusMoveCallback(autoFocusMvCallback != null ? new ContinuousFocusMovingCallback(autoFocusMvCallback) : null);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean setUncompressedImageCallback(Camera.PictureCallback pictureCallback) {
        this.mCameraDevice.setUncompressedImageCallback(pictureCallback);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setAsdCallback(ICameraDeviceManager.ICameraDevice.AsdListener asdListener) {
        this.mCameraDevice.setAsdCallback(asdListener != null ? new AsdListenerImpl(asdListener) : null);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean setcFBOrignalCallback(ICameraDeviceManager.ICameraDevice.cFbOriginalCallback cfboriginalcallback) {
        this.mCameraDevice.setFbOriginalCallback(cfboriginalcallback != null ? new cFBCallback(cfboriginalcallback) : null);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setStereoDataCallback(ICameraDeviceManager.ICameraDevice.StereoDataCallback stereoDataCallback) {
        this.mCameraDevice.setStereoCameraDataCallback(stereoDataCallback != null ? new StereoCameraDataListener(stereoDataCallback) : null);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setStereoWarningCallback(ICameraDeviceManager.ICameraDevice.StereoWarningCallback stereoWarningCallback) {
        this.mCameraDevice.setStereoCameraWarningCallback(stereoWarningCallback != null ? new StereoCameraWarningListener(stereoWarningCallback) : null);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setStereoDistanceCallback(ICameraDeviceManager.ICameraDevice.StereoDistanceCallback stereoDistanceCallback) {
        this.mCameraDevice.setStereoCameraDistanceCallback(stereoDistanceCallback != null ? new StereoCameraDistanceListener(stereoDistanceCallback) : null);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean startAutoRama(int i) {
        this.mCameraDevice.startAutoRama(i);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean stopAutoRama(boolean z) {
        this.mCameraDevice.stopAutoRama(z ? 1 : 0);
        return true;
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void addCallbackBuffer(byte[] bArr) {
        this.mCameraDevice.addCallbackBuffer(bArr);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback) {
        this.mCameraDevice.setPreviewCallbackWithBuffer(previewCallback);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public String getParameter(String str) {
        return this.mMyCameraDevice.getParameters().get(str);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setParameter(String str, String str2) {
        this.mMyCameraDevice.getParametersExt().set(str, str2);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public Parameters getParameters() {
        return this.mMyCameraDevice.getParametersExt();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public List<Camera.Size> getSupportedPreviewSizes() {
        return this.mMyCameraDevice.getParametersExt().getSupportedPreviewSizes();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public Camera.Size getPreviewSize() {
        return this.mMyCameraDevice.getParameters().getPreviewSize();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setPreviewSize(int i, int i2) {
        this.mMyCameraDevice.getParametersExt().setPreviewSize(i, i2);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public List<Integer> getPIPFrameRateZSDOff() {
        return this.mMyCameraDevice.getParametersExt().getPIPFrameRateZSDOff();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public boolean isDynamicFrameRateSupported() {
        return this.mMyCameraDevice.getParametersExt().isDynamicFrameRateSupported();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setDynamicFrameRate(boolean z) {
        this.mMyCameraDevice.getParametersExt().setDynamicFrameRate(z);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void applyParameters() {
        this.mMyCameraDevice.applyParametersToServer();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void fetchParametersFromServer() {
        this.mMyCameraDevice.fetchParametersFromServer();
    }

    private class AutoRamaListenerImpl implements Camera.AutoRamaCallback {
        private ICameraDeviceManager.ICameraDevice.PanoramaListener mListener;

        AutoRamaListenerImpl(ICameraDeviceManager.ICameraDevice.PanoramaListener panoramaListener) {
            this.mListener = panoramaListener;
        }

        public void onCapture(byte[] bArr) {
            if (this.mListener != null) {
                this.mListener.onCapture(bArr);
            }
        }
    }

    private class AsdListenerImpl implements Camera.AsdCallback {
        private ICameraDeviceManager.ICameraDevice.AsdListener mListener;

        AsdListenerImpl(ICameraDeviceManager.ICameraDevice.AsdListener asdListener) {
            this.mListener = asdListener;
        }

        public void onDetected(int i) {
            Log.m31d("CameraDeviceImpl", "[onDetecte]xy:" + i);
            if (this.mListener != null) {
                this.mListener.onDeviceCallback(i);
            }
        }
    }

    private class ContinuousFocusMovingCallback implements Camera.AutoFocusMoveCallback {
        private ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback mListener;

        ContinuousFocusMovingCallback(ICameraDeviceManager.ICameraDevice.AutoFocusMvCallback autoFocusMvCallback) {
            this.mListener = autoFocusMvCallback;
        }

        @Override // android.hardware.Camera.AutoFocusMoveCallback
        public void onAutoFocusMoving(boolean z, Camera camera) {
            Log.m34i("CameraDeviceImpl", "[onAutoFocusMoving]moving = " + z);
            if (this.mListener != null) {
                this.mListener.onAutoFocusMoving(z, camera);
            }
        }
    }

    private class StereoCameraDataListener implements Camera.StereoCameraDataCallback {
        private ICameraDeviceManager.ICameraDevice.StereoDataCallback mListener;

        public StereoCameraDataListener(ICameraDeviceManager.ICameraDevice.StereoDataCallback stereoDataCallback) {
            this.mListener = stereoDataCallback;
        }

        public void onJpsCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onJpsCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onJpsCapture(bArr);
            }
        }

        public void onMaskCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onMaskCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onMaskCapture(bArr);
            }
        }

        public void onDepthMapCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onDepthMapCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onDepthMapCapture(bArr);
            }
        }

        public void onClearImageCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onClearImageCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onClearImageCapture(bArr);
            }
        }

        public void onLdcCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onLdcCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onLdcCapture(bArr);
            }
        }

        public void onN3dCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onN3dCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onN3dCapture(bArr);
            }
        }

        public void onDepthWrapperCapture(byte[] bArr) {
            Log.m31d("CameraDeviceImpl", "[onDepthWrapperCapture]data:" + bArr);
            if (this.mListener != null) {
                this.mListener.onDepthWrapperCapture(bArr);
            }
        }
    }

    private class StereoCameraWarningListener implements Camera.StereoCameraWarningCallback {
        private ICameraDeviceManager.ICameraDevice.StereoWarningCallback mListener;

        public StereoCameraWarningListener(ICameraDeviceManager.ICameraDevice.StereoWarningCallback stereoWarningCallback) {
            this.mListener = stereoWarningCallback;
        }

        public void onWarning(int i) {
            Log.m31d("CameraDeviceImpl", "[onWarning]type:" + i);
            if (this.mListener != null) {
                this.mListener.onWarning(i);
            }
        }
    }

    private class StereoCameraDistanceListener implements Camera.DistanceInfoCallback {
        private ICameraDeviceManager.ICameraDevice.StereoDistanceCallback mListener;

        public StereoCameraDistanceListener(ICameraDeviceManager.ICameraDevice.StereoDistanceCallback stereoDistanceCallback) {
            this.mListener = stereoDistanceCallback;
        }

        public void onInfo(String str) {
            Log.m31d("CameraDeviceImpl", "[onInfo]info:" + str);
            if (this.mListener != null) {
                this.mListener.onInfo(str);
            }
        }
    }

    private class AutoRamaMvListenerImpl implements Camera.AutoRamaMoveCallback {
        private ICameraDeviceManager.ICameraDevice.PanoramaMvListener mListener;

        AutoRamaMvListenerImpl(ICameraDeviceManager.ICameraDevice.PanoramaMvListener panoramaMvListener) {
            this.mListener = panoramaMvListener;
        }

        public void onFrame(int i, int i2) {
            Log.m31d("CameraDeviceImpl", "[onFrame]xy:" + i + ",direction:" + i2);
            if (this.mListener != null) {
                this.mListener.onFrame(i, i2);
            }
        }
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public Camera getCamera() {
        return this.mCameraDevice.getCamera().getInstance();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void lock() {
        this.mCameraDevice.lock();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void unlock() {
        this.mCameraDevice.unlock();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void startPreview() {
        this.mCameraDevice.startPreviewAsync();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void stopPreview() {
        this.mCameraDevice.stopPreview();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        this.mCameraDevice.autoFocus(autoFocusCallback);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3) {
        this.mCameraDevice.takePicture(shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void takePictureAsync(Camera.ShutterCallback shutterCallback, Camera.PictureCallback pictureCallback, Camera.PictureCallback pictureCallback2, Camera.PictureCallback pictureCallback3) {
        this.mCameraDevice.takePictureAsync(shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void cancelAutoFocus() {
        this.mCameraDevice.cancelAutoFocus();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setContinuousShotSpeed(int i) {
        this.mCameraDevice.setContinuousShotSpeed(i);
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void cancelContinuousShot() {
        this.mCameraDevice.cancelContinuousShot();
    }

    @Override // com.mediatek.camera.platform.ICameraDeviceManager.ICameraDevice
    public void setContinuousShotCallback(ICameraDeviceManager.ICameraDevice.ContinuousShotListener continuousShotListener) {
        this.mCameraDevice.setContinuousShotCallback(continuousShotListener != null ? new ContinuousShotListenerImpl(continuousShotListener) : null);
    }

    private class ContinuousShotListenerImpl implements Camera.ContinuousShotCallback {
        private ICameraDeviceManager.ICameraDevice.ContinuousShotListener mCallback;

        public ContinuousShotListenerImpl(ICameraDeviceManager.ICameraDevice.ContinuousShotListener continuousShotListener) {
            this.mCallback = continuousShotListener;
        }

        public void onConinuousShotDone(int i) {
            if (this.mCallback != null) {
                this.mCallback.onConinuousShotDone(i);
            }
        }
    }

    private class cFBCallback implements Camera.FbOriginalCallback {
        ICameraDeviceManager.ICameraDevice.cFbOriginalCallback mFbOriginalCallback;

        public cFBCallback(ICameraDeviceManager.ICameraDevice.cFbOriginalCallback cfboriginalcallback) {
            this.mFbOriginalCallback = cfboriginalcallback;
        }

        public void onCapture(byte[] bArr) {
            if (this.mFbOriginalCallback != null) {
                this.mFbOriginalCallback.onOriginalCallback(bArr);
            }
        }
    }
}
