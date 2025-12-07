package com.android.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.p000v8.renderscript.ScriptIntrinsicBLAS;
import android.support.v4.app.FrameMetricsAggregator;
import android.view.SurfaceHolder;
import com.mediatek.camera.util.CameraPerformanceTracker;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class CameraManager {
    private ICamera mCamera;
    private Handler mCameraHandler;
    private CameraProxy mCameraProxy;
    private ErrorCallbackWrapper mErrorCallbackWrapper;
    private Camera.Parameters mParameters;
    private boolean mParametersIsDirty;
    private Camera.Parameters mParamsToSet;
    private IOException mReconnectException;
    private String mSubTag = "CameraManager";
    private boolean mFaceDetectionRunning = false;
    private boolean mIsCameraInError = false;

    public CameraManager(String str) {
        Log.m5d("CameraManager", "[CameraManager]constructor,subTag = " + str);
        this.mSubTag += "/" + str;
        HandlerThread handlerThread = new HandlerThread("Camera Handler" + str + " Thread");
        handlerThread.start();
        this.mCameraHandler = new CameraHandler(handlerThread.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMsgLabel(int i) {
        switch (i) {
            case 1:
                return "[release] ";
            case 2:
                return "[reconnect] ";
            case 3:
                return "[unlock] ";
            case 4:
                return "[lock] ";
            case 5:
                return "[setPreviewTexture] ";
            case 6:
                return "[startPreviewAsync] ";
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                return "[stopPreview] ";
            case 8:
                return "[setPreviewCallbackWithBuffer] ";
            case 9:
                return "[addCallbackBuffer] ";
            case 10:
                return "[autoFocus] ";
            case 11:
                return "[cancelAutoFocus] ";
            case 12:
                return "[setAutoFocusMoveCallback] ";
            case 13:
                return "[setDisplayOrientation] ";
            case 14:
                return "[setZoomChangeListener] ";
            case 15:
                return "[setFaceDetectionListener] ";
            case 16:
                return "[startFaceDetection] ";
            case 17:
                return "[stopFaceDetection] ";
            case 18:
                return "[setErrorCallback] ";
            case 19:
                return "[setParameters] ";
            case 20:
                return "[getParameters] ";
            case 21:
                return "[setParametersAsync] ";
            case 24:
                return "[setFbOriginalCallback] ";
            case 100:
                return "[startSmoothZoom] ";
            case 101:
                return "[setAutoRamaCallback] ";
            case 102:
                return "[setAutoramraMVCallback] ";
            case 103:
                return "[startAutoRama] ";
            case 104:
                return "[stopAutoRama] ";
            case 108:
                return "[setAsdCallback] ";
            case ScriptIntrinsicBLAS.TRANSPOSE /* 112 */:
                return "[cancelContinuousShot] ";
            case ScriptIntrinsicBLAS.CONJ_TRANSPOSE /* 113 */:
                return "[setContinuousShotSpeed] ";
            case 115:
                return "[setContinuousShotCallback] ";
            case 117:
                return "[setStereo3DMode] ";
            case ScriptIntrinsicBLAS.LOWER /* 122 */:
                return "[setContinousShotState] ";
            case 124:
                return "[setPreviewSurfaceHolderAsync] ";
            case ScriptIntrinsicBLAS.UNIT /* 132 */:
                return "[addRawCallback] ";
            default:
                return "unknown message msg id = " + i;
        }
    }

    private class CameraHandler extends Handler {
        private Camera.ErrorCallback mErrorCallback;

        CameraHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (CameraManager.this.mCamera == null || CameraManager.this.mIsCameraInError) {
                Log.m6e(CameraManager.this.mSubTag, "[handleMessage] with abnormal: device = " + CameraManager.this.mCamera + ", error = " + CameraManager.this.mIsCameraInError);
                return;
            }
            if (9 != message.what) {
                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " + pending time = " + (jUptimeMillis - message.getWhen()) + "ms.");
            }
            try {
                try {
                    switch (message.what) {
                        case 1:
                            CameraPerformanceTracker.onEvent("CameraManager", "Release", true);
                            CameraManager.this.mCamera.release();
                            CameraPerformanceTracker.onEvent("CameraManager", "Release", false);
                            CameraManager.this.mCamera = null;
                            CameraManager.this.mCameraProxy = null;
                            CameraManager.this.mFaceDetectionRunning = false;
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 2:
                            CameraManager.this.mReconnectException = null;
                            try {
                                CameraManager.this.mCamera.reconnect();
                            } catch (IOException e) {
                                CameraManager.this.mReconnectException = e;
                            }
                            CameraManager.this.mFaceDetectionRunning = false;
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 3:
                            CameraManager.this.mCamera.unlock();
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 4:
                            CameraManager.this.mCamera.lock();
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 5:
                            try {
                                CameraManager.this.mCamera.setPreviewTexture((SurfaceTexture) message.obj);
                                if (9 != message.what) {
                                    Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                    return;
                                }
                                return;
                            } catch (IOException e2) {
                                Log.m6e(CameraManager.this.mSubTag, "[handleMessage] IOException. ");
                                throw new RuntimeException(e2);
                            }
                        case 6:
                            CameraPerformanceTracker.onEvent("CameraManager", "StartPreview", true);
                            CameraManager.this.mCamera.startPreview();
                            CameraPerformanceTracker.onEvent("CameraManager", "StartPreview", false);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                            CameraPerformanceTracker.onEvent("CameraManager", "StopPreview", true);
                            if (CameraManager.this.mFaceDetectionRunning) {
                                CameraManager.this.mCamera.stopFaceDetection();
                                CameraManager.this.mFaceDetectionRunning = false;
                                Log.m11w("CameraManager", "Please call stopFaceDetecton firstly before stop preview!");
                            }
                            CameraManager.this.mCamera.stopPreview();
                            CameraPerformanceTracker.onEvent("CameraManager", "StopPreview", false);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 8:
                            CameraManager.this.mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 9:
                            CameraManager.this.mCamera.addCallbackBuffer((byte[]) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 10:
                            CameraManager.this.mCamera.autoFocus((Camera.AutoFocusCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 11:
                            CameraManager.this.mCamera.cancelAutoFocus();
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 12:
                            CameraManager.this.mCamera.setAutoFocusMoveCallback((Camera.AutoFocusMoveCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 13:
                            CameraManager.this.mCamera.setDisplayOrientation(message.arg1);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 14:
                            CameraManager.this.mCamera.setZoomChangeListener((Camera.OnZoomChangeListener) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 15:
                            CameraManager.this.mCamera.setFaceDetectionListener((Camera.FaceDetectionListener) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 16:
                            if (CameraManager.this.mFaceDetectionRunning) {
                                Log.m11w("CameraManager", "face detection is running, don't need to start it again!");
                            } else {
                                CameraManager.this.mCamera.startFaceDetection();
                                CameraManager.this.mFaceDetectionRunning = true;
                            }
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 17:
                            if (CameraManager.this.mFaceDetectionRunning) {
                                CameraManager.this.mCamera.stopFaceDetection();
                                CameraManager.this.mFaceDetectionRunning = false;
                            } else {
                                Log.m11w("CameraManager", "face detection is stopped, don't need to stop it again!");
                            }
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 18:
                            this.mErrorCallback = (Camera.ErrorCallback) message.obj;
                            CameraManager.this.mCamera.setErrorCallback((Camera.ErrorCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 19:
                            CameraPerformanceTracker.onEvent("CameraManager", "setParameters", true);
                            CameraManager.this.mParametersIsDirty = true;
                            CameraManager.this.mParamsToSet.unflatten((String) message.obj);
                            CameraManager.this.mCamera.setParameters(CameraManager.this.mParamsToSet);
                            CameraPerformanceTracker.onEvent("CameraManager", "setParameters", false);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 20:
                            CameraPerformanceTracker.onEvent("CameraManager", "getParameters", true);
                            if (CameraManager.this.mParametersIsDirty) {
                                CameraManager.this.mParameters = CameraManager.this.mCamera.getParameters();
                                CameraManager.this.mParametersIsDirty = false;
                            }
                            CameraPerformanceTracker.onEvent("CameraManager", "getParameters", false);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 21:
                            CameraPerformanceTracker.onEvent("CameraManager", "setParameters", true);
                            CameraManager.this.mParametersIsDirty = true;
                            CameraManager.this.mParamsToSet.unflatten((String) message.obj);
                            CameraManager.this.mCamera.setParameters(CameraManager.this.mParamsToSet);
                            CameraPerformanceTracker.onEvent("CameraManager", "setParameters", false);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 24:
                            CameraManager.this.mCamera.setFbOriginalCallback((Camera.FbOriginalCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 28:
                            CameraManager.this.mCamera.setUncompressedImageCallback((Camera.PictureCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 100:
                            CameraManager.this.mCamera.startSmoothZoom(message.arg1);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 101:
                            CameraManager.this.mCamera.setAutoRamaCallback((Camera.AutoRamaCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 102:
                            CameraManager.this.mCamera.setAutoRamaMoveCallback((Camera.AutoRamaMoveCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 103:
                            CameraManager.this.mCamera.startAutoRama(message.arg1);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 104:
                            CameraManager.this.mCamera.stopAutoRama(message.arg1);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 108:
                            CameraManager.this.mCamera.setAsdCallback((Camera.AsdCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case ScriptIntrinsicBLAS.TRANSPOSE /* 112 */:
                            CameraManager.this.mCamera.cancelContinuousShot();
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case ScriptIntrinsicBLAS.CONJ_TRANSPOSE /* 113 */:
                            CameraManager.this.mCamera.setContinuousShotSpeed(message.arg1);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 115:
                            CameraManager.this.mCamera.setContinuousShotCallback((Camera.ContinuousShotCallback) message.obj);
                            break;
                        case 124:
                            try {
                                CameraManager.this.mCamera.setPreviewDisplay((SurfaceHolder) message.obj);
                                break;
                            } catch (IOException e3) {
                                throw new RuntimeException(e3);
                            }
                        case 125:
                            CameraManager.this.mCamera.setDataCallback((Camera.StereoCameraDataCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 126:
                            CameraManager.this.mCamera.setWarningCallback((Camera.StereoCameraWarningCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 128:
                            CameraManager.this.mCamera.setDistanceInfoCallback((Camera.DistanceInfoCallback) message.obj);
                            if (9 != message.what) {
                                Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                                return;
                            }
                            return;
                        case 129:
                            CameraManager.this.mCamera.setOneShotPreviewCallback((Camera.PreviewCallback) message.obj);
                            break;
                        default:
                            throw new RuntimeException("Invalid CameraProxy message=" + message.what);
                    }
                    if (9 != message.what) {
                        Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                    }
                } catch (RuntimeException e4) {
                    if (message.what != 1 && CameraManager.this.mCamera != null) {
                        try {
                            Log.m6e("CameraManager", "[handleMessgae]release the camera.");
                            CameraManager.this.mCamera.release();
                        } catch (Exception e5) {
                            Log.m6e("CameraManager", "Fail to release the camera.");
                        }
                        CameraManager.this.mCamera = null;
                        CameraManager.this.mCameraProxy = null;
                    }
                    if (!CameraManager.this.mIsCameraInError && this.mErrorCallback != null) {
                        this.mErrorCallback.onError(1, null);
                    }
                    if (9 != message.what) {
                        Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                    }
                }
            } catch (Throwable th) {
                if (9 != message.what) {
                    Log.m8i(CameraManager.this.mSubTag, "[handleMessage]msg.what = " + CameraManager.getMsgLabel(message.what) + " - run time = " + (SystemClock.uptimeMillis() - jUptimeMillis) + "ms.");
                }
                throw th;
            }
        }
    }

    public static ICamera openCamera(int i) {
        Camera cameraOpenLegacy = Camera.openLegacy(i, 256);
        if (cameraOpenLegacy == null) {
            Log.m6e("CameraManager", "openCamera:got null hardware camera!");
            return null;
        }
        return new AndroidCamera(cameraOpenLegacy);
    }

    CameraProxy cameraOpen(int i) {
        CameraProxy cameraProxy = null;
        this.mIsCameraInError = false;
        CameraPerformanceTracker.onEvent("CameraManager", "Open", true);
        this.mCamera = openCamera(i);
        Log.m5d(this.mSubTag, "openCamera cameraId = " + i + " camera device = " + this.mCamera);
        CameraPerformanceTracker.onEvent("CameraManager", "Open", false);
        if (this.mCamera == null) {
            return null;
        }
        this.mParametersIsDirty = true;
        if (this.mParamsToSet == null) {
            this.mParamsToSet = this.mCamera.getParameters();
        }
        this.mCameraProxy = new CameraProxy(this, cameraProxy);
        return this.mCameraProxy;
    }

    public class CameraProxy {
        private Runnable mAsyncRunnable;
        private ReentrantLock mLock;

        /* synthetic */ CameraProxy(CameraManager cameraManager, CameraProxy cameraProxy) {
            this();
        }

        private CameraProxy() {
            this.mLock = new ReentrantLock();
            Util.assertError(CameraManager.this.mCamera != null);
        }

        public ICamera getCamera() {
            return CameraManager.this.mCamera;
        }

        public void release() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(1);
            CameraManager.this.waitDone();
        }

        public void releaseAsync() {
            Log.m5d("CameraManager", "releaseAsync");
            Message messageObtain = Message.obtain();
            messageObtain.what = 1;
            CameraManager.this.mCameraHandler.sendMessageAtFrontOfQueue(messageObtain);
        }

        public void reconnect() throws IOException {
            CameraManager.this.mCameraHandler.sendEmptyMessage(2);
            CameraManager.this.waitDone();
            if (CameraManager.this.mReconnectException != null) {
                throw CameraManager.this.mReconnectException;
            }
        }

        public void unlock() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(3);
            CameraManager.this.waitDone();
        }

        public void lock() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(4);
            CameraManager.this.waitDone();
        }

        public void setPreviewTextureAsync(SurfaceTexture surfaceTexture) {
            CameraManager.this.mCameraHandler.obtainMessage(5, surfaceTexture).sendToTarget();
        }

        public void startPreviewAsync() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(6);
            CameraManager.this.waitDone();
        }

        public void stopPreview() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(7);
            CameraManager.this.waitDone();
        }

        public void setPreviewCallbackWithBuffer(Camera.PreviewCallback previewCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(8, previewCallback).sendToTarget();
        }

        public void addCallbackBuffer(byte[] bArr) {
            CameraManager.this.mCameraHandler.obtainMessage(9, bArr).sendToTarget();
        }

        public void autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(10, autoFocusCallback).sendToTarget();
        }

        public void cancelAutoFocus() {
            CameraManager.this.mCameraHandler.removeMessages(10);
            CameraManager.this.mCameraHandler.sendEmptyMessage(11);
        }

        public void setAutoFocusMoveCallback(Camera.AutoFocusMoveCallback autoFocusMoveCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(12, autoFocusMoveCallback).sendToTarget();
        }

        public void setUncompressedImageCallback(Camera.PictureCallback pictureCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(28, pictureCallback).sendToTarget();
        }

        public void takePictureAsync(final Camera.ShutterCallback shutterCallback, final Camera.PictureCallback pictureCallback, final Camera.PictureCallback pictureCallback2, final Camera.PictureCallback pictureCallback3) {
            CameraManager.this.mCameraHandler.post(new Runnable() { // from class: com.android.camera.CameraManager.CameraProxy.1
                @Override // java.lang.Runnable
                public void run() {
                    Log.m5d(CameraManager.this.mSubTag, " takePictureAsync begin, mIsCameraInError:" + CameraManager.this.mIsCameraInError);
                    if (CameraManager.this.mCamera != null && (!CameraManager.this.mIsCameraInError)) {
                        CameraManager.this.mCamera.takePicture(shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
                        CameraManager.this.mFaceDetectionRunning = false;
                    }
                }
            });
        }

        public void takePicture(final Camera.ShutterCallback shutterCallback, final Camera.PictureCallback pictureCallback, final Camera.PictureCallback pictureCallback2, final Camera.PictureCallback pictureCallback3) {
            CameraManager.this.mCameraHandler.post(new Runnable() { // from class: com.android.camera.CameraManager.CameraProxy.2
                @Override // java.lang.Runnable
                public void run() {
                    Log.m5d(CameraManager.this.mSubTag, " takePicture begin, mIsCameraInError:" + CameraManager.this.mIsCameraInError);
                    if (CameraManager.this.mCamera != null && (!CameraManager.this.mIsCameraInError)) {
                        CameraManager.this.mCamera.takePicture(shutterCallback, pictureCallback, pictureCallback2, pictureCallback3);
                        CameraManager.this.mFaceDetectionRunning = false;
                    }
                }
            });
            CameraManager.this.waitDone();
        }

        public void setDisplayOrientation(int i) {
            CameraManager.this.mCameraHandler.obtainMessage(13, i, 0).sendToTarget();
        }

        public void setFaceDetectionListener(Camera.FaceDetectionListener faceDetectionListener) {
            CameraManager.this.mCameraHandler.obtainMessage(15, faceDetectionListener).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void startFaceDetection() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(16);
        }

        public void stopFaceDetection() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(17);
        }

        public void setErrorCallback(Camera.ErrorCallback errorCallback) {
            CameraManager.this.mErrorCallbackWrapper = errorCallback != null ? CameraManager.this.new ErrorCallbackWrapper(errorCallback) : null;
            CameraManager.this.mCameraHandler.obtainMessage(18, CameraManager.this.mErrorCallbackWrapper).sendToTarget();
        }

        public void setParameters(Camera.Parameters parameters) {
            if (parameters == null) {
                Log.m10v("CameraManager", "null parameters in setParameters()");
            } else {
                CameraManager.this.mCameraHandler.obtainMessage(19, parameters.flatten()).sendToTarget();
            }
        }

        public void setParametersAsync(final Camera.Parameters parameters, final int i) {
            synchronized (this) {
                if (this.mAsyncRunnable != null) {
                    CameraManager.this.mCameraHandler.removeCallbacks(this.mAsyncRunnable);
                }
                this.mAsyncRunnable = new Runnable() { // from class: com.android.camera.CameraManager.CameraProxy.3
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.m5d("CameraManager", "mAsyncRunnable.run(" + i + ") this=" + CameraProxy.this.mAsyncRunnable + ", mCamera=" + CameraManager.this.mCamera);
                        if (CameraManager.this.mCamera != null && CameraManager.this.mCameraProxy != null) {
                            CameraProxy cameraProxy = CameraManager.this.mCameraProxy;
                            final Camera.Parameters parameters2 = parameters;
                            final int i2 = i;
                            if (!cameraProxy.tryLockParametersRun(new Runnable() { // from class: com.android.camera.CameraManager.CameraProxy.3.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    CameraPerformanceTracker.onEvent("CameraManager", "setParameters", true);
                                    if (CameraManager.this.mCamera != null && parameters2 != null) {
                                        parameters2.setZoom(i2);
                                        CameraManager.this.mCamera.setParameters(parameters2);
                                    }
                                    CameraPerformanceTracker.onEvent("CameraManager", "setParameters", false);
                                }
                            })) {
                                synchronized (CameraProxy.this) {
                                    if (CameraProxy.this.mAsyncRunnable != null) {
                                        CameraManager.this.mCameraHandler.removeCallbacks(CameraProxy.this.mAsyncRunnable);
                                    }
                                    CameraManager.this.mCameraHandler.post(CameraProxy.this.mAsyncRunnable);
                                    Log.m5d("CameraManager", "mAsyncRunnable.post " + CameraProxy.this.mAsyncRunnable);
                                }
                            }
                        }
                    }
                };
                CameraManager.this.mCameraHandler.post(this.mAsyncRunnable);
                Log.m5d("CameraManager", "setParametersAsync(" + i + ") and mAsyncRunnable = " + this.mAsyncRunnable);
            }
        }

        public Camera.Parameters getParameters() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(20);
            CameraManager.this.waitDone();
            return CameraManager.this.mParameters;
        }

        public boolean isFaceDetectionRunning() {
            return CameraManager.this.mFaceDetectionRunning;
        }

        public void setAutoRamaCallback(Camera.AutoRamaCallback autoRamaCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(101, autoRamaCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setAutoRamaMoveCallback(Camera.AutoRamaMoveCallback autoRamaMoveCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(102, autoRamaMoveCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setFbOriginalCallback(Camera.FbOriginalCallback fbOriginalCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(24, fbOriginalCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void startAutoRama(int i) {
            CameraManager.this.mCameraHandler.obtainMessage(103, i, 0).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void stopAutoRama(int i) {
            CameraManager.this.mCameraHandler.obtainMessage(104, i, 0).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setAsdCallback(Camera.AsdCallback asdCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(108, asdCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setStereoCameraDataCallback(Camera.StereoCameraDataCallback stereoCameraDataCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(125, stereoCameraDataCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setStereoCameraWarningCallback(Camera.StereoCameraWarningCallback stereoCameraWarningCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(126, stereoCameraWarningCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setStereoCameraDistanceCallback(Camera.DistanceInfoCallback distanceInfoCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(128, distanceInfoCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void cancelContinuousShot() {
            CameraManager.this.mCameraHandler.sendEmptyMessage(ScriptIntrinsicBLAS.TRANSPOSE);
            CameraManager.this.waitDone();
        }

        public void setContinuousShotSpeed(int i) {
            CameraManager.this.mCameraHandler.obtainMessage(ScriptIntrinsicBLAS.CONJ_TRANSPOSE, i, 0).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setContinuousShotCallback(Camera.ContinuousShotCallback continuousShotCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(115, continuousShotCallback).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setPreviewDisplayAsync(SurfaceHolder surfaceHolder) {
            CameraManager.this.mCameraHandler.obtainMessage(124, surfaceHolder).sendToTarget();
            CameraManager.this.waitDone();
        }

        public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
            CameraManager.this.mCameraHandler.obtainMessage(129, previewCallback).sendToTarget();
        }

        public void lockParameters() throws InterruptedException {
            this.mLock.lock();
        }

        public void unlockParameters() {
            this.mLock.unlock();
        }

        private boolean tryLockParameters(long j) throws InterruptedException {
            boolean zTryLock = this.mLock.tryLock(j, TimeUnit.MILLISECONDS);
            Log.m5d("CameraManager", "try lock: grabbed lock status " + zTryLock);
            return zTryLock;
        }

        public void lockParametersRun(Runnable runnable) {
            boolean z = false;
            try {
                try {
                    lockParameters();
                    z = true;
                    runnable.run();
                    unlockParameters();
                } catch (InterruptedException e) {
                    Log.m7e("CameraManager", "lockParametersRun() not successfull.", e);
                    if (z) {
                        unlockParameters();
                    }
                }
            } catch (Throwable th) {
                if (z) {
                    unlockParameters();
                }
                throw th;
            }
        }

        public boolean tryLockParametersRun(Runnable runnable) {
            boolean zTryLockParameters = false;
            try {
                try {
                    zTryLockParameters = tryLockParameters(500L);
                    if (zTryLockParameters) {
                        runnable.run();
                    }
                } catch (InterruptedException e) {
                    Log.m7e("CameraManager", "tryLockParametersRun() not successfull.", e);
                    if (zTryLockParameters) {
                        unlockParameters();
                    }
                }
                Log.m5d("CameraManager", "tryLockParametersRun(" + runnable + ") return " + zTryLockParameters);
                return zTryLockParameters;
            } finally {
                if (zTryLockParameters) {
                    unlockParameters();
                }
            }
        }
    }

    public boolean waitDone() {
        final Object obj = new Object();
        Runnable runnable = new Runnable() { // from class: com.android.camera.CameraManager.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (obj) {
                    obj.notifyAll();
                }
            }
        };
        synchronized (obj) {
            this.mCameraHandler.post(runnable);
            try {
                obj.wait();
            } catch (InterruptedException e) {
                Log.m10v("CameraManager", "waitDone interrupted");
                return false;
            }
        }
        return true;
    }

    private class ErrorCallbackWrapper implements Camera.ErrorCallback {
        private final Camera.ErrorCallback mErrorCallback;

        public ErrorCallbackWrapper(Camera.ErrorCallback errorCallback) {
            this.mErrorCallback = errorCallback;
        }

        @Override // android.hardware.Camera.ErrorCallback
        public void onError(int i, Camera camera) {
            CameraManager.this.mIsCameraInError = true;
            this.mErrorCallback.onError(i, camera);
            if (i == 100) {
                CameraManager.this.mCameraHandler.sendEmptyMessage(1);
            }
        }
    }
}
