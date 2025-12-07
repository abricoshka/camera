package com.mediatek.camera.p005v2.detection.asd;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.vendortag.TagRequest;
import com.mediatek.camera.p005v2.vendortag.TagResult;

/* loaded from: classes.dex */
public class AsdDeviceImpl {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AsdDeviceImpl.class.getSimpleName());
    private IDetectionManager.IDetectionListener mDetectionListener;
    private IAsdPresenterListener mPresenterListener;
    private boolean mIsAsdOpened = false;
    private boolean mIsFullMode = false;
    private AsdCaptureObserver mAsdCaptureObserver = new AsdCaptureObserver(this, null);

    public AsdDeviceImpl(IDetectionManager.IDetectionListener iDetectionListener) {
        this.mDetectionListener = iDetectionListener;
    }

    public void requestStartDetection() {
        LogHelper.m26i(TAG, "requestStartDetection ");
        this.mIsAsdOpened = true;
        this.mDetectionListener.requestChangeCaptureRequest(false, this.mDetectionListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
    }

    public void requestStopDetection() {
        LogHelper.m26i(TAG, "requestStopDetection");
        this.mIsAsdOpened = false;
        this.mDetectionListener.requestChangeCaptureRequest(true, this.mDetectionListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
    }

    protected void setListener(IAsdPresenterListener iAsdPresenterListener) {
        this.mPresenterListener = iAsdPresenterListener;
    }

    public IDetectionCaptureObserver getCaptureObserver() {
        return this.mAsdCaptureObserver;
    }

    private class AsdCaptureObserver implements IDetectionCaptureObserver {
        /* synthetic */ AsdCaptureObserver(AsdDeviceImpl asdDeviceImpl, AsdCaptureObserver asdCaptureObserver) {
            this();
        }

        private AsdCaptureObserver() {
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void configuringRequests(CaptureRequest.Builder builder, ModuleListener.RequestType requestType) {
            LogHelper.m26i(AsdDeviceImpl.TAG, "configuringRequests mIsAsdOpened = " + AsdDeviceImpl.this.mIsAsdOpened + ",mIsFullMode = " + AsdDeviceImpl.this.mIsFullMode);
            builder.set(TagRequest.STATISTICS_ASD_MODE, Integer.valueOf(AsdDeviceImpl.this.mIsAsdOpened ? AsdDeviceImpl.this.mIsFullMode ? 2 : 1 : 0));
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void onCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
        }

        @Override // com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver
        public void onCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
            LogHelper.m23d(AsdDeviceImpl.TAG, "onCaptureCompleted asdRequest = " + ((Integer) captureRequest.get(TagRequest.STATISTICS_ASD_MODE)));
            int[] iArr = (int[]) totalCaptureResult.get(TagResult.STATISTICS_ASD_RESULT);
            if (iArr == null) {
                return;
            }
            int length = iArr.length;
            for (int i = 0; i < length; i++) {
                LogHelper.m23d(AsdDeviceImpl.TAG, "onCaptureCompleted mode[" + i + "]= " + iArr[i]);
            }
            if (AsdDeviceImpl.this.mPresenterListener != null) {
                AsdDeviceImpl.this.mPresenterListener.onSceneUpdate(iArr[0]);
            }
        }
    }
}
