package com.android.camera.p002v2.bridge;

import android.view.MotionEvent;
import android.view.Surface;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import com.mediatek.camera.p005v2.platform.module.ModuleUi;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class ModuleUIAdapter implements PreviewStatusListener {
    private AppGestureListener mAppGestureListener;
    private AppOnPreviewTouchedListener mAppPreviewTouchedListener;
    private ModuleUi.GestureListener mModuleGestureListener;
    private ModuleUi.PreviewTouchedListener mModulePreviewTouchedListener;
    private final ModuleUi mModuleUi;

    public ModuleUIAdapter(ModuleUi moduleUi) {
        Assert.assertNotNull(moduleUi);
        this.mModuleUi = moduleUi;
    }

    @Override // com.android.camera.p002v2.p003ui.PreviewStatusListener
    public void surfaceAvailable(Surface surface, int i, int i2) {
        this.mModuleUi.onSurfaceAvailable(surface, i, i2);
    }

    @Override // com.android.camera.p002v2.p003ui.PreviewStatusListener
    public void surfaceDestroyed(Surface surface) {
        this.mModuleUi.onSurfaceDestroyed(surface);
    }

    @Override // com.android.camera.p002v2.p003ui.PreviewStatusListener
    public void surfaceSizeChanged(Surface surface, int i, int i2) {
        this.mModuleUi.onSurfaceSizeChanged(surface, i, i2);
    }

    @Override // com.android.camera.p002v2.p003ui.PreviewStatusListener
    public PreviewStatusListener.OnGestureListener getGestureListener() {
        AppGestureListener appGestureListener = null;
        if (this.mAppGestureListener == null) {
            this.mAppGestureListener = new AppGestureListener(this, appGestureListener);
        }
        this.mModuleGestureListener = this.mModuleUi.getGestureListener();
        return this.mAppGestureListener;
    }

    @Override // com.android.camera.p002v2.p003ui.PreviewStatusListener
    public PreviewStatusListener.OnPreviewTouchedListener getTouchListener() {
        AppOnPreviewTouchedListener appOnPreviewTouchedListener = null;
        if (this.mAppPreviewTouchedListener == null) {
            this.mAppPreviewTouchedListener = new AppOnPreviewTouchedListener(this, appOnPreviewTouchedListener);
        }
        this.mModulePreviewTouchedListener = this.mModuleUi.getPreviewTouchedListener();
        return this.mAppPreviewTouchedListener;
    }

    private class AppGestureListener implements PreviewStatusListener.OnGestureListener {
        /* synthetic */ AppGestureListener(ModuleUIAdapter moduleUIAdapter, AppGestureListener appGestureListener) {
            this();
        }

        private AppGestureListener() {
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onDown(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onDown(f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onUp() {
            return ModuleUIAdapter.this.mModuleGestureListener.onUp();
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onFling(motionEvent, motionEvent2, f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onScroll(float f, float f2, float f3, float f4) {
            return ModuleUIAdapter.this.mModuleGestureListener.onScroll(f, f2, f3, f4);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onSingleTapUp(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onSingleTapUp(f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onSingleTapConfirmed(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onSingleTapConfirmed(f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onDoubleTap(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onDoubleTap(f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onScale(float f, float f2, float f3) {
            return ModuleUIAdapter.this.mModuleGestureListener.onScale(f, f2, f3);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onScaleBegin(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onScaleBegin(f, f2);
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnGestureListener
        public boolean onLongPress(float f, float f2) {
            return ModuleUIAdapter.this.mModuleGestureListener.onLongPress(f, f2);
        }
    }

    public ModuleUi getModuleUi() {
        return this.mModuleUi;
    }

    private class AppOnPreviewTouchedListener implements PreviewStatusListener.OnPreviewTouchedListener {
        /* synthetic */ AppOnPreviewTouchedListener(ModuleUIAdapter moduleUIAdapter, AppOnPreviewTouchedListener appOnPreviewTouchedListener) {
            this();
        }

        private AppOnPreviewTouchedListener() {
        }

        @Override // com.android.camera.v2.ui.PreviewStatusListener.OnPreviewTouchedListener
        public boolean onPreviewTouched() {
            if (ModuleUIAdapter.this.mModulePreviewTouchedListener != null) {
                return ModuleUIAdapter.this.mModulePreviewTouchedListener.onPreviewTouched();
            }
            return false;
        }
    }
}
