package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import com.android.camera.FeatureSwitcher;
import com.android.camera.Log;
import com.android.camera.Util;

/* loaded from: classes.dex */
public class FaceView extends FrameView {
    private int mLastFaceNum;

    public FaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFaceIndicator = this.mFaceStatusIndicator[0];
    }

    @Override // com.android.camera.p001ui.FrameView
    public void setFaces(Camera.Face[] faceArr) {
        int length = faceArr.length;
        if (this.mPause) {
            return;
        }
        if (length == 0 && this.mLastFaceNum == 0) {
            return;
        }
        this.mFaces = faceArr;
        this.mLastFaceNum = length;
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView
    public void setMirror(boolean z) {
        this.mMirror = z;
        Log.m5d("FaceView", "mMirror=" + z);
    }

    @Override // com.android.camera.p001ui.FrameView
    public boolean faceExists() {
        return this.mFaces != null && this.mFaces.length > 0;
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showStart() {
        this.mFaceIndicator = this.mFaceStatusIndicator[0];
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showSuccess(boolean z) {
        this.mFaceIndicator = this.mFaceStatusIndicator[1];
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void showFail(boolean z) {
        this.mFaceIndicator = this.mFaceStatusIndicator[2];
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView, com.android.camera.p001ui.FocusIndicator
    public void clear() {
        this.mFaceIndicator = this.mFaceStatusIndicator[0];
        this.mFaces = null;
        invalidate();
    }

    @Override // com.android.camera.p001ui.FrameView
    public void enableFaceBeauty(boolean z) {
        this.mEnableBeauty = z;
        if (!this.mEnableBeauty) {
            this.mFaceIndicator = this.mFaceStatusIndicator[0];
        }
    }

    @Override // com.android.camera.p001ui.FrameView, android.view.View
    protected void onDraw(Canvas canvas) {
        Log.m5d("FaceView", "onDraw,mEnableBeauty  =" + this.mEnableBeauty + ",mFaces = " + this.mFaces);
        if (this.mFaces != null && this.mFaces.length > 0) {
            if (this.mEnableBeauty && this.mFaceIndicator == this.mFaceStatusIndicator[0]) {
                this.mFaceIndicator = this.mFaceStatusIndicator[3];
            }
            int unCropWidth = this.mContext.getUnCropWidth();
            int unCropHeight = this.mContext.getUnCropHeight();
            if ((unCropHeight <= unCropWidth || (this.mDisplayOrientation != 0 && this.mDisplayOrientation != 180)) && (unCropHeight >= unCropWidth || (this.mDisplayOrientation != 90 && this.mDisplayOrientation != 270))) {
                unCropHeight = unCropWidth;
                unCropWidth = unCropHeight;
            }
            Util.prepareMatrix(this.mMatrix, this.mMirror, this.mDisplayOrientation, unCropHeight, unCropWidth);
            Matrix matrix = new Matrix();
            float[] fArr = {(getWidth() - unCropHeight) / 2.0f, (getHeight() - unCropWidth) / 2.0f};
            canvas.save();
            this.mMatrix.postRotate(this.mOrientation);
            canvas.rotate(-this.mOrientation);
            matrix.postRotate(this.mContext.getOrientationCompensation());
            matrix.mapPoints(fArr);
            for (int i = 0; i < this.mFaces.length; i++) {
                this.mRect.set(this.mFaces[i].rect);
                Util.dumpRect(this.mRect, "Original rect");
                this.mMatrix.mapRect(this.mRect);
                Util.dumpRect(this.mRect, "Transformed rect");
                if (this.mEnableBeauty && FeatureSwitcher.isVfbEnable()) {
                    if (this.mFaces[i].score == 100) {
                        this.mFaceIndicator = this.mFaceStatusIndicator[3];
                    } else {
                        this.mFaceIndicator = this.mFaceStatusIndicator[0];
                    }
                }
                this.mRect.offset(fArr[0], fArr[1]);
                this.mFaceIndicator.setBounds(Math.round(this.mRect.left), Math.round(this.mRect.top), Math.round(this.mRect.right), Math.round(this.mRect.bottom));
                this.mFaceIndicator.draw(canvas);
            }
            canvas.restore();
        }
        super.onDraw(canvas);
    }
}
