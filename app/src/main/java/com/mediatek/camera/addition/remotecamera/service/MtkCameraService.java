package com.mediatek.camera.addition.remotecamera.service;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import com.android.camera.CameraActivity;
import com.android.camera.CameraHolder;
import com.android.camera.ComboPreferences;
import com.mediatek.camera.addition.remotecamera.service.IMtkCameraService;
import com.mediatek.camera.platform.Parameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public class MtkCameraService extends Service {
    private RemoteCallbackList<ICameraClientCallback> mRemoteClientCallback;
    private Handler mServiceHandler;
    private String mSupportedFeatures;
    private int mPreviewWidth = 1920;
    private int mPreviewHeight = 1080;
    private int mPictureWidth = 0;
    private int mPictureHeight = 0;
    private int mImageFormat = 17;
    private int mTargetWidth = 240;
    private int mTargetHeight = 240;
    private int mOrientation = 0;
    private long mRefreshInterval = 250;
    private long mLastRefreshTime = 0;
    private boolean mIsDuringCapture = false;
    private long mLastFrameTimeMs = 0;
    private long mCurrentTimeMs = 0;
    private int mFrameRate = 30;
    private boolean mIsClientRequestExit = false;
    private boolean mReleasCamera = false;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i("MtkCameraService", "onCreate()");
        this.mRemoteClientCallback = new RemoteCallbackList<>();
        HandlerThread handlerThread = new HandlerThread("camera service handler thread");
        handlerThread.start();
        this.mServiceHandler = new ServiceHandler(handlerThread.getLooper());
        this.mSupportedFeatures = "supported-features=";
        this.mSupportedFeatures += "Capture,";
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int i) {
        Log.i("MtkCameraService", "startId :" + i);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.i("MtkCameraService", "intent:" + intent.getAction());
        return new MtkCameraServiceImpl();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.mediatek.camera.addition.remotecamera.service.MtkCameraService$1] */
    public void sendKeyCode(final int i) {
        new Thread() { // from class: com.mediatek.camera.addition.remotecamera.service.MtkCameraService.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    new Instrumentation().sendKeyDownUpSync(i);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ByteArrayOutputStream cropFromYuvData(byte[] bArr) {
        Rect rect = new Rect(0, 0, this.mPreviewWidth, this.mPreviewHeight);
        YuvImage yuvImage = new YuvImage(bArr, this.mImageFormat, this.mPreviewWidth, this.mPreviewHeight, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(rect, 100, byteArrayOutputStream);
        return byteArrayOutputStream;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Bitmap decodeJpegToBitmap(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        Bitmap bitmapDecodeByteArray = null;
        try {
            bitmapDecodeByteArray = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
            byteArrayOutputStream.close();
            return bitmapDecodeByteArray;
        } catch (Exception e) {
            return bitmapDecodeByteArray;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] cropScaleRotateJpegData(Bitmap bitmap, int i) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate(i);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int iMin = Math.min(width, height);
        boolean z = width > height;
        try {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, z ? (width - height) / 2 : 0, z ? 0 : (height - width) / 2, iMin, iMin, matrix, true);
            int iMin2 = this.mTargetWidth;
            int iMin3 = this.mTargetHeight;
            if (this.mOrientation == 90 || this.mOrientation == 270) {
                iMin3 = (Math.min(this.mPreviewWidth, this.mPreviewHeight) * iMin2) / Math.max(this.mPreviewWidth, this.mPreviewHeight);
            } else {
                iMin2 = (Math.min(this.mPreviewWidth, this.mPreviewHeight) * iMin3) / Math.max(this.mPreviewWidth, this.mPreviewHeight);
            }
            Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapCreateBitmap, iMin2, iMin3, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapCreateScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            bitmapCreateScaledBitmap.recycle();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
                return byteArray;
            } catch (Exception e) {
                return byteArray;
            }
        } catch (Exception e2) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] scaleCropJpegData(byte[] bArr) {
        Log.d("MtkCameraService", "scaleJpegData()");
        Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        int width = bitmapDecodeByteArray.getWidth();
        int height = bitmapDecodeByteArray.getHeight();
        int iMin = Math.min(width, height);
        boolean z = width > height;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmapDecodeByteArray, z ? (width - height) / 2 : 0, z ? 0 : (height - width) / 2, iMin, iMin);
        new Matrix();
        Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapCreateBitmap, this.mTargetWidth, this.mTargetHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapCreateScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        bitmapCreateScaledBitmap.recycle();
        return byteArray;
    }

    private class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) throws IOException {
            int i = 0;
            long jCurrentTimeMillis = System.currentTimeMillis();
            Log.d("MtkCameraService", "Message.what:" + message.what);
            switch (message.what) {
                case 100:
                    Parameters parameters = (Parameters) message.obj;
                    MtkCameraService.this.mPreviewWidth = parameters.getPreviewSize().width;
                    MtkCameraService.this.mPreviewHeight = parameters.getPreviewSize().height;
                    MtkCameraService.this.mPictureWidth = parameters.getPictureSize().width;
                    MtkCameraService.this.mPictureHeight = parameters.getPictureSize().height;
                    MtkCameraService.this.mImageFormat = parameters.getPreviewFormat();
                    Log.d("MtkCameraService", "mPreviewWidth:" + MtkCameraService.this.mPreviewWidth + ", mPreviewHeight:" + MtkCameraService.this.mPreviewHeight + ", mPictureWidth:" + MtkCameraService.this.mPictureWidth + ", mPictureHeight:" + MtkCameraService.this.mPictureHeight + ", mImageFormat:" + MtkCameraService.this.mImageFormat + ", mReleasCamera:" + MtkCameraService.this.mReleasCamera);
                    if (MtkCameraService.this.mReleasCamera) {
                        MtkCameraService.this.sendKeyCode(4);
                        MtkCameraService.this.mReleasCamera = false;
                        break;
                    }
                    break;
                case 101:
                    Log.d("MtkCameraService", "preview frame comes~ orientation = " + message.arg1);
                    int iBeginBroadcast = MtkCameraService.this.mRemoteClientCallback.beginBroadcast();
                    byte[] bArr = (byte[]) message.obj;
                    if (bArr != null) {
                        byte[] bArrCropScaleRotateJpegData = MtkCameraService.this.cropScaleRotateJpegData(MtkCameraService.this.decodeJpegToBitmap(MtkCameraService.this.cropFromYuvData(bArr)), message.arg1);
                        while (i < iBeginBroadcast) {
                            try {
                                ((ICameraClientCallback) MtkCameraService.this.mRemoteClientCallback.getBroadcastItem(i)).onPreviewFrame(bArrCropScaleRotateJpegData);
                                Log.d("MtkCameraService", "onPreviewFrame dstData = " + bArrCropScaleRotateJpegData);
                            } catch (RemoteException e) {
                            }
                            i++;
                        }
                    }
                    MtkCameraService.this.mRemoteClientCallback.finishBroadcast();
                    break;
                case 102:
                    int iBeginBroadcast2 = MtkCameraService.this.mRemoteClientCallback.beginBroadcast();
                    byte[] bArr2 = (byte[]) message.obj;
                    if (bArr2 != null) {
                        byte[] bArrScaleCropJpegData = MtkCameraService.this.scaleCropJpegData(bArr2);
                        while (i < iBeginBroadcast2) {
                            try {
                                ((ICameraClientCallback) MtkCameraService.this.mRemoteClientCallback.getBroadcastItem(i)).onPictureTaken(bArrScaleCropJpegData);
                            } catch (RemoteException e2) {
                            }
                            i++;
                        }
                    }
                    MtkCameraService.this.mRemoteClientCallback.finishBroadcast();
                    Log.d("MtkCameraService", "process capture frame consume time =" + (System.currentTimeMillis() - jCurrentTimeMillis));
                    break;
                case 103:
                    int iBeginBroadcast3 = MtkCameraService.this.mRemoteClientCallback.beginBroadcast();
                    while (i < iBeginBroadcast3) {
                        try {
                            ((ICameraClientCallback) MtkCameraService.this.mRemoteClientCallback.getBroadcastItem(i)).cameraServerApExit();
                        } catch (RemoteException e3) {
                            Log.e("MtkCameraService", "cameraServerExit exception = " + e3);
                        }
                        i++;
                    }
                    MtkCameraService.this.mRemoteClientCallback.finishBroadcast();
                    break;
                case 104:
                    MtkCameraService.this.mOrientation = message.arg1;
                    int iMin = MtkCameraService.this.mTargetWidth;
                    int iMin2 = MtkCameraService.this.mTargetHeight;
                    if (MtkCameraService.this.mOrientation == 90 || MtkCameraService.this.mOrientation == 270) {
                        iMin2 = (Math.min(MtkCameraService.this.mPreviewWidth, MtkCameraService.this.mPreviewHeight) * iMin) / Math.max(MtkCameraService.this.mPreviewWidth, MtkCameraService.this.mPreviewHeight);
                    } else {
                        iMin = (Math.min(MtkCameraService.this.mPreviewWidth, MtkCameraService.this.mPreviewHeight) * iMin2) / Math.max(MtkCameraService.this.mPreviewWidth, MtkCameraService.this.mPreviewHeight);
                    }
                    Log.d("MtkCameraService", "onOrientationChanged, mOrientation:" + MtkCameraService.this.mOrientation + ",targetHeight:" + iMin2 + ", targetWidth:" + iMin);
                    break;
            }
        }
    }

    public class MtkCameraServiceImpl extends IMtkCameraService.Stub {
        public MtkCameraServiceImpl() {
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void openCamera() throws NumberFormatException, RemoteException {
            Log.d("MtkCameraService", "openCamera");
            CameraHolder cameraHolderInstance = CameraHolder.instance();
            int i = Integer.parseInt(new ComboPreferences(MtkCameraService.this.getApplicationContext()).getGlobal().getString("pref_camera_id_key", "0"));
            if (cameraHolderInstance.tryOpen(i) == null) {
                return;
            }
            cameraHolderInstance.keep(3000, i);
            cameraHolderInstance.release(false);
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClass(MtkCameraService.this.getApplicationContext(), CameraActivity.class);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setFlags(335544320);
            intent.putExtra("android.camera.service.launch", false);
            MtkCameraService.this.getApplicationContext().startActivity(intent);
            MtkCameraService.this.mLastFrameTimeMs = 0L;
            MtkCameraService.this.mCurrentTimeMs = 0L;
            MtkCameraService.this.mIsClientRequestExit = false;
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void setFrameRate(int i) throws RemoteException {
            Log.d("MtkCameraService", "setFrameRate frameRate = " + i);
            MtkCameraService.this.mFrameRate = i;
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void releaseCamera() throws RemoteException {
            Log.d("MtkCameraService", "releaseCamera mIsClientRequestExit = " + MtkCameraService.this.mIsClientRequestExit);
            if (!MtkCameraService.this.mIsClientRequestExit) {
                MtkCameraService.this.mReleasCamera = true;
                MtkCameraService.this.sendKeyCode(4);
                MtkCameraService.this.mIsClientRequestExit = true;
                if (MtkCameraService.this.mServiceHandler != null) {
                    MtkCameraService.this.mServiceHandler.removeCallbacksAndMessages(null);
                }
            }
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public String getSupportedFeatureList() throws RemoteException {
            Log.d("MtkCameraService", "getSupportedFeatureList = " + MtkCameraService.this.mSupportedFeatures);
            return MtkCameraService.this.mSupportedFeatures;
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void capture() throws RemoteException {
            Log.d("MtkCameraService", "capture");
            MtkCameraService.this.mIsDuringCapture = true;
            if (MtkCameraService.this.mServiceHandler != null) {
                MtkCameraService.this.mServiceHandler.removeMessages(101);
            }
            MtkCameraService.this.sendKeyCode(27);
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void sendMessage(Message message) {
            if (message.what == 100 || message.what == 104) {
                if (MtkCameraService.this.mServiceHandler != null) {
                    MtkCameraService.this.mServiceHandler.sendMessage(message);
                    return;
                }
                return;
            }
            if (!MtkCameraService.this.mIsDuringCapture && message.what == 102) {
                return;
            }
            if (MtkCameraService.this.mIsDuringCapture && message.what != 102) {
                MtkCameraService.this.mCurrentTimeMs = System.currentTimeMillis();
                return;
            }
            MtkCameraService.this.mCurrentTimeMs = System.currentTimeMillis();
            long j = MtkCameraService.this.mCurrentTimeMs - MtkCameraService.this.mLastFrameTimeMs;
            if (MtkCameraService.this.mIsDuringCapture || MtkCameraService.this.mLastFrameTimeMs == 0 || j > 1000 / MtkCameraService.this.mFrameRate) {
                MtkCameraService.this.mLastFrameTimeMs = MtkCameraService.this.mCurrentTimeMs;
                if (MtkCameraService.this.mServiceHandler != null) {
                    MtkCameraService.this.mServiceHandler.sendMessage(message);
                }
                MtkCameraService.this.mIsDuringCapture = false;
            }
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void registerCallback(ICameraClientCallback iCameraClientCallback) throws RemoteException {
            Log.d("MtkCameraService", "registerCallback");
            if (iCameraClientCallback != null) {
                MtkCameraService.this.mRemoteClientCallback.register(iCameraClientCallback);
            }
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void unregisterCallback(ICameraClientCallback iCameraClientCallback) throws RemoteException {
            Log.d("MtkCameraService", "unregisterCallback");
            if (iCameraClientCallback != null) {
                MtkCameraService.this.mRemoteClientCallback.unregister(iCameraClientCallback);
            }
        }

        @Override // com.mediatek.camera.addition.remotecamera.service.IMtkCameraService
        public void cameraServerExit() {
            Log.d("MtkCameraService", "cameraServerExit mIsClientRequestExit = " + MtkCameraService.this.mIsClientRequestExit);
            if (!MtkCameraService.this.mIsClientRequestExit) {
                MtkCameraService.this.mIsClientRequestExit = true;
                if (MtkCameraService.this.mServiceHandler != null) {
                    MtkCameraService.this.mServiceHandler.removeCallbacksAndMessages(null);
                    MtkCameraService.this.mServiceHandler.sendEmptyMessage(103);
                }
            }
        }
    }
}
