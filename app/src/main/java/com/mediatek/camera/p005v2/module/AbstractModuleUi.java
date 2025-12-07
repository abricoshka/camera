package com.mediatek.camera.p005v2.module;

import android.app.Activity;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.ViewGroup;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.p006ui.CountDownView;
import com.mediatek.camera.p005v2.platform.module.ModuleUi;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import com.mediatek.camera.p005v2.util.Utils;
import junit.framework.Assert;

/* loaded from: classes.dex */
public abstract class AbstractModuleUi implements ModuleUi {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(AbstractModuleUi.class.getSimpleName());
    protected final Activity mActivity;
    private final CountDownView mCountdownView;
    protected final AbstractCameraModule mModule;
    private final ModuleGestureListener mModuleGestureListener = new ModuleGestureListener(this, null);
    private final ModulePreviewTouchedListener mModulePreviewTouchedListener = new ModulePreviewTouchedListener(this, 0 == true ? 1 : 0);
    protected final ViewGroup mModuleRootView;
    protected final IPreviewStream.PreviewCallback mPreviewCallback;

    public AbstractModuleUi(Activity activity, AbstractCameraModule abstractCameraModule, ViewGroup viewGroup, IPreviewStream.PreviewCallback previewCallback) {
        Assert.assertNotNull(activity);
        Assert.assertNotNull(abstractCameraModule);
        Assert.assertNotNull(viewGroup);
        Assert.assertNotNull(previewCallback);
        this.mActivity = activity;
        this.mModule = abstractCameraModule;
        this.mModuleRootView = viewGroup;
        this.mPreviewCallback = previewCallback;
        activity.getLayoutInflater().inflate(R.layout.count_down_view, viewGroup, true);
        this.mCountdownView = (CountDownView) viewGroup.findViewById(R.id.count_down_view);
    }

    public void onOrientationChanged(int i) {
        Utils.setRotatableOrientation(this.mModuleRootView, (Utils.getDisplayRotation(this.mActivity) + i) % 360, true);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleUi
    public void onSurfaceAvailable(Surface surface, int i, int i2) {
        this.mPreviewCallback.surfaceAvailable(surface, i, i2);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleUi
    public boolean onSurfaceDestroyed(Surface surface) {
        this.mPreviewCallback.surfaceDestroyed(surface);
        return false;
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleUi
    public void onSurfaceSizeChanged(Surface surface, int i, int i2) {
        this.mPreviewCallback.surfaceSizeChanged(surface, i, i2);
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleUi
    public ModuleUi.GestureListener getGestureListener() {
        return this.mModuleGestureListener;
    }

    @Override // com.mediatek.camera.p005v2.platform.module.ModuleUi
    public ModuleUi.PreviewTouchedListener getPreviewTouchedListener() {
        return this.mModulePreviewTouchedListener;
    }

    public void onPreviewAreaChanged(RectF rectF) {
        this.mCountdownView.onPreviewAreaChanged(rectF);
    }

    public void startCountdown(int i) {
        this.mModule.mAppUi.showInfo(this.mActivity.getString(R.string.count_down_title_text), i * 1000);
        this.mCountdownView.startCountDown(i);
    }

    public void setCountdownFinishedListener(CountDownView.OnCountDownStatusListener onCountDownStatusListener) {
        this.mCountdownView.setCountDownStatusListener(onCountDownStatusListener);
    }

    public boolean isCountingDown() {
        return this.mCountdownView.isCountingDown();
    }

    public void cancelCountDown() {
        this.mCountdownView.cancelCountDown();
    }

    private class ModuleGestureListener implements ModuleUi.GestureListener {
        /* synthetic */ ModuleGestureListener(AbstractModuleUi abstractModuleUi, ModuleGestureListener moduleGestureListener) {
            this();
        }

        private ModuleGestureListener() {
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onDown(float f, float f2) {
            return AbstractModuleUi.this.mModule.onDown(f, f2);
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onUp() {
            return AbstractModuleUi.this.mModule.onUp();
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onScroll(float f, float f2, float f3, float f4) {
            LogHelper.m26i(AbstractModuleUi.TAG, "onScroll (dx,dy)(" + f + "," + f2 + ") totalX = " + f3 + " totalY = " + f4);
            return AbstractModuleUi.this.mModule.onScroll(f, f2, f3, f4);
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onSingleTapUp(float f, float f2) {
            return AbstractModuleUi.this.mModule.onSingleTapUp(f, f2);
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onSingleTapConfirmed(float f, float f2) {
            return false;
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onDoubleTap(float f, float f2) {
            return false;
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onScale(float f, float f2, float f3) {
            return false;
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onScaleBegin(float f, float f2) {
            return false;
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onLongPress(float f, float f2) {
            return AbstractModuleUi.this.mModule.onLongPress(f, f2);
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.GestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return false;
        }
    }

    private class ModulePreviewTouchedListener implements ModuleUi.PreviewTouchedListener {
        /* synthetic */ ModulePreviewTouchedListener(AbstractModuleUi abstractModuleUi, ModulePreviewTouchedListener modulePreviewTouchedListener) {
            this();
        }

        private ModulePreviewTouchedListener() {
        }

        @Override // com.mediatek.camera.v2.platform.module.ModuleUi.PreviewTouchedListener
        public boolean onPreviewTouched() {
            LogHelper.m26i(AbstractModuleUi.TAG, "onPreviewTouched");
            return false;
        }
    }
}
