package com.android.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.android.camera.CameraManager;
import java.io.IOException;

/* loaded from: classes.dex */
public class CameraHolder {
    private static CameraProxyWrapper sBackCamProxyWrapper;
    private static CameraManager sBackCameraManager;
    private static CameraProxyWrapper sFrontCamProxyWrapper;
    private static CameraManager sFrontCameraManager;
    private static CameraHolder sHolder;
    private int mBackCameraId;
    private int mFrontCameraId;
    private final int mNumberOfCameras = Camera.getNumberOfCameras();
    private final Camera.CameraInfo[] mInfo = new Camera.CameraInfo[this.mNumberOfCameras];

    public static synchronized CameraHolder instance() {
        if (sHolder == null) {
            sHolder = new CameraHolder();
        }
        return sHolder;
    }

    private CameraHolder() {
        CameraProxyWrapper cameraProxyWrapper = null;
        this.mBackCameraId = -1;
        this.mFrontCameraId = -1;
        com.mediatek.camera.util.Log.m34i("CameraHolder", "mNumberOfCameras = " + this.mNumberOfCameras);
        for (int i = 0; i < this.mNumberOfCameras; i++) {
            this.mInfo[i] = new Camera.CameraInfo();
            Camera.getCameraInfo(i, this.mInfo[i]);
        }
        for (int i2 = 0; i2 < this.mNumberOfCameras; i2++) {
            if (this.mBackCameraId == -1 && this.mInfo[i2].facing == 0) {
                this.mBackCameraId = i2;
                sBackCameraManager = new CameraManager("BackCam");
                sBackCamProxyWrapper = new CameraProxyWrapper(this, this.mBackCameraId, sBackCameraManager, cameraProxyWrapper);
            } else if (this.mFrontCameraId == -1 && this.mInfo[i2].facing == 1) {
                this.mFrontCameraId = i2;
                sFrontCameraManager = new CameraManager("FrontCam");
                sFrontCamProxyWrapper = new CameraProxyWrapper(this, this.mFrontCameraId, sFrontCameraManager, cameraProxyWrapper);
            }
        }
    }

    public int getNumberOfCameras() {
        return this.mNumberOfCameras;
    }

    public Camera.CameraInfo[] getCameraInfo() {
        com.mediatek.camera.util.Log.m31d("CameraHolder", "getCameraInfo,size = " + this.mInfo.length);
        return this.mInfo;
    }

    public CameraManager.CameraProxy open(int i) throws CameraHardwareException {
        com.mediatek.camera.util.Log.m34i("CameraHolder", "CameraHolder open cameraId = " + i);
        Util.assertError(i != -1);
        return getCameraProxyWrapper(i).open();
    }

    public CameraManager.CameraProxy tryOpen(int i) {
        return getCameraProxyWrapper(i).tryOpen();
    }

    public void release(boolean z) {
        if (getCameraProxyWrapper(this.mBackCameraId).getCameraProxy() != null) {
            com.mediatek.camera.util.Log.m34i("CameraHolder", "CameraHolder release back camera");
            getCameraProxyWrapper(this.mBackCameraId).release(z);
        }
        if (getCameraProxyWrapper(this.mFrontCameraId).getCameraProxy() != null) {
            com.mediatek.camera.util.Log.m34i("CameraHolder", "CameraHolder release front camera");
            getCameraProxyWrapper(this.mFrontCameraId).release(z);
        }
    }

    public synchronized void keep(int i, int i2) {
        getCameraProxyWrapper(i2).keep(i);
    }

    public int getBackCameraId() {
        return this.mBackCameraId;
    }

    public int getFrontCameraId() {
        return this.mFrontCameraId;
    }

    public Camera.Parameters getOriginalParameters(int i) {
        if (i == -1) {
            return null;
        }
        return getCameraProxyWrapper(i).getOriginalParameters();
    }

    public CameraManager.CameraProxy getCameraProxy(int i) {
        if (i == -1) {
            return null;
        }
        return getCameraProxyWrapper(i).getCameraProxy();
    }

    private CameraProxyWrapper getCameraProxyWrapper(int i) {
        CameraProxyWrapper cameraProxyWrapper = null;
        if (i == this.mBackCameraId) {
            if (sBackCamProxyWrapper == null) {
                sBackCameraManager = new CameraManager("BackCam");
                sBackCamProxyWrapper = new CameraProxyWrapper(this, i, sBackCameraManager, cameraProxyWrapper);
            }
            return sBackCamProxyWrapper;
        }
        if (sFrontCamProxyWrapper == null) {
            sFrontCameraManager = new CameraManager("FrontCam");
            sFrontCamProxyWrapper = new CameraProxyWrapper(this, i, sFrontCameraManager, cameraProxyWrapper);
        }
        return sFrontCamProxyWrapper;
    }

    private class CameraProxyWrapper {
        private int mCameraId;
        private CameraManager mCameraManager;
        private boolean mCameraOpened;
        private CameraManager.CameraProxy mCameraProxy;
        private final Handler mHandler;
        private long mKeepBeforeTime;
        private Camera.Parameters mParameters;

        /* synthetic */ CameraProxyWrapper(CameraHolder cameraHolder, int i, CameraManager cameraManager, CameraProxyWrapper cameraProxyWrapper) {
            this(i, cameraManager);
        }

        private class MyHandler extends Handler {
            MyHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        synchronized (CameraHolder.this) {
                            if (!CameraProxyWrapper.this.mCameraOpened) {
                                CameraProxyWrapper.this.release(false);
                            }
                        }
                        return;
                    default:
                        return;
                }
            }
        }

        private CameraProxyWrapper(int i, CameraManager cameraManager) {
            this.mCameraId = -1;
            com.mediatek.camera.util.Log.m31d("CameraHolder", "[CameraProxyWrapper]constructor, cameraId = " + i);
            this.mCameraId = i;
            this.mCameraManager = cameraManager;
            HandlerThread handlerThread = new HandlerThread(i + "'s CameraHolder ");
            handlerThread.start();
            this.mHandler = new MyHandler(handlerThread.getLooper());
        }

        public synchronized CameraManager.CameraProxy open() throws CameraHardwareException {
            com.mediatek.camera.util.Log.m31d("CameraHolder", "CameraProxyWrapper open mCameraOpened = " + this.mCameraOpened + " mCameraId = " + this.mCameraId);
            Util.assertError(!this.mCameraOpened);
            if (this.mCameraProxy == null) {
                try {
                    com.mediatek.camera.util.Log.m34i("CameraHolder", "open camera " + this.mCameraId);
                    this.mCameraProxy = this.mCameraManager.cameraOpen(this.mCameraId);
                    this.mParameters = this.mCameraProxy.getParameters();
                    this.mCameraOpened = true;
                    this.mHandler.removeMessages(1);
                    this.mKeepBeforeTime = 0L;
                    com.mediatek.camera.util.Log.m31d("CameraHolder", "open camera " + this.mCameraId + " end mCameraProxy = " + this.mCameraProxy);
                } catch (RuntimeException e) {
                    com.mediatek.camera.util.Log.m33e("CameraHolder", "fail to connect Camera", e);
                    throw new CameraHardwareException(e);
                }
            } else {
                try {
                    this.mCameraProxy.reconnect();
                    this.mCameraProxy.setParameters(this.mParameters);
                    this.mCameraOpened = true;
                    this.mHandler.removeMessages(1);
                    this.mKeepBeforeTime = 0L;
                    com.mediatek.camera.util.Log.m31d("CameraHolder", "open camera " + this.mCameraId + " end mCameraProxy = " + this.mCameraProxy);
                } catch (IOException e2) {
                    com.mediatek.camera.util.Log.m32e("CameraHolder", "reconnect failed.");
                    throw new CameraHardwareException(e2);
                }
            }
            return this.mCameraProxy;
        }

        public CameraManager.CameraProxy tryOpen() {
            try {
                if (this.mCameraOpened) {
                    return null;
                }
                return open();
            } catch (CameraHardwareException e) {
                return null;
            }
        }

        public CameraManager.CameraProxy getCameraProxy() {
            return this.mCameraProxy;
        }

        public synchronized void release(boolean z) {
            synchronized (this) {
                com.mediatek.camera.util.Log.m34i("CameraHolder", "release");
                Util.assertError(this.mCameraProxy != null);
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis < this.mKeepBeforeTime) {
                    if (this.mCameraOpened) {
                        this.mCameraOpened = false;
                        this.mCameraProxy.stopPreview();
                    }
                    this.mHandler.sendEmptyMessageDelayed(1, this.mKeepBeforeTime - jCurrentTimeMillis);
                    return;
                }
                this.mCameraOpened = false;
                if (z) {
                    this.mCameraProxy.releaseAsync();
                } else {
                    this.mCameraProxy.release();
                }
                this.mCameraProxy = null;
                this.mParameters = null;
            }
        }

        public synchronized void keep(int i) {
            this.mKeepBeforeTime = System.currentTimeMillis() + i;
        }

        public Camera.Parameters getOriginalParameters() {
            if (this.mParameters == null) {
                throw new IllegalArgumentException();
            }
            return this.mParameters;
        }
    }
}
