package com.android.camera.p002v2.bridge.device;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class CameraDeviceProxyImpl implements CameraDeviceProxy {

    /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$CaptureTypeSwitchesValues */
    private static final /* synthetic */ int[] f64xc7312b7a = null;

    /* renamed from: -com-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static final /* synthetic */ int[] f65x71d17683 = null;
    private final Activity mActivity;
    private CameraCaptureSession mCameraCaptureSession;
    private final CameraDevice mCameraDevice;
    private final CameraHandler mCameraHandler;
    private final String mCameraId;
    private final CameraDeviceProxy.CameraSessionCallback mCameraSessionCallback;
    private final Handler mSessionStateHandler;
    private final LogHelper.Tag mTag;
    private SessionStateCallback mSessionStateCallback = new SessionStateCallback(this, null);
    private volatile boolean mIsClosed = false;
    private volatile boolean mIsSessionAbortCalled = false;
    private AtomicInteger mPendingSessionChangeCount = new AtomicInteger();
    private Object mSessionLock = new Object();
    private final HandlerThread mCameraThread = new HandlerThread("CameraDeviceProxyImpl");

    /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$CaptureTypeSwitchesValues */
    private static /* synthetic */ int[] m17xeb6efa56() {
        if (f64xc7312b7a != null) {
            return f64xc7312b7a;
        }
        int[] iArr = new int[ModuleListener.CaptureType.valuesCustom().length];
        try {
            iArr[ModuleListener.CaptureType.CAPTURE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ModuleListener.CaptureType.CAPTURE_BURST.ordinal()] = 7;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ModuleListener.CaptureType.REPEATING_BURST.ordinal()] = 8;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ModuleListener.CaptureType.REPEATING_REQUEST.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        f64xc7312b7a = iArr;
        return iArr;
    }

    /* renamed from: -getcom-mediatek-camera-v2-module-ModuleListener$RequestTypeSwitchesValues */
    private static /* synthetic */ int[] m18x960f455f() {
        if (f65x71d17683 != null) {
            return f65x71d17683;
        }
        int[] iArr = new int[ModuleListener.RequestType.valuesCustom().length];
        try {
            iArr[ModuleListener.RequestType.MANUAL.ordinal()] = 7;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ModuleListener.RequestType.PREVIEW.ordinal()] = 1;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ModuleListener.RequestType.RECORDING.ordinal()] = 2;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[ModuleListener.RequestType.STILL_CAPTURE.ordinal()] = 3;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[ModuleListener.RequestType.VIDEO_SNAP_SHOT.ordinal()] = 4;
        } catch (NoSuchFieldError e5) {
        }
        try {
            iArr[ModuleListener.RequestType.ZERO_SHUTTER_DELAY.ordinal()] = 8;
        } catch (NoSuchFieldError e6) {
        }
        f65x71d17683 = iArr;
        return iArr;
    }

    public CameraDeviceProxyImpl(Activity activity, CameraDevice cameraDevice, CameraDeviceProxy.CameraSessionCallback cameraSessionCallback, Handler handler) {
        this.mActivity = activity;
        this.mCameraDevice = cameraDevice;
        this.mCameraId = this.mCameraDevice.getId();
        this.mTag = getTag(this.mCameraId);
        this.mCameraSessionCallback = cameraSessionCallback;
        this.mSessionStateHandler = handler;
        this.mCameraThread.start();
        this.mCameraHandler = new CameraHandler(this.mCameraThread.getLooper());
        this.mPendingSessionChangeCount.set(0);
    }

    @Override // com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy
    public void requestChangeCaptureRequets(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        LogHelper.m23d(this.mTag, "[requestChangeCaptureRequets]+ requestType:" + requestType + " captureType:" + captureType + " sync:" + z + " mIsClosed:" + this.mIsClosed + " mCameraCaptureSession:" + this.mCameraCaptureSession + " mIsSessionAbortCalled:" + this.mIsSessionAbortCalled);
        if (this.mIsClosed) {
            return;
        }
        synchronized (this.mSessionLock) {
            if (this.mIsSessionAbortCalled) {
                try {
                    LogHelper.m23d(this.mTag, "waiting for new session ready...");
                    this.mSessionLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        HashMap map = new HashMap();
        map.put("requestType", requestType);
        map.put("captureType", captureType);
        this.mCameraHandler.obtainMessage(1, map).sendToTarget();
        if (z) {
            waitMessageProcessDone();
        }
        LogHelper.m23d(this.mTag, "[requestChangeCaptureRequets]-");
    }

    @Override // com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy
    public void requestChangeSessionOutputs(boolean z) {
        LogHelper.m23d(this.mTag, "[requestChangeSessionOutputs]+ mIsClosed:" + this.mIsClosed);
        if (this.mIsClosed) {
            return;
        }
        this.mPendingSessionChangeCount.incrementAndGet();
        this.mCameraHandler.removeMessages(1);
        this.mCameraHandler.sendEmptyMessage(0);
        if (z) {
            waitMessageProcessDone();
        }
        LogHelper.m23d(this.mTag, "[requestChangeSessionOutputs]- mIsClosed:" + this.mIsClosed);
    }

    @Override // com.mediatek.camera.p005v2.platform.device.CameraDeviceProxy
    public void close() {
        LogHelper.m23d(this.mTag, "[close]+ mIsClosed:" + this.mIsClosed);
        if (this.mIsClosed) {
            return;
        }
        this.mCameraHandler.removeMessages(1);
        this.mCameraHandler.removeMessages(0);
        this.mCameraHandler.sendEmptyMessage(2);
        waitMessageProcessDone();
        this.mCameraThread.quitSafely();
        LogHelper.m23d(this.mTag, "[close]-");
    }

    private boolean waitMessageProcessDone() {
        if (this.mCameraThread.getThreadId() == ((int) Thread.currentThread().getId())) {
            LogHelper.m23d(this.mTag, "ignore waitDone.");
            return false;
        }
        final Object obj = new Object();
        Runnable runnable = new Runnable() { // from class: com.android.camera.v2.bridge.device.CameraDeviceProxyImpl.1
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
                LogHelper.m26i(this.mTag, "waitDone interrupted");
                return false;
            }
        }
        return true;
    }

    private class CameraHandler extends Handler {
        public CameraHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "handleMessage: what = " + message.what);
            switch (message.what) {
                case 0:
                    try {
                        synchronized (CameraDeviceProxyImpl.this.mSessionLock) {
                            if (CameraDeviceProxyImpl.this.mIsSessionAbortCalled || CameraDeviceProxyImpl.this.mIsClosed) {
                                LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "ignore configure session, mIsSessionAbortCalled:" + CameraDeviceProxyImpl.this.mIsSessionAbortCalled + " DeviceClosed:" + CameraDeviceProxyImpl.this.mIsClosed);
                                return;
                            }
                            if (CameraDeviceProxyImpl.this.mCameraCaptureSession != null) {
                                CameraDeviceProxyImpl.this.mCameraCaptureSession.abortCaptures();
                                CameraDeviceProxyImpl.this.mIsSessionAbortCalled = true;
                            }
                            ArrayList arrayList = new ArrayList();
                            CameraDeviceProxyImpl.this.mCameraSessionCallback.configuringSessionOutputs(arrayList);
                            if (arrayList.size() != 0) {
                                CameraDeviceProxyImpl.this.mCameraDevice.createCaptureSession(arrayList, CameraDeviceProxyImpl.this.mSessionStateCallback, CameraDeviceProxyImpl.this.mCameraHandler);
                            }
                            return;
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                case 1:
                    synchronized (CameraDeviceProxyImpl.this.mSessionLock) {
                        if (CameraDeviceProxyImpl.this.mIsSessionAbortCalled || CameraDeviceProxyImpl.this.mCameraCaptureSession == null || CameraDeviceProxyImpl.this.mIsClosed) {
                            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "ignore request change, mIsSessionAbortCalled:" + CameraDeviceProxyImpl.this.mIsSessionAbortCalled + " mCameraCaptureSession:" + CameraDeviceProxyImpl.this.mCameraCaptureSession + " DeviceClosed:" + CameraDeviceProxyImpl.this.mIsClosed);
                            return;
                        }
                        HashMap map = (HashMap) message.obj;
                        ModuleListener.RequestType requestType = (ModuleListener.RequestType) map.get("requestType");
                        ModuleListener.CaptureType captureType = (ModuleListener.CaptureType) map.get("captureType");
                        CaptureRequest.Builder builderCreateCaptureRequests = CameraDeviceProxyImpl.this.createCaptureRequests(requestType);
                        CameraDeviceProxyImpl.this.submitCaptureRequests(builderCreateCaptureRequests.build(), requestType, captureType, CameraDeviceProxyImpl.this.mCameraSessionCallback.configuringSessionRequests(builderCreateCaptureRequests, requestType, captureType));
                        LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "request change done");
                        return;
                    }
                case 2:
                    if (CameraDeviceProxyImpl.this.mIsClosed) {
                        return;
                    }
                    CameraDeviceProxyImpl.this.mCameraDevice.close();
                    CameraDeviceProxyImpl.this.mIsClosed = true;
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CaptureRequest.Builder createCaptureRequests(ModuleListener.RequestType requestType) throws CameraAccessException {
        CaptureRequest.Builder builderCreateCaptureRequest;
        try {
            switch (m18x960f455f()[requestType.ordinal()]) {
                case 1:
                    builderCreateCaptureRequest = this.mCameraDevice.createCaptureRequest(1);
                    break;
                case 2:
                    builderCreateCaptureRequest = this.mCameraDevice.createCaptureRequest(3);
                    break;
                case 3:
                    builderCreateCaptureRequest = this.mCameraDevice.createCaptureRequest(2);
                    break;
                case 4:
                    builderCreateCaptureRequest = this.mCameraDevice.createCaptureRequest(4);
                    break;
                default:
                    builderCreateCaptureRequest = this.mCameraDevice.createCaptureRequest(1);
                    break;
            }
            return builderCreateCaptureRequest;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void submitCaptureRequests(CaptureRequest captureRequest, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType, CameraCaptureSession.CaptureCallback captureCallback) throws CameraAccessException {
        LogHelper.m23d(this.mTag, "[submitCaptureRequests] captureType = " + captureType);
        if (captureRequest.getTargets().isEmpty() || this.mPendingSessionChangeCount.get() > 0) {
            LogHelper.m24e(this.mTag, "Skip submitCaptureRequests,pending session count:" + this.mPendingSessionChangeCount.get());
            return;
        }
        try {
            switch (m17xeb6efa56()[captureType.ordinal()]) {
                case 1:
                    this.mCameraCaptureSession.capture(captureRequest, captureCallback, this.mCameraHandler);
                    break;
                case 2:
                    this.mCameraCaptureSession.setRepeatingRequest(captureRequest, captureCallback, this.mCameraHandler);
                    break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private class SessionStateCallback extends CameraCaptureSession.StateCallback {
        /* synthetic */ SessionStateCallback(CameraDeviceProxyImpl cameraDeviceProxyImpl, SessionStateCallback sessionStateCallback) {
            this();
        }

        private SessionStateCallback() {
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onActive(CameraCaptureSession cameraCaptureSession) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "onActive");
            if (cameraCaptureSession == CameraDeviceProxyImpl.this.mCameraCaptureSession) {
                CameraDeviceProxyImpl.this.mSessionStateHandler.post(new Runnable() { // from class: com.android.camera.v2.bridge.device.CameraDeviceProxyImpl.SessionStateCallback.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraDeviceProxyImpl.this.mCameraSessionCallback.onSessionActive();
                    }
                });
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onClosed(CameraCaptureSession cameraCaptureSession) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "onClosed, session = " + cameraCaptureSession);
            if (cameraCaptureSession == CameraDeviceProxyImpl.this.mCameraCaptureSession) {
                CameraDeviceProxyImpl.this.mCameraCaptureSession = null;
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "onConfigureFailed, session = " + cameraCaptureSession);
            if (cameraCaptureSession == CameraDeviceProxyImpl.this.mCameraCaptureSession) {
                CameraDeviceProxyImpl.this.mCameraCaptureSession = null;
            }
            CameraDeviceProxyImpl.this.mPendingSessionChangeCount.decrementAndGet();
            cameraCaptureSession.close();
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "onConfigured session:" + cameraCaptureSession);
            synchronized (CameraDeviceProxyImpl.this.mSessionLock) {
                CameraDeviceProxyImpl.this.mCameraCaptureSession = cameraCaptureSession;
                CameraDeviceProxyImpl.this.mIsSessionAbortCalled = false;
                CameraDeviceProxyImpl.this.mPendingSessionChangeCount.decrementAndGet();
                CameraDeviceProxyImpl.this.mSessionLock.notifyAll();
            }
            CameraDeviceProxyImpl.this.mSessionStateHandler.post(new Runnable() { // from class: com.android.camera.v2.bridge.device.CameraDeviceProxyImpl.SessionStateCallback.2
                @Override // java.lang.Runnable
                public void run() {
                    CameraDeviceProxyImpl.this.mCameraSessionCallback.onSessionConfigured();
                }
            });
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onReady(CameraCaptureSession cameraCaptureSession) {
            LogHelper.m23d(CameraDeviceProxyImpl.this.mTag, "onReady,session = " + cameraCaptureSession);
        }
    }

    private LogHelper.Tag getTag(String str) {
        return new LogHelper.Tag("CameraDevice(" + str + ")");
    }
}
