package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.camera.CameraActivity;
import com.android.camera.Log;
import com.android.camera.Util;

/* loaded from: classes.dex */
public class ObjectView extends FrameView {
    private Runnable mEndAction;
    private float mOldX;
    private float mOldY;
    private Runnable mStartAction;
    private int mZoomInAnimaState;
    private int mZoomOutAnimaState;

    public ObjectView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mStartAction = new StartAction(this, null);
        this.mEndAction = new EndAction(this, 0 == true ? 1 : 0);
        this.mZoomInAnimaState = 0;
        this.mZoomOutAnimaState = 0;
        this.mOldX = 2000.0f;
        this.mOldY = 2000.0f;
        this.mTrackIndicator = this.mTrackStatusIndicator[0];
        this.mContext = (CameraActivity) context;
    }

    @Override // com.android.camera.p001ui.FrameView
    public boolean faceExists() {
        return this.mFace != null;
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showStart() {
        Log.m8i("ObjectView", "showStart()");
        this.mZoomInAnimaState = 1;
        this.mZoomOutAnimaState = 0;
        this.mTrackIndicator = this.mTrackStatusIndicator[0];
        setBackground(this.mTrackIndicator);
        animate().withLayer().setDuration(300L).scaleX(1.5f).scaleY(1.5f).withEndAction(this.mStartAction);
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showSuccess(boolean z) {
        Log.m8i("ObjectView", "showSuccess()");
        this.mZoomOutAnimaState = 1;
        this.mTrackIndicator = this.mTrackStatusIndicator[1];
        setBackground(this.mTrackIndicator);
        animate().withLayer().setDuration(200L).scaleX(0.8f).scaleY(0.8f).withEndAction(this.mEndAction);
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showFail(boolean z) {
        this.mZoomOutAnimaState = 1;
        this.mTrackIndicator = this.mTrackStatusIndicator[2];
        setBackground(this.mTrackIndicator);
        animate().withLayer().setDuration(200L).scaleX(0.8f).scaleY(0.8f).withEndAction(this.mEndAction);
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void clear() {
        Log.m8i("ObjectView", "clear()");
        this.mFace = null;
        resetView();
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView, android.view.View
    protected void onDraw(Canvas canvas) {
        Log.m5d("ObjectView", "onDraw(), mZoomInAnimaState:" + this.mZoomInAnimaState + ", mZoomOutAnimaState:" + this.mZoomOutAnimaState + ", mOrientation:" + this.mOrientation);
        if (this.mZoomOutAnimaState == 2 && this.mZoomInAnimaState == 2) {
            if (this.mFace != null) {
                Log.m8i("ObjectView", "mFace:" + this.mFace);
                if (this.mFace.score != 100) {
                    return;
                }
                this.mTrackIndicator = this.mTrackStatusIndicator[1];
                int unCropWidth = this.mContext.getUnCropWidth();
                int unCropHeight = this.mContext.getUnCropHeight();
                if ((unCropHeight <= unCropWidth || (this.mDisplayOrientation != 0 && this.mDisplayOrientation != 180)) && (unCropHeight >= unCropWidth || (this.mDisplayOrientation != 90 && this.mDisplayOrientation != 270))) {
                    unCropHeight = unCropWidth;
                    unCropWidth = unCropHeight;
                }
                Util.prepareMatrix(this.mMatrix, false, this.mDisplayOrientation, unCropHeight, unCropWidth);
                Matrix matrix = new Matrix();
                float[] fArr = {(getWidth() - unCropHeight) / 2.0f, (getHeight() - unCropWidth) / 2.0f};
                canvas.save();
                this.mMatrix.postRotate(this.mOrientation);
                canvas.rotate(-this.mOrientation);
                matrix.postRotate(this.mContext.getOrientationCompensation());
                matrix.mapPoints(fArr);
                this.mRect.set(this.mFace.rect);
                this.mOldX = calculateMiddlePoint(this.mRect.left, this.mRect.right);
                this.mOldY = calculateMiddlePoint(this.mRect.top, this.mRect.bottom);
                Util.dumpRect(this.mRect, "Original rect");
                this.mMatrix.mapRect(this.mRect);
                Util.dumpRect(this.mRect, "Transformed rect");
                this.mRect.offset(fArr[0], fArr[1]);
                this.mTrackIndicator.setBounds(Math.round(this.mRect.left), Math.round(this.mRect.top), Math.round(this.mRect.right), Math.round(this.mRect.bottom));
                this.mTrackIndicator.draw(canvas);
                canvas.restore();
            }
            super.onDraw(canvas);
        }
    }

    private class StartAction implements Runnable {
        /* synthetic */ StartAction(ObjectView objectView, StartAction startAction) {
            this();
        }

        private StartAction() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ObjectView.this.mZoomInAnimaState = 2;
        }
    }

    private class EndAction implements Runnable {
        /* synthetic */ EndAction(ObjectView objectView, EndAction endAction) {
            this();
        }

        private EndAction() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ObjectView.this.mZoomOutAnimaState = 2;
            ObjectView.this.resetView();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetView() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        setBackground(null);
        animate().cancel();
        setScaleX(1.0f);
        setScaleY(1.0f);
        layoutParams.width = this.mContext.getPreviewFrameWidth();
        layoutParams.height = this.mContext.getPreviewFrameHeight();
        layoutParams.setMargins(0, 0, 0, 0);
    }

    @Override // com.android.camera.p001ui.FrameView
    public void enableFaceBeauty(boolean z) {
        this.mEnableBeauty = false;
    }

    private float calculateMiddlePoint(float f, float f2) {
        return ((f2 - f) / 2.0f) + f;
    }
}
