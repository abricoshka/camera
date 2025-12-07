package com.android.camera.p002v2.app;

import android.view.MotionEvent;
import com.android.camera.p002v2.p003ui.PreviewStatusListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public abstract class GestureManager {
    private PreviewStatusListener.OnGestureListener mPreviewGestureListener;
    private boolean mTouchEventsNeedintercept;
    protected final GestureNotifier mGestureNotifier = new GestureNotifier();
    private final ArrayList<PreviewStatusListener.OnPreviewTouchedListener> mPreviewTouchListeners = new ArrayList<>();
    private boolean mScrollEnable = true;
    protected int mGsensorOrientation = 0;

    public void registerPreviewTouchListener(PreviewStatusListener.OnPreviewTouchedListener onPreviewTouchedListener) {
        if (onPreviewTouchedListener != null && (!this.mPreviewTouchListeners.contains(onPreviewTouchedListener))) {
            this.mPreviewTouchListeners.add(onPreviewTouchedListener);
        }
    }

    public void setPreviewGestureListener(PreviewStatusListener.OnGestureListener onGestureListener) {
        this.mPreviewGestureListener = onGestureListener;
    }

    public void setScrollEnable(boolean z) {
        this.mScrollEnable = z;
    }

    public void onOrientationChanged(int i) {
        this.mGsensorOrientation = i;
    }

    protected class GestureNotifier {
        public GestureNotifier() {
        }

        public boolean onDown(float f, float f2) {
            for (PreviewStatusListener.OnPreviewTouchedListener onPreviewTouchedListener : GestureManager.this.mPreviewTouchListeners) {
                GestureManager.this.mTouchEventsNeedintercept = !onPreviewTouchedListener.onPreviewTouched() ? GestureManager.this.mTouchEventsNeedintercept : true;
            }
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            return GestureManager.this.mPreviewGestureListener != null && GestureManager.this.mPreviewGestureListener.onDown(f, f2);
        }

        public boolean onUp() {
            GestureManager.this.mTouchEventsNeedintercept = false;
            if (GestureManager.this.mPreviewGestureListener != null) {
                return GestureManager.this.mPreviewGestureListener.onUp();
            }
            return false;
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            boolean zOnFling = false;
            if (GestureManager.this.mPreviewGestureListener != null) {
                zOnFling = GestureManager.this.mPreviewGestureListener.onFling(motionEvent, motionEvent2, f, f2);
            }
            if (zOnFling) {
                return true;
            }
            return !GestureManager.this.mScrollEnable;
        }

        public boolean onScroll(float f, float f2, float f3, float f4) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            boolean zOnScroll = false;
            if (GestureManager.this.mPreviewGestureListener != null) {
                zOnScroll = GestureManager.this.mPreviewGestureListener.onScroll(f, f2, f3, f4);
            }
            if (zOnScroll) {
                return true;
            }
            return !GestureManager.this.mScrollEnable;
        }

        public boolean onSingleTapUp(float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onSingleTapUp(f, f2);
        }

        public boolean onSingleTapConfirmed(float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onSingleTapConfirmed(f, f2);
        }

        public boolean onDoubleTap(float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onDoubleTap(f, f2);
        }

        public boolean onScale(float f, float f2, float f3) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onScale(f, f2, f3);
        }

        public boolean onScaleBegin(float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onScaleBegin(f, f2);
        }

        public boolean onLongPress(float f, float f2) {
            if (GestureManager.this.mTouchEventsNeedintercept) {
                return true;
            }
            if (GestureManager.this.mPreviewGestureListener == null) {
                return false;
            }
            return GestureManager.this.mPreviewGestureListener.onLongPress(f, f2);
        }
    }
}
