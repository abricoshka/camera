package com.mediatek.camera.p005v2.detection;

import android.app.Activity;
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.view.ViewGroup;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.detection.IDetectionManager;
import com.mediatek.camera.p005v2.detection.asd.AsdPresenterImpl;
import com.mediatek.camera.p005v2.detection.asd.AsdView;
import com.mediatek.camera.p005v2.detection.asd.IAsdView;
import com.mediatek.camera.p005v2.detection.facedetection.FdPresenterImpl;
import com.mediatek.camera.p005v2.detection.facedetection.FdViewManager;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.platform.app.AppUi;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class DetectionManager implements IDetectionManager, ISettingServant.ISettingChangedListener, IDetectionManager.IDetectionListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(DetectionManager.class.getSimpleName());
    private final AppController mAppController;
    private final AppUi mAppUi;
    private AsdPresenterImpl mAsdPresenterImpl;
    private IAsdView mAsdView;
    private final CopyOnWriteArrayList<IDetectionCaptureObserver> mCaptureObservers = new CopyOnWriteArrayList<>();
    private ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    private IDetectionPresenter mDetectionPresenter;
    private FdPresenterImpl mFdPresenterImpl;
    private FdViewManager mFdViewManager;
    private String mInitializedCameraId;
    private final ModuleListener mModuleListener;
    private final ISettingServant mSettingServant;

    public DetectionManager(AppController appController, ModuleListener moduleListener, String str) {
        Assert.assertNotNull(appController);
        Assert.assertNotNull(moduleListener);
        this.mAppController = appController;
        this.mInitializedCameraId = str;
        this.mAppUi = this.mAppController.getCameraAppUi();
        this.mModuleListener = moduleListener;
        this.mSettingServant = this.mAppController.getServices().getSettingController().getSettingServant(str);
        addCaredSettingChangedKeys("pref_camera_id_key");
        this.mAsdView = new AsdView(this.mAppController.getActivity(), this.mAppUi, this.mSettingServant);
        this.mAsdPresenterImpl = new AsdPresenterImpl(this.mAsdView, this, this.mSettingServant);
        addCaredSettingChangedKeys("pref_asd_key");
        this.mFdViewManager = new FdViewManager(this.mSettingServant);
        this.mFdPresenterImpl = new FdPresenterImpl(this.mFdViewManager, this);
        addCaredSettingChangedKeys("pref_face_detect_key");
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        if (map.get("pref_camera_id_key") != null) {
            updateDetectionState("pref_asd_key");
            updateDetectionState("pref_face_detect_key");
            return;
        }
        String str = map.get("pref_asd_key");
        String str2 = map.get("pref_face_detect_key");
        if (str != null) {
            updateDetectionState("pref_asd_key");
        }
        if (str2 != null) {
            updateDetectionState("pref_face_detect_key");
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void open(Activity activity, ViewGroup viewGroup, boolean z) {
        this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
        this.mFdViewManager.open(activity, viewGroup);
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void resume() {
        LogHelper.m26i(TAG, "resume");
        resumeDetectionState("pref_asd_key");
        resumeDetectionState("pref_face_detect_key");
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void pause() {
        LogHelper.m26i(TAG, "pause");
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void close() {
        if (this.mFdViewManager != null) {
            this.mFdViewManager.close();
        }
        this.mSettingServant.unRegisterSettingChangedListener(this);
        this.mAsdPresenterImpl.stopDetection();
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onSingleTapUp(float f, float f2) {
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onLongPressed(float f, float f2) {
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onPreviewAreaChanged(RectF rectF) {
        LogHelper.m26i(TAG, "onPreviewAreaChanged  mFdViewManager:" + this.mFdViewManager);
        if (this.mFdViewManager != null) {
            this.mFdViewManager.onPreviewAreaChanged(rectF);
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onOrientationChanged(int i) {
        if (this.mFdViewManager != null) {
            this.mFdViewManager.onOrientationChanged(i);
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void configuringSessionRequests(Map<ModuleListener.RequestType, CaptureRequest.Builder> map, ModuleListener.CaptureType captureType) {
        for (ModuleListener.RequestType requestType : map.keySet()) {
            CaptureRequest.Builder builder = map.get(requestType);
            Iterator<T> it = this.mCaptureObservers.iterator();
            while (it.hasNext()) {
                ((IDetectionCaptureObserver) it.next()).configuringRequests(builder, requestType);
            }
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
        Iterator<T> it = this.mCaptureObservers.iterator();
        while (it.hasNext()) {
            ((IDetectionCaptureObserver) it.next()).onCaptureStarted(captureRequest, j, j2);
        }
    }

    @Override // com.mediatek.camera.p005v2.detection.IDetectionManager
    public void onCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        LogHelper.m26i(TAG, "onCaptureCompleted camera id:" + this.mSettingServant.getCameraId());
        Iterator<T> it = this.mCaptureObservers.iterator();
        while (it.hasNext()) {
            ((IDetectionCaptureObserver) it.next()).onCaptureCompleted(captureRequest, totalCaptureResult);
        }
    }

    @Override // com.mediatek.camera.v2.detection.IDetectionManager.IDetectionListener
    public ModuleListener.RequestType getRepeatingRequestType() {
        return this.mModuleListener.getRepeatingRequestType();
    }

    @Override // com.mediatek.camera.v2.detection.IDetectionManager.IDetectionListener
    public void requestChangeCaptureRequest(boolean z, ModuleListener.RequestType requestType, ModuleListener.CaptureType captureType) {
        if (this.mInitializedCameraId == null) {
            this.mModuleListener.requestChangeCaptureRequets(z, requestType, captureType);
        } else if ("0".equals(this.mInitializedCameraId)) {
            this.mModuleListener.requestChangeCaptureRequets(true, z, requestType, captureType);
        } else if ("1".equals(this.mInitializedCameraId)) {
            this.mModuleListener.requestChangeCaptureRequets(false, z, requestType, captureType);
        }
    }

    private void addCaredSettingChangedKeys(String str) {
        if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
            this.mCaredSettingChangedKeys.add(str);
        }
    }

    private void registerCaptureObserver(IDetectionCaptureObserver iDetectionCaptureObserver) {
        if (iDetectionCaptureObserver != null && (!this.mCaptureObservers.contains(iDetectionCaptureObserver))) {
            this.mCaptureObservers.add(iDetectionCaptureObserver);
        }
    }

    private void unregisterCaptureObserver(IDetectionCaptureObserver iDetectionCaptureObserver) {
        if (iDetectionCaptureObserver != null && this.mCaptureObservers.contains(iDetectionCaptureObserver)) {
            this.mCaptureObservers.remove(iDetectionCaptureObserver);
        }
    }

    private IDetectionPresenter getPresenterInstance(String str) {
        if (str.equals("pref_asd_key")) {
            this.mDetectionPresenter = this.mAsdPresenterImpl;
        } else if (str.equals("pref_face_detect_key")) {
            this.mDetectionPresenter = this.mFdPresenterImpl;
        }
        return this.mDetectionPresenter;
    }

    private void updateDetectionState(String str) {
        boolean zEquals = "on".equals(this.mSettingServant.getSettingValue(str));
        IDetectionPresenter presenterInstance = getPresenterInstance(str);
        IDetectionCaptureObserver captureObserver = presenterInstance.getCaptureObserver();
        if (zEquals) {
            registerCaptureObserver(captureObserver);
            presenterInstance.startDetection();
        } else {
            presenterInstance.stopDetection();
            unregisterCaptureObserver(captureObserver);
        }
    }

    private void resumeDetectionState(String str) {
        boolean zEquals = "on".equals(this.mSettingServant.getSettingValue(str));
        IDetectionPresenter presenterInstance = getPresenterInstance(str);
        IDetectionCaptureObserver captureObserver = presenterInstance.getCaptureObserver();
        if (zEquals) {
            registerCaptureObserver(captureObserver);
            presenterInstance.startDetection();
        }
    }
}
