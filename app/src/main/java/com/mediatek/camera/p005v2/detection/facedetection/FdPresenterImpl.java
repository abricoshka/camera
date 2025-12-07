package com.mediatek.camera.p005v2.detection.facedetection;

import android.graphics.Point;
import android.graphics.Rect;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.detection.IDetectionPresenter;

/* loaded from: classes.dex */
public class FdPresenterImpl implements IFdPresenterListener, IDetectionPresenter {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdPresenterImpl.class.getSimpleName());
    private FdDeviceImpl mFdDeviceImpl;
    private boolean mIsFdStarted = false;
    private FdViewManager mViewManager;

    public FdPresenterImpl(FdViewManager fdViewManager, IDetectionManager.IDetectionListener iDetectionListener) {
        this.mViewManager = fdViewManager;
        this.mFdDeviceImpl = new FdDeviceImpl(iDetectionListener);
        this.mFdDeviceImpl.setListener(this);
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionPresenter
    public void startDetection() {
        if (this.mIsFdStarted) {
            LogHelper.m26i(TAG, "face detection has been stared so return");
            return;
        }
        this.mViewManager.initFaceView();
        this.mFdDeviceImpl.setForceFace3a(this.mViewManager.isForceFace3aSupported());
        this.mFdDeviceImpl.requestStartDetection();
        this.mIsFdStarted = true;
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionPresenter
    public void stopDetection() {
        if (!this.mIsFdStarted) {
            LogHelper.m26i(TAG, "face detection has been stopped or not open so return");
            return;
        }
        this.mFdDeviceImpl.requestStopDetection();
        this.mIsFdStarted = false;
        this.mViewManager.hideFaceView();
    }

    @Override // com.mediatek.camera.p005v2.detection.ICaptureObserver
    public IDetectionCaptureObserver getCaptureObserver() {
        return this.mFdDeviceImpl.getCaptureObserver();
    }

    @Override // com.mediatek.camera.p005v2.detection.facedetection.IFdPresenterListener
    public void onFaceDetected(int[] iArr, Rect[] rectArr, byte[] bArr, Point[][] pointArr, Rect rect) {
        this.mViewManager.showFaceView(iArr, rectArr, bArr, pointArr, rect);
    }
}
