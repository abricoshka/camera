package com.mediatek.camera.p005v2.control.focus;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.control.ControlHelper;
import com.mediatek.camera.p005v2.control.IControl$AutoFocusState;
import com.mediatek.camera.p005v2.control.IControl$FocusStateListener;
import com.mediatek.camera.p005v2.control.IControl$IAaaListener;
import com.mediatek.camera.p005v2.module.ModuleListener;
import com.mediatek.camera.p005v2.platform.app.AppController;
import com.mediatek.camera.p005v2.services.ISoundPlayback;
import com.mediatek.camera.p005v2.setting.ISettingServant;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class AutoFocus implements IFocus, ISettingServant.ISettingChangedListener {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AutoFocus.class.getSimpleName());
    private IControl$IAaaListener mAaaListener;
    private final Activity mActivity;
    private final AppController mAppController;
    private ViewGroup mAutoFocusParentViewGroup;
    private AutoFocusRotateLayout mAutoFocusRotateLayout;
    private View mFocusIndicator;
    private final Handler mMainHandler;
    private ViewGroup mParentViewGroup;
    private RectF mPreviewArea;
    private final ISettingServant mSettingServant;
    private final ISoundPlayback mSoundPlayer;
    private ArrayList<String> mCaredSettingChangedKeys = new ArrayList<>();
    private int mAfMode = 4;
    private boolean mTapToFocusWaitForActiveScan = false;
    private boolean mIsAfTriggerRequestSubmitted = false;
    private boolean mAfTriggerEnabled = false;
    private MeteringRectangle[] mAFRegions = ControlHelper.ZERO_WEIGHT_3A_REGION;
    private MeteringRectangle[] mAERegions = ControlHelper.ZERO_WEIGHT_3A_REGION;
    private long mLastControlAfStateFrameNumber = -1;
    private int mLastResultAFState = 0;
    private String mFaceDetectionValue = null;
    private ModuleListener.RequestType mLastRequestType = ModuleListener.RequestType.PREVIEW;
    private ArrayList<IControl$FocusStateListener> mFocusStateListener = new ArrayList<>();
    private final Runnable mReturnToContinuousAFRunnable = new Runnable() { // from class: com.mediatek.camera.v2.control.focus.AutoFocus.1
        @Override // java.lang.Runnable
        public void run() {
            AutoFocus.this.mIsAfTriggerRequestSubmitted = false;
            AutoFocus.this.resetTouchFocus();
            AutoFocus.this.mAFRegions = ControlHelper.ZERO_WEIGHT_3A_REGION;
            AutoFocus.this.mAfMode = 4;
            AutoFocus.this.mAaaListener.requestChangeCaptureRequets(true, AutoFocus.this.mAaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
        }
    };
    private final Runnable mClearAutoFocusUIRunnable = new Runnable() { // from class: com.mediatek.camera.v2.control.focus.AutoFocus.2
        @Override // java.lang.Runnable
        public void run() {
            AutoFocus.this.mAutoFocusRotateLayout.clear();
        }
    };

    public AutoFocus(ISettingServant iSettingServant, ISoundPlayback iSoundPlayback, AppController appController, IControl$IAaaListener iControl$IAaaListener, ViewGroup viewGroup) {
        this.mSettingServant = iSettingServant;
        this.mAaaListener = iControl$IAaaListener;
        this.mSoundPlayer = iSoundPlayback;
        this.mAppController = appController;
        this.mActivity = appController.getActivity();
        this.mMainHandler = new Handler(this.mActivity.getMainLooper());
        this.mParentViewGroup = viewGroup;
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void open(Activity activity, ViewGroup viewGroup, boolean z) {
        intializeFocusUi(this.mParentViewGroup);
        updateCaredSettingChangedKeys("pref_camera_id_key");
        this.mSettingServant.registerSettingChangedListener(this, this.mCaredSettingChangedKeys, 1);
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void resume() {
        LogHelper.m26i(TAG, "[resume]+");
        this.mIsAfTriggerRequestSubmitted = false;
        this.mTapToFocusWaitForActiveScan = false;
        this.mLastControlAfStateFrameNumber = -1L;
        this.mAfMode = 4;
        this.mLastResultAFState = 0;
        this.mLastRequestType = ModuleListener.RequestType.PREVIEW;
        resetTouchFocus();
        LogHelper.m26i(TAG, "[resume]-");
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void pause() {
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void close() {
        LogHelper.m26i(TAG, "[close]+");
        unIntializeFocusUi(this.mParentViewGroup);
        this.mSettingServant.unRegisterSettingChangedListener(this);
        LogHelper.m26i(TAG, "[close]-");
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onOrientationCompensationChanged(int i) {
        this.mAutoFocusRotateLayout.setOrientation(i, true);
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onPreviewAreaChanged(RectF rectF) {
        LogHelper.m26i(TAG, "onPreviewAreaChanged width = " + rectF.width() + " height = " + rectF.height());
        this.mPreviewArea = rectF;
        int iMin = Math.min((int) this.mPreviewArea.width(), (int) this.mPreviewArea.height()) / 4;
        ViewGroup.LayoutParams layoutParams = this.mFocusIndicator.getLayoutParams();
        layoutParams.width = iMin;
        layoutParams.height = iMin;
        this.mFocusIndicator.requestLayout();
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onSingleTapUp(float f, float f2) {
        LogHelper.m26i(TAG, "onSingleTapUp x = " + f + " y = " + f2 + " preview area = " + this.mPreviewArea + " mIsAfTriggered : " + this.mIsAfTriggerRequestSubmitted);
        if (this.mPreviewArea == null) {
            return;
        }
        String cameraId = this.mSettingServant.getCameraId();
        CameraCharacteristics cameraCharacteristics = Utils.getCameraCharacteristics(this.mActivity, cameraId);
        if (!hasFocuser(cameraCharacteristics)) {
            return;
        }
        if (!this.mAfTriggerEnabled) {
            this.mFaceDetectionValue = this.mSettingServant.getSettingValue("pref_face_detect_key");
        }
        this.mAfTriggerEnabled = true;
        this.mMainHandler.removeCallbacks(this.mReturnToContinuousAFRunnable);
        this.mAppController.getCameraAppUi().setAllCommonViewEnable(false);
        resetTouchFocus();
        LogHelper.m26i(TAG, "[onSingleTapUp] mIsAfTriggerRequestSubmitted=" + this.mIsAfTriggerRequestSubmitted);
        if (this.mIsAfTriggerRequestSubmitted) {
            sendAutoFocusCancelCaptureRequest();
        }
        this.mTapToFocusWaitForActiveScan = true;
        this.mLastResultAFState = 0;
        this.mSettingServant.doSettingChange("pref_face_detect_key", "off", false);
        Math.min(this.mPreviewArea.width(), this.mPreviewArea.height());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mAutoFocusRotateLayout.getLayoutParams();
        int width = this.mAutoFocusRotateLayout.getWidth();
        int height = this.mAutoFocusRotateLayout.getHeight();
        LogHelper.m26i(TAG, "focus area width = " + width + " height = " + height);
        int iClamp = clamp(((int) f) - (width / 2), 0, ((int) this.mPreviewArea.width()) - width);
        int iClamp2 = clamp(((int) f2) - (height / 2), 0, ((int) this.mPreviewArea.height()) - height);
        int i = (int) (iClamp + this.mPreviewArea.left);
        int i2 = (int) (iClamp2 + this.mPreviewArea.top);
        LogHelper.m26i(TAG, "left = " + i + " top = " + i2 + " focus area width = " + width + " height = " + height);
        if (layoutParams.getLayoutDirection() != 1) {
            layoutParams.setMargins(i, i2, 0, 0);
        } else {
            layoutParams.setMargins(0, i2, ((int) this.mPreviewArea.width()) - (width + i), 0);
        }
        layoutParams.getRules()[13] = 0;
        this.mMainHandler.removeCallbacks(this.mClearAutoFocusUIRunnable);
        this.mAutoFocusRotateLayout.requestLayout();
        this.mAutoFocusRotateLayout.onFocusStarted();
        float[] fArr = {(f - this.mPreviewArea.left) / this.mPreviewArea.width(), (f2 - this.mPreviewArea.top) / this.mPreviewArea.height()};
        Matrix matrix = new Matrix();
        matrix.setRotate(Utils.getDisplayRotation(this.mActivity), 0.5f, 0.5f);
        matrix.mapPoints(fArr);
        LogHelper.m26i(TAG, "onSingleTapUp points[0]:" + fArr[0] + " points[1]:" + fArr[1]);
        int iIntValue = ((Integer) cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
        Rect rectCropRegionForZoom = Utils.cropRegionForZoom(this.mActivity, cameraId, 1.0f);
        this.mAERegions = ControlHelper.aeRegionsForNormalizedCoord(fArr[0], fArr[1], rectCropRegionForZoom, iIntValue);
        this.mAFRegions = ControlHelper.afRegionsForNormalizedCoord(fArr[0], fArr[1], rectCropRegionForZoom, iIntValue);
        sendAutoFocusTriggerCaptureRequest();
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void configuringSessionRequest(ModuleListener.RequestType requestType, CaptureRequest.Builder builder, ModuleListener.CaptureType captureType, boolean z) {
        LogHelper.m26i(TAG, "[configuringSessionRequests] + ");
        addBaselineCaptureKeysToRequest(builder);
        onRequestTypeChanged(requestType);
        if (ModuleListener.RequestType.RECORDING == requestType || ModuleListener.RequestType.VIDEO_SNAP_SHOT == requestType) {
            builder.set(CaptureRequest.CONTROL_AF_MODE, 1);
        } else {
            builder.set(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(this.mAfMode));
        }
        if ((requestType == ModuleListener.RequestType.PREVIEW || requestType == ModuleListener.RequestType.RECORDING) && captureType == ModuleListener.CaptureType.CAPTURE && this.mAfMode == 1) {
            if (this.mIsAfTriggerRequestSubmitted) {
                builder.set(CaptureRequest.CONTROL_AF_TRIGGER, 2);
            } else {
                builder.set(CaptureRequest.CONTROL_AF_TRIGGER, 1);
            }
        }
        LogHelper.m26i(TAG, "[configuringSessionRequests]- requestType = " + requestType + " AFMode:" + this.mAfMode + " captureType:" + captureType + " mIsAfTriggerRequestSubmitted:" + this.mIsAfTriggerRequestSubmitted);
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onPreviewCaptureStarted(CaptureRequest captureRequest, long j, long j2) {
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onPreviewCaptureProgressed(CaptureRequest captureRequest, CaptureResult captureResult) {
    }

    @Override // com.mediatek.camera.p005v2.control.focus.IFocus
    public void onPreviewCaptureCompleted(CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
        autofocusStateChangeDispatcher(totalCaptureResult);
    }

    @Override // com.mediatek.camera.v2.setting.ISettingServant.ISettingChangedListener
    public void onSettingChanged(Map<String, String> map) {
        String str = map.get("pref_camera_id_key");
        if (str != null) {
            LogHelper.m26i(TAG, "camera id changed new:" + str);
            this.mIsAfTriggerRequestSubmitted = false;
            this.mTapToFocusWaitForActiveScan = false;
            this.mLastControlAfStateFrameNumber = -1L;
            this.mLastResultAFState = 0;
            this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.control.focus.AutoFocus.4
                @Override // java.lang.Runnable
                public void run() {
                    AutoFocus.this.resetTouchFocus();
                }
            });
        }
    }

    private void intializeFocusUi(ViewGroup viewGroup) {
        this.mAutoFocusParentViewGroup = (ViewGroup) this.mActivity.getLayoutInflater().inflate(R.layout.focus_indicator_v2, viewGroup, true);
        this.mAutoFocusRotateLayout = (AutoFocusRotateLayout) viewGroup.findViewById(R.id.focus_indicator_rotate_layout);
        this.mFocusIndicator = this.mAutoFocusRotateLayout.findViewById(R.id.focus_indicator);
    }

    private void unIntializeFocusUi(ViewGroup viewGroup) {
        if (this.mAutoFocusParentViewGroup != null) {
            this.mAutoFocusParentViewGroup.removeAllViewsInLayout();
            this.mParentViewGroup.removeView(this.mAutoFocusParentViewGroup);
        }
    }

    private int clamp(int i, int i2, int i3) {
        if (i > i3) {
            return i3;
        }
        if (i < i2) {
            return i2;
        }
        return i;
    }

    private void sendAutoFocusTriggerCaptureRequest() {
        this.mAfMode = 1;
        ModuleListener.RequestType repeatingRequestType = this.mAaaListener.getRepeatingRequestType();
        this.mAaaListener.requestChangeCaptureRequets(true, repeatingRequestType, ModuleListener.CaptureType.CAPTURE);
        this.mAaaListener.requestChangeCaptureRequets(true, repeatingRequestType, ModuleListener.CaptureType.REPEATING_REQUEST);
        this.mIsAfTriggerRequestSubmitted = true;
    }

    private void sendAutoFocusCancelCaptureRequest() {
        this.mAfMode = 1;
        this.mAaaListener.requestChangeCaptureRequets(true, this.mAaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.CAPTURE);
        this.mIsAfTriggerRequestSubmitted = false;
    }

    private void addBaselineCaptureKeysToRequest(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AF_REGIONS, this.mAFRegions);
        builder.set(CaptureRequest.CONTROL_AE_REGIONS, this.mAERegions);
        builder.set(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(this.mAfMode));
        builder.set(CaptureRequest.CONTROL_AF_TRIGGER, 0);
    }

    private void autofocusStateChangeDispatcher(CaptureResult captureResult) {
        long frameNumber = captureResult.getFrameNumber();
        if (frameNumber < this.mLastControlAfStateFrameNumber || captureResult.get(CaptureResult.CONTROL_AF_STATE) == null) {
            LogHelper.m26i(TAG, "frame number, last:current " + this.mLastControlAfStateFrameNumber + ":" + frameNumber + " afState:" + captureResult.get(CaptureResult.CONTROL_AF_STATE));
            return;
        }
        Face[] faceArr = (Face[]) captureResult.get(CaptureResult.STATISTICS_FACES);
        if (faceArr != null && faceArr.length > 0) {
            if (this.mAfTriggerEnabled) {
                return;
            }
            this.mMainHandler.post(this.mClearAutoFocusUIRunnable);
        } else {
            this.mLastControlAfStateFrameNumber = captureResult.getFrameNumber();
            int iIntValue = ((Integer) captureResult.get(CaptureResult.CONTROL_AF_STATE)).intValue();
            if (this.mLastResultAFState != iIntValue) {
                onFocusStatusUpdate(iIntValue);
                notifyFocusStateChanged(iIntValue);
            }
            this.mLastResultAFState = iIntValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetTouchFocus() {
        LogHelper.m26i(TAG, "resetTouchFocus");
        this.mAutoFocusRotateLayout.clear();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mAutoFocusRotateLayout.getLayoutParams();
        int[] rules = layoutParams.getRules();
        layoutParams.setMargins(0, 0, 0, 0);
        rules[13] = -1;
        this.mAutoFocusRotateLayout.requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resumeContinuousAF() {
        if (this.mAfTriggerEnabled) {
            this.mSettingServant.doSettingChange("pref_face_detect_key", this.mFaceDetectionValue, false);
        }
        this.mAfTriggerEnabled = false;
        this.mTapToFocusWaitForActiveScan = true;
        this.mAppController.getCameraAppUi().setAllCommonViewEnable(true);
        this.mMainHandler.removeCallbacks(this.mReturnToContinuousAFRunnable);
        this.mMainHandler.postDelayed(this.mReturnToContinuousAFRunnable, 1000L);
    }

    private void onFocusStatusUpdate(final int i) {
        LogHelper.m26i(TAG, "onFocusStatusUpdate resultAFState: " + i + " cameraId:" + this.mSettingServant.getCameraId());
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.control.focus.AutoFocus.5
            @Override // java.lang.Runnable
            public void run() {
                switch (i) {
                    case 1:
                        if (AutoFocus.this.mAaaListener.getRepeatingRequestType() != ModuleListener.RequestType.RECORDING) {
                            AutoFocus.this.mAutoFocusRotateLayout.onFocusStarted();
                        }
                        if (!ControlHelper.ZERO_WEIGHT_3A_REGION.equals(AutoFocus.this.mAERegions)) {
                            AutoFocus.this.mAERegions = ControlHelper.ZERO_WEIGHT_3A_REGION;
                            AutoFocus.this.mAaaListener.requestChangeCaptureRequets(false, AutoFocus.this.mAaaListener.getRepeatingRequestType(), ModuleListener.CaptureType.REPEATING_REQUEST);
                            break;
                        }
                        break;
                    case 2:
                        if (AutoFocus.this.mAaaListener.getRepeatingRequestType() != ModuleListener.RequestType.RECORDING && !AutoFocus.this.mAfTriggerEnabled) {
                            AutoFocus.this.mAutoFocusRotateLayout.setPassiveFocusSuccess(true);
                            break;
                        }
                        break;
                    case 3:
                        AutoFocus.this.mTapToFocusWaitForActiveScan = false;
                        break;
                    case 4:
                        if (!AutoFocus.this.mTapToFocusWaitForActiveScan) {
                            AutoFocus.this.mAutoFocusRotateLayout.onFocusSucceeded();
                            if (AutoFocus.this.mAaaListener.getRepeatingRequestType() != ModuleListener.RequestType.RECORDING) {
                                AutoFocus.this.mSoundPlayer.play(0);
                            }
                            AutoFocus.this.resumeContinuousAF();
                            break;
                        }
                        break;
                    case 5:
                        if (!AutoFocus.this.mTapToFocusWaitForActiveScan) {
                            AutoFocus.this.mAutoFocusRotateLayout.onFocusFailed();
                            AutoFocus.this.resumeContinuousAF();
                            break;
                        }
                        break;
                    case 6:
                        if (AutoFocus.this.mAaaListener.getRepeatingRequestType() != ModuleListener.RequestType.RECORDING && !AutoFocus.this.mAfTriggerEnabled) {
                            AutoFocus.this.mAutoFocusRotateLayout.setPassiveFocusSuccess(false);
                            break;
                        }
                        break;
                }
            }
        });
    }

    private boolean hasFocuser(CameraCharacteristics cameraCharacteristics) {
        Float f = (Float) cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (f != null && f.floatValue() > 0.0f) {
            return true;
        }
        int[] iArr = (int[]) cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        if (iArr == null) {
            return false;
        }
        for (int i : iArr) {
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                    return true;
                default:
            }
        }
        return false;
    }

    private void updateCaredSettingChangedKeys(String str) {
        if (str != null && (!this.mCaredSettingChangedKeys.contains(str))) {
            this.mCaredSettingChangedKeys.add(str);
        }
    }

    private void onRequestTypeChanged(final ModuleListener.RequestType requestType) {
        if ((ModuleListener.RequestType.RECORDING == requestType || ModuleListener.RequestType.PREVIEW == requestType) && this.mLastRequestType != requestType) {
            LogHelper.m23d(TAG, "onRequestTypeChanged mLastRequestType = " + this.mLastRequestType + " ,the new type = " + requestType);
            this.mActivity.runOnUiThread(new Runnable() { // from class: com.mediatek.camera.v2.control.focus.AutoFocus.6
                @Override // java.lang.Runnable
                public void run() {
                    AutoFocus.this.resetTouchFocus();
                    AutoFocus.this.mLastRequestType = requestType;
                }
            });
        }
    }

    private void notifyFocusStateChanged(int i) {
        IControl$AutoFocusState iControl$AutoFocusState;
        IControl$AutoFocusState iControl$AutoFocusState2 = IControl$AutoFocusState.INACTIVE;
        switch (i) {
            case 1:
                iControl$AutoFocusState = IControl$AutoFocusState.PASSIVE_SCAN;
                break;
            case 2:
                iControl$AutoFocusState = IControl$AutoFocusState.PASSIVE_FOCUSED;
                break;
            case 3:
                iControl$AutoFocusState = IControl$AutoFocusState.ACTIVE_SCAN;
                break;
            case 4:
                iControl$AutoFocusState = IControl$AutoFocusState.ACTIVE_FOCUSED;
                break;
            case 5:
                iControl$AutoFocusState = IControl$AutoFocusState.ACTIVE_UNFOCUSED;
                break;
            case 6:
                iControl$AutoFocusState = IControl$AutoFocusState.PASSIVE_UNFOCUSED;
                break;
            default:
                iControl$AutoFocusState = iControl$AutoFocusState2;
                break;
        }
        Iterator<T> it = this.mFocusStateListener.iterator();
        while (it.hasNext()) {
            ((IControl$FocusStateListener) it.next()).onFocusStatusUpdate(iControl$AutoFocusState);
        }
    }
}
