package com.mediatek.camera.p005v2.detection.facedetection;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.vendortag.TagRequest;
import java.lang.reflect.Array;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class FdDeviceImpl {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdDeviceImpl.class.getSimpleName());
    private IDetectionManager.IDetectionListener mDetectionListener;
    private IFdPresenterListener mListener;
    private FdCaptureObserver mCaptureObserver = new FdCaptureObserver(this, null);
    private boolean mIsFdRequested = false;
    private boolean mIsForceFace3aSupported = false;

    public FdDeviceImpl(IDetectionManager.IDetectionListener iDetectionListener) {
        this.mDetectionListener = iDetectionListener;
    }

    protected IDetectionCaptureObserver getCaptureObserver() {
        return this.mCaptureObserver;
    }

    public void requestStartDetection() {
        LogHelper.m26i(TAG, "startFaceDetection");
        this.mIsFdRequested = true;
        this.mDetectionListener.requestChangeCaptureRequest(false, this.mDetectionListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
    }

    public void requestStopDetection() {
        LogHelper.m26i(TAG, "stopFaceDetection");
        this.mIsFdRequested = false;
        this.mDetectionListener.requestChangeCaptureRequest(false, this.mDetectionListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
    }

    public void setListener(IFdPresenterListener iFdPresenterListener) {
        this.mListener = iFdPresenterListener;
    }

    protected void setForceFace3a(boolean z) {
        this.mIsForceFace3aSupported = z;
    }

    private class FdCaptureObserver implements IDetectionCaptureObserver {
        /* synthetic */ FdCaptureObserver(FdDeviceImpl fdDeviceImpl, FdCaptureObserver fdCaptureObserver) {
            this();
        }

        private FdCaptureObserver() {
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void configuringRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType) {
            int i;
            int i2 = FdDeviceImpl.this.mIsFdRequested ? 1 : 0;
            builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, Integer.valueOf(i2));
            if (FdDeviceImpl.this.mIsForceFace3aSupported) {
                i = FdDeviceImpl.this.mIsFdRequested ? 1 : 0;
                builder.set(TagRequest.STATISTICS_FORCE_FACE_3A, Integer.valueOf(i));
            } else {
                i = 0;
            }
            LogHelper.m26i(FdDeviceImpl.TAG, "configuringRequests done,fdMode = " + i2 + " ,forceFace3a = " + i);
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void onCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void onCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
            Assert.assertNotNull(totalCaptureResult);
            Face[] faceArr = (Face[]) totalCaptureResult.get(CaptureResult.STATISTICS_FACES);
            int length = faceArr.length;
            int[] iArr = (int[]) totalCaptureResult.get(CaptureResult.STATISTICS_FACE_IDS);
            Rect[] rectArr = (Rect[]) totalCaptureResult.get(CaptureResult.STATISTICS_FACE_RECTANGLES);
            byte[] bArr = (byte[]) totalCaptureResult.get(CaptureResult.STATISTICS_FACE_SCORES);
            Point[][] pointArr = (Point[][]) Array.newInstance((Class<?>) Point.class, length, 3);
            Rect rect = (Rect) totalCaptureResult.get(CaptureResult.SCALER_CROP_REGION);
            LogHelper.m26i(FdDeviceImpl.TAG, "[onCaptureCompleted] faces's length = " + length);
            if (length != 0) {
                for (int i = 0; i < length; i++) {
                    pointArr[i][0] = faceArr[i].getLeftEyePosition();
                    pointArr[i][1] = faceArr[i].getRightEyePosition();
                    pointArr[i][2] = faceArr[i].getMouthPosition();
                }
            }
            FdDeviceImpl.this.mListener.onFaceDetected(iArr, rectArr, bArr, pointArr, rect);
        }
    }
}
