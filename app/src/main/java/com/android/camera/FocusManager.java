package com.android.camera;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.camera.CameraActivity;
import com.android.camera.p001ui.FocusIndicator;
import com.android.camera.p001ui.FocusIndicatorRotateLayout;
import com.android.camera.p001ui.FrameView;
import com.android.camera.p001ui.MultiZoneAfView;
import com.mediatek.camera.R;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FocusManager implements CameraActivity.OnOrientationListener, CameraActivity.OnParametersReadyListener {
    private static final String[] MATRIX_FOCUS_MODE_CONTINUOUS = {"continuous-picture", "continuous-picture", "continuous-picture", "continuous-picture", "continuous-picture", "continuous-picture", "continuous-picture", "continuous-picture", "continuous-video", "continuous-video", "continuous-video", "continuous-picture", "continuous-picture"};
    private boolean mAeLock;
    private boolean mAeLockSupported;
    private boolean mAwbLock;
    private boolean mAwbLockSupported;
    private CameraActivity mContext;
    private String mContinousFocusMode;
    private boolean mContinousFocusSupported;
    private int mCropPreviewHeight;
    private int mCropPreviewWidth;
    private String[] mDefaultFocusModes;
    private int mDisplayOrientation;
    private String mDistanceInfo;
    private List<Camera.Area> mFocusArea;
    private boolean mFocusAreaSupported;
    private View mFocusIndicator;
    private FocusIndicatorRotateLayout mFocusIndicatorRotateLayout;
    private String mFocusMode;
    private Handler mHandler;
    private boolean mInitialized;
    Listener mListener;
    private boolean mLockAeAwbNeeded;
    private List<Camera.Area> mMeteringArea;
    private boolean mMeteringAreaSupported;
    private boolean mMirror;
    private MultiZoneAfView.MultiWindow[] mMultiAfWindows;
    private MultiZoneAfView mMultiZoneAfView;
    private String mOverrideFocusMode;
    private Camera.Parameters mParameters;
    private ComboPreferences mPreferences;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private int mState = -1;
    private boolean mLockAeNeeded = true;
    private int mOrientation = -1;
    private Matrix mMatrix = new Matrix();
    private Matrix mObjextMatrix = new Matrix();

    public interface Listener {
        void autoFocus();

        void cancelAutoFocus();

        boolean capture();

        void playSound(int i);

        boolean readyToCapture();

        void setFocusParameters();

        void startFaceDetection();

        void stopFaceDetection();
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.m5d("FocusManager", "[handleMessage] msg .what = " + message.what);
            switch (message.what) {
                case 0:
                    FocusManager.this.cancelAutoFocus();
                    break;
                case 1:
                    FocusManager.this.resetFaceBeautyTouchPosition();
                    break;
                case 2:
                    if (FocusManager.this.mMultiZoneAfView != null) {
                        FocusManager.this.mMultiZoneAfView.clear();
                        break;
                    }
                    break;
            }
        }
    }

    public void setFocusAreaIndicator(View view) {
        this.mFocusIndicatorRotateLayout = (FocusIndicatorRotateLayout) view;
        this.mFocusIndicator = view.findViewById(R.id.focus_indicator);
        this.mMultiZoneAfView = (MultiZoneAfView) this.mContext.findViewById(R.id.multi_focus_indicator);
        ((RelativeLayout.LayoutParams) this.mFocusIndicatorRotateLayout.getLayoutParams()).getRules()[13] = -1;
        if (this.mFocusIndicator != null) {
            int iMin = Math.min(this.mPreviewWidth, this.mPreviewHeight) / 4;
            ViewGroup.LayoutParams layoutParams = this.mFocusIndicator.getLayoutParams();
            layoutParams.width = iMin;
            layoutParams.height = iMin;
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setParameters(Camera.Parameters parameters) {
        this.mParameters = parameters;
        this.mFocusAreaSupported = this.mParameters.getMaxNumFocusAreas() > 0 ? isSupported("auto", this.mParameters.getSupportedFocusModes()) : false;
        this.mMeteringAreaSupported = this.mParameters.getMaxNumMeteringAreas() > 0;
        this.mAeLockSupported = this.mParameters.isAutoExposureLockSupported();
        this.mAwbLockSupported = this.mParameters.isAutoWhiteBalanceLockSupported();
        this.mContinousFocusSupported = this.mParameters.getSupportedFocusModes().contains(this.mContinousFocusMode);
        this.mLockAeAwbNeeded = this.mAeLockSupported ? true : this.mAwbLockSupported;
    }

    public void setPreviewSize(int i, int i2) {
        if (this.mPreviewWidth != i || this.mPreviewHeight != i2) {
            this.mPreviewWidth = i;
            this.mPreviewHeight = i2;
            setMatrix();
            if (this.mFocusIndicator != null) {
                int iMin = Math.min(this.mPreviewWidth, this.mPreviewHeight) / 4;
                ViewGroup.LayoutParams layoutParams = this.mFocusIndicator.getLayoutParams();
                layoutParams.width = iMin;
                layoutParams.height = iMin;
            }
        }
    }

    public void setCropPreviewSize(int i, int i2) {
        this.mCropPreviewHeight = i2;
        this.mCropPreviewWidth = i;
    }

    public void setMirror(boolean z) {
        if (this.mMultiZoneAfView != null) {
            this.mMultiZoneAfView.setMirror(z);
        }
        this.mMirror = z;
        setMatrix();
    }

    public void setDisplayOrientation(int i) {
        this.mDisplayOrientation = i;
        if (this.mMultiZoneAfView != null) {
            this.mMultiZoneAfView.setDisplayOrientation(this.mDisplayOrientation);
        }
        setMatrix();
    }

    private void setMatrix() {
        if (this.mPreviewWidth != 0 && this.mPreviewHeight != 0) {
            Matrix matrix = new Matrix();
            Util.prepareMatrix(matrix, this.mMirror, this.mDisplayOrientation, this.mPreviewWidth, this.mPreviewHeight);
            matrix.invert(this.mMatrix);
            Matrix matrix2 = new Matrix();
            Util.prepareMatrix(matrix2, false, this.mDisplayOrientation, this.mPreviewWidth, this.mPreviewHeight);
            matrix2.invert(this.mObjextMatrix);
            this.mInitialized = true;
        }
    }

    public void onShutterDown() {
        Log.m5d("FocusManager", "onShutterDown");
        if (!this.mInitialized) {
            return;
        }
        if (this.mLockAeAwbNeeded) {
            if (!(this.mAeLock ? this.mAwbLock : false)) {
                setAeLock(true);
                this.mAwbLock = true;
                this.mListener.setFocusParameters();
            }
        }
        if (needAutoFocusCall() && this.mState != 3 && this.mState != 4) {
            autoFocus();
        }
    }

    public void onShutterUp() {
        if (!this.mInitialized) {
            return;
        }
        if (needAutoFocusCall() && (this.mState == 1 || this.mState == 3 || this.mState == 4)) {
            cancelAutoFocus();
        }
        boolean z = this.mAeLock ? true : this.mAwbLock;
        if (this.mLockAeAwbNeeded && z && this.mState != 2) {
            this.mAeLock = false;
            this.mAwbLock = false;
            this.mListener.setFocusParameters();
        }
    }

    public void doSnap() {
        Log.m5d("FocusManager", "[doSnap]mInitialized =" + this.mInitialized + " mState=" + this.mState);
        if (!this.mInitialized) {
            return;
        }
        if (!this.mListener.readyToCapture()) {
            Log.m5d("FocusManager", "[doSnap]readyToCapture is false,return.");
            return;
        }
        if (!needAutoFocusCall() || this.mState == 3 || this.mState == 4) {
            capture();
        } else if (this.mState == 1) {
            this.mState = 2;
        } else if (this.mState == 0) {
            capture();
        }
    }

    public void onAutoFocus(boolean z) {
        Log.m5d("FocusManager", "onAutoFocus focused=" + z + " mState=" + this.mState + " mFocusMode=" + this.mFocusMode);
        if (this.mState == 2) {
            if (z) {
                this.mState = 3;
            } else {
                this.mState = 4;
            }
            updateFocusUI();
            capture();
            return;
        }
        if (this.mState == 1) {
            if (z) {
                this.mState = 3;
                if (!"continuous-picture".equals(this.mFocusMode)) {
                    this.mListener.playSound(1);
                }
            } else {
                this.mState = 4;
            }
            updateFocusUI();
            this.mHandler.sendEmptyMessageDelayed(0, 1000L);
            return;
        }
        if (this.mState == 0) {
            this.mHandler.sendEmptyMessage(0);
        }
    }

    public void onAutoFocusMoving(boolean z) {
        Log.m5d("FocusManager", "onAutoFocusMoving = " + z);
        if (getFrameview() != null && getFrameview().faceExists()) {
            return;
        }
        if (this.mState != 0 && this.mState != -1) {
            Log.m5d("FocusManager", "[onAutoFocusMoving]return,mState = " + this.mState);
            return;
        }
        if ("infinity".equals(getCurrentFocusMode(this.mContext))) {
            Log.m5d("FocusManager", "[onAutoFocusMoving]return,current focus mode is INFINISTY.");
            return;
        }
        this.mListener.setFocusParameters();
        if (hasMultiAFData(this.mMultiAfWindows)) {
            if (this.mFocusIndicatorRotateLayout.isFocusing()) {
                this.mFocusIndicatorRotateLayout.clear();
            }
            handleMultiAfWindow(z);
        } else if (z) {
            this.mFocusIndicatorRotateLayout.showStart();
        } else {
            this.mFocusIndicatorRotateLayout.showSuccess(true);
        }
    }

    private boolean hasMultiAFData(MultiZoneAfView.MultiWindow[] multiWindowArr) {
        boolean z = false;
        if (multiWindowArr != null && multiWindowArr.length > 0) {
            z = true;
        }
        Log.m5d("FocusManager", "hasMultiAFData result = " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetFaceBeautyTouchPosition() {
        Log.m5d("FocusManager", "resetFaceBeautyTouchPosition");
        if (this.mContext.getParameters() != null) {
            this.mContext.getParameters().set("fb-touch-pos", "-2000:-2000");
        }
    }

    public void onSingleTapUp(int i, int i2) {
        Log.m5d("FocusManager", "onSingleTapUp x = " + i + " y = " + i2);
        String currentFocusMode = getCurrentFocusMode(this.mContext);
        if (currentFocusMode == null || "infinity".equals(currentFocusMode)) {
            Log.m11w("FocusManager", "[onSingleTapUp]focusMode:" + currentFocusMode);
            return;
        }
        if (!this.mFocusAreaSupported) {
            Log.m8i("FocusManager", "[onSingleTapUp] mFocusAreaSupported is false");
            return;
        }
        if (!this.mInitialized || this.mState == 2 || this.mState == -1) {
            return;
        }
        if (this.mFocusArea != null && (this.mState == 1 || this.mState == 3 || this.mState == 4)) {
            cancelAutoFocus();
        }
        int width = this.mFocusIndicatorRotateLayout.getWidth();
        int height = this.mFocusIndicatorRotateLayout.getHeight();
        if (width == 0 || height == 0) {
            Log.m8i("FocusManager", "UI Component not initialized, cancel this touch");
            return;
        }
        int i3 = this.mPreviewWidth;
        int i4 = this.mPreviewHeight;
        if (this.mFocusArea == null) {
            this.mFocusArea = new ArrayList();
            this.mFocusArea.add(new Camera.Area(new Rect(), 1));
            this.mMeteringArea = new ArrayList();
            this.mMeteringArea.add(new Camera.Area(new Rect(), 1));
        }
        int[] iArrCalculateTapPoint = calculateTapPoint(i, i2);
        if (FeatureSwitcher.isVfbEnable() && this.mContext.getCurrentMode() == 2 && iArrCalculateTapPoint != null && iArrCalculateTapPoint.length == 2) {
            Log.m5d("FocusManager", "[vFB]set touch point to native ,x = " + iArrCalculateTapPoint[0] + ",y = " + iArrCalculateTapPoint[1]);
            this.mContext.getParameters().set("fb-touch-pos", iArrCalculateTapPoint[0] + ":" + iArrCalculateTapPoint[1]);
            this.mHandler.sendEmptyMessageDelayed(1, 40L);
        }
        calculateTapArea(width, height, 1.0f, i, i2, i3, i4, this.mFocusArea.get(0).rect);
        calculateTapArea(width, height, 1.0f, i, i2, i3, i4, this.mMeteringArea.get(0).rect);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mFocusIndicatorRotateLayout.getLayoutParams();
        int iClamp = Util.clamp(i - (width / 2), 0, i3 - width);
        int iClamp2 = Util.clamp(i2 - (height / 2), 0, i4 - height);
        if (layoutParams.getLayoutDirection() != 1) {
            layoutParams.setMargins(iClamp, iClamp2, 0, 0);
        } else {
            layoutParams.setMargins(0, iClamp2, i3 - (width + iClamp), 0);
        }
        layoutParams.getRules()[13] = 0;
        this.mFocusIndicatorRotateLayout.requestLayout();
        this.mListener.stopFaceDetection();
        this.mListener.setFocusParameters();
        autoFocus();
    }

    public void onPreviewStarted() {
        Log.m5d("FocusManager", "onPreviewStarted");
        this.mState = 0;
    }

    public void onPreviewStopped() {
        Log.m5d("FocusManager", "onPreviewStopped");
        this.mState = -1;
        resetTouchFocus();
        updateFocusUI();
    }

    public void onCameraReleased() {
        onPreviewStopped();
    }

    public void cancelAutoFocus() {
        Log.m5d("FocusManager", "Cancel autofocus.");
        resetTouchFocus();
        if (this.mListener != null) {
            this.mListener.cancelAutoFocus();
            this.mListener.startFaceDetection();
        }
        if (getFrameview() != null) {
            getFrameview().resume();
        }
        this.mState = 0;
        updateFocusUI();
        this.mHandler.removeMessages(0);
    }

    private void autoFocus() {
        Log.m5d("FocusManager", "Start autofocus.");
        this.mListener.autoFocus();
        this.mState = 1;
        if (getFrameview() != null) {
            getFrameview().pause();
        }
        updateFocusUI();
        this.mHandler.removeMessages(0);
    }

    private void capture() {
        if (this.mListener.capture()) {
            this.mState = 0;
            this.mFocusArea = null;
            resetTouchFocus();
            updateFocusUI();
            this.mHandler.removeMessages(0);
        }
    }

    public String getFocusMode() {
        Log.m5d("FocusManager", "getFocusMode() mOverrideFocusMode=" + this.mOverrideFocusMode + " mFocusArea=" + this.mFocusArea + " mFocusAreaSupported=" + this.mFocusAreaSupported);
        if (this.mOverrideFocusMode != null) {
            return this.mOverrideFocusMode;
        }
        List<String> supportedFocusModes = this.mParameters.getSupportedFocusModes();
        if (!this.mFocusAreaSupported || this.mFocusArea == null) {
            this.mFocusMode = this.mContinousFocusMode;
            if (this.mFocusMode == null) {
                int i = 0;
                while (true) {
                    if (i >= this.mDefaultFocusModes.length) {
                        break;
                    }
                    String str = this.mDefaultFocusModes[i];
                    if (isSupported(str, supportedFocusModes)) {
                        this.mFocusMode = str;
                        break;
                    }
                    i++;
                }
            }
        } else {
            this.mFocusMode = "auto";
        }
        if (!isSupported(this.mFocusMode, supportedFocusModes)) {
            if (isSupported("auto", this.mParameters.getSupportedFocusModes())) {
                this.mFocusMode = "auto";
            } else {
                this.mFocusMode = this.mParameters.getFocusMode();
            }
        }
        Log.m5d("FocusManager", "getFocusMode() return " + this.mFocusMode);
        return this.mFocusMode;
    }

    public List<Camera.Area> getFocusAreas() {
        return this.mFocusArea;
    }

    public List<Camera.Area> getMeteringAreas() {
        return this.mMeteringArea;
    }

    public void updateFocusUI() {
        if (this.mInitialized) {
            if (this.mMultiZoneAfView != null) {
                this.mMultiZoneAfView.clear();
            }
            boolean zFaceExists = getFrameview() != null ? getFrameview().faceExists() : false;
            FocusIndicator frameview = zFaceExists ? getFrameview() : this.mFocusIndicatorRotateLayout;
            Log.m5d("FocusManager", "updateFocusUI, faceExists = " + zFaceExists + ", mState = " + this.mState + " mFocusArea = " + this.mFocusArea + " focusIndicator = " + frameview);
            if (this.mState == 0 || this.mState == -1) {
                if (this.mFocusArea == null) {
                    frameview.clear();
                    return;
                } else {
                    frameview.showStart();
                    return;
                }
            }
            if (this.mState == 1 || this.mState == 2) {
                frameview.showStart();
                return;
            }
            if (this.mState != 3) {
                if (this.mState == 4) {
                    frameview.showFail(false);
                }
            } else {
                if (this.mParameters != null && "auto".equals(this.mParameters.getFocusMode()) && "on".equals(this.mParameters.get("stereo-distance-measurement")) && (!zFaceExists)) {
                    frameview.needDistanceInfoShow(true);
                }
                frameview.showSuccess(false);
            }
        }
    }

    public void resetTouchFocus() {
        Log.m5d("FocusManager", "resetTouchFocus mInitialized = " + this.mInitialized);
        if (!this.mInitialized) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mFocusIndicatorRotateLayout.getLayoutParams();
        layoutParams.getRules()[13] = -1;
        layoutParams.setMargins(0, 0, 0, 0);
        this.mFocusIndicatorRotateLayout.clear();
        this.mState = 0;
        this.mFocusArea = null;
        this.mMeteringArea = null;
    }

    public void calculateTapArea(int i, int i2, float f, int i3, int i4, int i5, int i6, Rect rect) {
        Log.m5d("FocusManager", "[calculateTapArea] previewWidth = " + i5 + ", previewHeight = " + i6 + ", mCropPreviewHeight = " + this.mCropPreviewHeight + ", mCropPreviewWidth = " + this.mCropPreviewWidth + ", x = " + i3 + ", y = " + i4);
        if (i6 >= i5) {
            i4 += (i6 - this.mCropPreviewHeight) / 2;
        } else {
            i3 += (i5 - this.mCropPreviewWidth) / 2;
        }
        int i7 = (int) (i * f);
        int i8 = (int) (i2 * f);
        RectF rectF = new RectF(Util.clamp(i3 - (i7 / 2), 0, i5 - i7), Util.clamp(i4 - (i8 / 2), 0, i6 - i8), i7 + r2, i8 + r3);
        this.mMatrix.mapRect(rectF);
        Util.rectFToRect(rectF, rect);
    }

    public int[] calculateTapPoint(int i, int i2) {
        float[] fArr = {i, i2};
        this.mObjextMatrix.mapPoints(fArr);
        return Util.pointFToPoint(fArr);
    }

    public boolean isFocusCompleted() {
        return this.mState == 3 || this.mState == 4 || this.mState == 0;
    }

    public void removeMessages() {
        this.mHandler.removeMessages(0);
    }

    public void overrideFocusMode(String str) {
        this.mOverrideFocusMode = str;
    }

    public void setAwbLock(boolean z) {
        this.mAwbLock = z;
    }

    public void setAeLock(boolean z) {
        if (this.mLockAeNeeded) {
            this.mAeLock = z;
        } else {
            this.mAeLock = false;
        }
    }

    public boolean getAwbLock() {
        return this.mAwbLock;
    }

    public boolean getAeLock() {
        return this.mAeLock;
    }

    public void setDistanceInfo(String str) {
        this.mDistanceInfo = str;
        if (this.mFocusIndicatorRotateLayout != null) {
            this.mFocusIndicatorRotateLayout.setDistanceInfo(str);
        }
    }

    public void setAfData(byte[] bArr) {
        if (bArr == null) {
            this.mMultiAfWindows = null;
        } else {
            this.mMultiAfWindows = getMultiWindows(bArr);
        }
    }

    private static boolean isSupported(String str, List<String> list) {
        return list != null && list.indexOf(str) >= 0;
    }

    private boolean needAutoFocusCall() {
        String focusMode = getFocusMode();
        boolean z = !((focusMode.equals("infinity") || focusMode.equals("fixed")) ? true : focusMode.equals("edof"));
        Log.m8i("FocusManager", "needAutoFocusCall,needAutoFocus = " + z);
        return z;
    }

    public void clearFocusAndFaceUi() {
        if (getFrameview() != null) {
            getFrameview().clear();
        }
        clearFocusUi();
    }

    public void clearFocusUi() {
        if (this.mFocusIndicatorRotateLayout.isFocusing()) {
            this.mFocusIndicatorRotateLayout.clear();
        }
        this.mMultiAfWindows = null;
        if (this.mMultiZoneAfView != null) {
            this.mMultiZoneAfView.clear();
        }
    }

    public boolean getAeLockSupported() {
        return this.mAeLockSupported;
    }

    public boolean getAwbLockSupported() {
        return this.mAwbLockSupported;
    }

    public boolean getFocusAreaSupported() {
        return this.mFocusAreaSupported;
    }

    public boolean getMeteringAreaSupported() {
        return this.mMeteringAreaSupported;
    }

    public String getCurrentFocusMode(CameraActivity cameraActivity) {
        if (cameraActivity.getParameters() != null) {
            return cameraActivity.getParameters().getFocusMode();
        }
        return null;
    }

    public FocusManager(CameraActivity cameraActivity, ComboPreferences comboPreferences, View view, Camera.Parameters parameters, Listener listener, boolean z, Looper looper, int i) {
        this.mContext = cameraActivity;
        this.mHandler = new MainHandler(looper);
        this.mPreferences = comboPreferences;
        this.mDefaultFocusModes = getModeDefaultFocusModes(i);
        this.mListener = listener;
        if (view != null) {
            setFocusAreaIndicator(view);
        }
        setParameters(parameters);
        this.mContinousFocusMode = getModeContinousFocusMode(i);
        setMirror(z);
        this.mContext.addOnOrientationListener(this);
        this.mContext.addOnParametersReadyListener(this);
        if (this.mDefaultFocusModes != null) {
            int length = this.mDefaultFocusModes.length;
            for (int i2 = 0; i2 < length; i2++) {
                Log.m5d("FocusManager", "FocusManager() defaultFocusModes[" + i2 + "]=" + this.mDefaultFocusModes[i2]);
            }
        }
    }

    @Override // com.android.camera.CameraActivity.OnOrientationListener
    public void onOrientationChanged(int i) {
        if (this.mOrientation != i && this.mFocusIndicator != null) {
            this.mOrientation = i;
            this.mFocusIndicatorRotateLayout.setOrientation(this.mOrientation, true);
            if (this.mMultiZoneAfView != null) {
                this.mMultiZoneAfView.setOrientation(i);
            }
        }
    }

    @Override // com.android.camera.CameraActivity.OnParametersReadyListener
    public void onCameraParameterReady() {
        if (this.mState == -1) {
            this.mState = 0;
        }
    }

    public void release() {
        this.mContext.removeOnOrientationListener(this);
        this.mContext.removeOnParametersReadyListener(this);
    }

    public FrameView getFrameview() {
        return this.mContext.getFrameManager().getFrameView();
    }

    private String[] getModeDefaultFocusModes(int i) {
        if (i == 8 || i == 9 || i == 10) {
            return new String[]{"auto", "continuous-video", "infinity"};
        }
        return new String[]{"continuous-picture", "auto"};
    }

    private String getModeContinousFocusMode(int i) {
        return MATRIX_FOCUS_MODE_CONTINUOUS[i];
    }

    private void handleMultiAfWindow(boolean z) {
        int length = this.mMultiAfWindows.length;
        if (z) {
            for (int i = 0; i < length; i++) {
                this.mMultiAfWindows[i].mResult = 0;
            }
            if (this.mMultiZoneAfView != null) {
                this.mMultiZoneAfView.updateFocusWindows(this.mMultiAfWindows);
                this.mMultiZoneAfView.showWindows(true);
                return;
            }
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < length; i2++) {
            if (this.mMultiAfWindows[i2].mResult > 0) {
                arrayList.add(this.mMultiAfWindows[i2]);
            }
        }
        MultiZoneAfView.MultiWindow[] multiWindowArr = new MultiZoneAfView.MultiWindow[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            multiWindowArr[i3] = (MultiZoneAfView.MultiWindow) arrayList.get(i3);
        }
        if (this.mMultiZoneAfView != null) {
            this.mMultiZoneAfView.updateFocusWindows(multiWindowArr);
            this.mMultiZoneAfView.showWindows(false);
        }
        this.mHandler.sendEmptyMessageDelayed(2, 1000L);
    }

    private MultiZoneAfView.MultiWindow[] getMultiWindows(byte[] bArr) {
        Log.m5d("FocusManager", "getMultiWindows original data size " + bArr.length);
        IntBuffer intBufferAsIntBuffer = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder()).asIntBuffer();
        int i = intBufferAsIntBuffer.get(0);
        int i2 = intBufferAsIntBuffer.get(1);
        int i3 = intBufferAsIntBuffer.get(2);
        IntBuffer intBufferAsIntBuffer2 = ByteBuffer.wrap(bArr, 12, bArr.length - 12).order(ByteOrder.nativeOrder()).asIntBuffer();
        Log.m5d("FocusManager", "getMultiWindows windowCount " + i + " ,single window (width,height ) from native (" + i2 + " ," + i3 + ")");
        MultiZoneAfView.MultiWindow[] multiWindowArr = new MultiZoneAfView.MultiWindow[i];
        int iLimit = intBufferAsIntBuffer2.limit();
        for (int i4 = 0; i4 < iLimit; i4 += 3) {
            Rect rect = new Rect();
            int i5 = intBufferAsIntBuffer2.get(i4);
            int i6 = intBufferAsIntBuffer2.get(i4 + 1);
            int i7 = intBufferAsIntBuffer2.get(i4 + 2);
            rect.left = i5 - (i2 / 2);
            rect.top = i6 - (i3 / 2);
            rect.right = i5 + (i2 / 2);
            rect.bottom = (i3 / 2) + i6;
            multiWindowArr[i4 / 3] = new MultiZoneAfView.MultiWindow(rect, i7);
        }
        return multiWindowArr;
    }
}
