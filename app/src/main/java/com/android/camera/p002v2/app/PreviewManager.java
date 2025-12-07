package com.android.camera.p002v2.app;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.Surface;
import android.view.View;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public abstract class PreviewManager {
    protected View.OnLayoutChangeListener mOnLayoutChangeListener;
    protected SurfaceCallback mSurfaceCallback;
    protected final ArrayList<PreviewStatusListener.OnPreviewAreaChangedListener> mPreviewAreaChangedListeners = new ArrayList<>();
    protected RectF mPreviewArea = new RectF();
    protected GestureDetector mGestureDetector = null;

    public interface SurfaceCallback {
        void surfaceAvailable(Surface surface, int i, int i2);

        void surfaceDestroyed(Surface surface);

        void surfaceSizeChanged(Surface surface, int i, int i2);
    }

    public abstract void updatePreviewSize(int i, int i2);

    public void setSurfaceCallback(SurfaceCallback surfaceCallback) {
        this.mSurfaceCallback = surfaceCallback;
    }

    public void addPreviewAreaSizeChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener) {
        if (onPreviewAreaChangedListener != null && (!this.mPreviewAreaChangedListeners.contains(onPreviewAreaChangedListener))) {
            this.mPreviewAreaChangedListeners.add(onPreviewAreaChangedListener);
            if (this.mPreviewArea.width() != 0.0f || this.mPreviewArea.height() != 0.0f) {
                onPreviewAreaChangedListener.onPreviewAreaChanged(this.mPreviewArea);
            }
        }
    }

    public void removePreviewAreaSizeChangedListener(PreviewStatusListener.OnPreviewAreaChangedListener onPreviewAreaChangedListener) {
        if (onPreviewAreaChangedListener != null && this.mPreviewAreaChangedListeners.contains(onPreviewAreaChangedListener)) {
            this.mPreviewAreaChangedListeners.remove(onPreviewAreaChangedListener);
        }
    }

    public void setGestureListener(View.OnTouchListener onTouchListener) {
    }

    public void onPreviewStarted() {
    }

    public void pause() {
    }

    public void resume() {
    }

    protected void notifyPreviewAreaChanged() {
        Iterator<T> it = this.mPreviewAreaChangedListeners.iterator();
        while (it.hasNext()) {
            ((PreviewStatusListener.OnPreviewAreaChangedListener) it.next()).onPreviewAreaChanged(this.mPreviewArea);
        }
    }
}
