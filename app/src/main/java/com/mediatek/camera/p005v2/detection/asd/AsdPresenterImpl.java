package com.mediatek.camera.p005v2.detection.asd;

import android.os.Handler;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.detection.IDetectionCaptureObserver;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.detection.IDetectionPresenter;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.setting.SettingConvertor;

/* loaded from: classes.dex */
public class AsdPresenterImpl implements IAsdPresenterListener, IDetectionPresenter {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AsdPresenterImpl.class.getSimpleName());
    private AsdDeviceImpl mAsdDevice;
    private IAsdView mAsdView;
    private ISettingServant mSettingServant;
    private Runnable mViewUpdateRunnable;
    private boolean mIsAsdStarted = false;
    private final Handler mHandler = new Handler();

    public AsdPresenterImpl(IAsdView iAsdView, IDetectionManager.IDetectionListener iDetectionListener, ISettingServant iSettingServant) {
        this.mAsdView = iAsdView;
        this.mSettingServant = iSettingServant;
        this.mAsdDevice = new AsdDeviceImpl(iDetectionListener);
        this.mAsdDevice.setListener(this);
    }

    @Override // com.mediatek.camera.p005v2.detection.asd.IAsdPresenterListener
    public void onSceneUpdate(final int i) {
        this.mViewUpdateRunnable = new Runnable() { // from class: com.mediatek.camera.v2.detection.asd.AsdPresenterImpl.1
            @Override // java.lang.Runnable
            public void run() {
                boolean zEqualsIgnoreCase = SettingConvertor.ASDDetectMode.ON.toString().equalsIgnoreCase(AsdPresenterImpl.this.mSettingServant.getSettingValue("pref_asd_key"));
                LogHelper.m23d(AsdPresenterImpl.TAG, "onSceneUpdate mIsAsdStarted is " + AsdPresenterImpl.this.mIsAsdStarted + ", isAsdOn:" + zEqualsIgnoreCase);
                if (AsdPresenterImpl.this.mAsdView != null && zEqualsIgnoreCase) {
                    AsdPresenterImpl.this.mAsdView.updateAsdView(i);
                }
            }
        };
        this.mHandler.post(this.mViewUpdateRunnable);
    }

    @Override // com.mediatek.camera.p005v2.detection.ICaptureObserver
    public IDetectionCaptureObserver getCaptureObserver() {
        return this.mAsdDevice.getCaptureObserver();
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionPresenter
    public void startDetection() {
        LogHelper.m26i(TAG, "[startDetection], mIsAsdStarted:" + this.mIsAsdStarted);
        if (this.mIsAsdStarted) {
            LogHelper.m26i(TAG, "is AsdStarted, call twice,so return");
        } else {
            this.mAsdDevice.requestStartDetection();
            this.mIsAsdStarted = true;
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionPresenter
    public void stopDetection() {
        LogHelper.m26i(TAG, "[stopAsd] mIsAsdStarted = " + this.mIsAsdStarted);
        if (!this.mIsAsdStarted) {
            LogHelper.m26i(TAG, "Asd not Started, why call stop,so return");
            return;
        }
        this.mAsdDevice.requestStopDetection();
        this.mHandler.removeCallbacks(this.mViewUpdateRunnable);
        this.mIsAsdStarted = false;
        this.mAsdView.hideAsdView();
    }
}
