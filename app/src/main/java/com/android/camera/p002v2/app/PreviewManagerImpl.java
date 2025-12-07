package com.android.camera.p002v2.app;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.camera.p002v2.p003ui.FixedAspectSurfaceView;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class PreviewManagerImpl extends PreviewManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PreviewManagerImpl.class.getSimpleName());
    private final Activity mActivity;
    private FrameLayout mCurSurfaceViewLayout;
    private Surface mSurface;
    private FixedAspectSurfaceView mSurfaceView;
    private View mSurfaceViewCover;
    private View.OnTouchListener mTouchListener;
    private boolean mSurfaceAvailable = false;
    private double mPreviewAspectRatio = 0.0d;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private Handler mHandler = new Handler();
    private BlockingQueue<FrameLayout> mFrameLayoutQueue = new LinkedBlockingQueue();
    private SurfaceHolder.Callback mSurfaceViewCallback = new SurfaceHolder.Callback() { // from class: com.android.camera.v2.app.PreviewManagerImpl.1
        @Override // android.view.SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
            LogHelper.m26i(PreviewManagerImpl.TAG, "surfaceCreated mPreviewWidth = " + surfaceFrame.width() + " mPreviewHeight = " + surfaceFrame.height());
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            LogHelper.m26i(PreviewManagerImpl.TAG, "surfaceChanged width = " + i2 + " height = " + i3);
            if (PreviewManagerImpl.this.mSurfaceCallback != null && i2 == PreviewManagerImpl.this.mPreviewWidth && i3 == PreviewManagerImpl.this.mPreviewHeight) {
                PreviewManagerImpl.this.mSurface = surfaceHolder.getSurface();
                if (PreviewManagerImpl.this.mSurfaceAvailable) {
                    PreviewManagerImpl.this.mSurfaceCallback.surfaceSizeChanged(PreviewManagerImpl.this.mSurface, i2, i3);
                } else {
                    PreviewManagerImpl.this.mSurfaceAvailable = true;
                    PreviewManagerImpl.this.mSurfaceCallback.surfaceAvailable(PreviewManagerImpl.this.mSurface, i2, i3);
                }
            }
        }

        @Override // android.view.SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogHelper.m26i(PreviewManagerImpl.TAG, "surfaceDestroyed");
            if (PreviewManagerImpl.this.mSurfaceCallback != null) {
                PreviewManagerImpl.this.mSurfaceCallback.surfaceDestroyed(surfaceHolder.getSurface());
            }
        }
    };
    private View.OnLayoutChangeListener mOnLayoutChangeCallback = new View.OnLayoutChangeListener() { // from class: com.android.camera.v2.app.PreviewManagerImpl.2
        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            LogHelper.m26i(PreviewManagerImpl.TAG, "onLayoutChange left = " + i + " top = " + i2 + " width = " + (i3 - i) + " height = " + (i4 - i2));
            PreviewManagerImpl.this.mPreviewArea.set(i, i2, i3, i4);
            PreviewManagerImpl.this.mSurfaceView.post(new Runnable() { // from class: com.android.camera.v2.app.PreviewManagerImpl.2.1
                @Override // java.lang.Runnable
                public void run() {
                    PreviewManagerImpl.this.notifyPreviewAreaChanged();
                }
            });
            if (PreviewManagerImpl.this.mOnLayoutChangeListener != null) {
                PreviewManagerImpl.this.mOnLayoutChangeListener.onLayoutChange(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        }
    };
    private Runnable mDetachRunnable = new Runnable() { // from class: com.android.camera.v2.app.PreviewManagerImpl.3
        @Override // java.lang.Runnable
        public void run() {
            PreviewManagerImpl.this.mSurfaceViewCover = ((FrameLayout) PreviewManagerImpl.this.mActivity.findViewById(R.id.camera_view_container)).findViewById(R.id.camera_cover);
            if (PreviewManagerImpl.this.mSurfaceViewCover != null && PreviewManagerImpl.this.mSurfaceViewCover.getVisibility() != 4) {
                if (PreviewManagerImpl.this.mSurfaceView != null) {
                    PreviewManagerImpl.this.mSurfaceView.expand();
                }
                PreviewManagerImpl.this.mSurfaceViewCover.setVisibility(4);
            }
            int size = PreviewManagerImpl.this.mFrameLayoutQueue.size();
            for (int i = 0; i < size; i++) {
                PreviewManagerImpl.this.detachSurfaceViewLayout((FrameLayout) PreviewManagerImpl.this.mFrameLayoutQueue.poll());
            }
        }
    };

    public PreviewManagerImpl(Activity activity) {
        this.mActivity = activity;
    }

    @Override // com.android.camera.p002v2.app.PreviewManager
    public void updatePreviewSize(int i, int i2) {
        Assert.assertTrue(i > 0 && i2 > 0);
        if (this.mPreviewWidth == i && this.mPreviewHeight == i2) {
            LogHelper.m26i(TAG, "setPreviewSize skip : width = " + i + " height = " + i2);
            return;
        }
        double d = i / i2;
        this.mSurfaceView = null;
        this.mPreviewWidth = i;
        this.mPreviewHeight = i2;
        this.mPreviewAspectRatio = Math.max(i, i2) / Math.min(i, i2);
        if (this.mSurfaceView == null) {
            attachSurfaceViewLayout();
            this.mSurfaceAvailable = false;
        }
        this.mSurfaceView.getHolder().setFixedSize(this.mPreviewWidth, this.mPreviewHeight);
        this.mSurfaceView.setAspectRatio(this.mPreviewAspectRatio);
    }

    @Override // com.android.camera.p002v2.app.PreviewManager
    public void setGestureListener(View.OnTouchListener onTouchListener) {
        this.mTouchListener = onTouchListener;
    }

    @Override // com.android.camera.p002v2.app.PreviewManager
    public void onPreviewStarted() {
        this.mHandler.removeCallbacks(this.mDetachRunnable);
        this.mHandler.post(this.mDetachRunnable);
    }

    @Override // com.android.camera.p002v2.app.PreviewManager
    public void pause() {
        super.pause();
        if (this.mSurfaceViewCover != null && this.mSurfaceViewCover.getVisibility() != 0) {
            this.mSurfaceViewCover.setVisibility(0);
        }
        if (this.mSurfaceView != null) {
            this.mSurfaceView.shrink();
        }
    }

    @Override // com.android.camera.p002v2.app.PreviewManager
    public void resume() {
        super.resume();
    }

    private void attachSurfaceViewLayout() {
        LogHelper.m26i(TAG, "[attachSurfaceViewLayout]...");
        if (this.mSurfaceView == null) {
            if (this.mCurSurfaceViewLayout != null) {
                this.mFrameLayoutQueue.add(this.mCurSurfaceViewLayout);
            }
            FrameLayout frameLayout = (FrameLayout) this.mActivity.findViewById(R.id.camera_preview_container);
            this.mCurSurfaceViewLayout = (FrameLayout) this.mActivity.getLayoutInflater().inflate(R.layout.camera_previewsurfaceview_layout, (ViewGroup) null);
            this.mSurfaceView = (FixedAspectSurfaceView) this.mCurSurfaceViewLayout.findViewById(R.id.previewsurfaceview);
            this.mSurfaceView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.camera.v2.app.PreviewManagerImpl.4
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    PreviewManagerImpl.this.mTouchListener.onTouch(view, motionEvent);
                    return true;
                }
            });
            this.mSurfaceView.addOnLayoutChangeListener(this.mOnLayoutChangeCallback);
            this.mSurfaceView.getHolder().addCallback(this.mSurfaceViewCallback);
            frameLayout.addView(this.mCurSurfaceViewLayout);
            frameLayout.setVisibility(0);
            this.mSurfaceView.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void detachSurfaceViewLayout(FrameLayout frameLayout) {
        LogHelper.m26i(TAG, "detachSurfaceViewLayout frameLayout = " + frameLayout);
        if (frameLayout != null) {
            ((FrameLayout) this.mActivity.findViewById(R.id.camera_preview_container)).removeViewInLayout(frameLayout);
            frameLayout.setVisibility(8);
        }
    }
}
