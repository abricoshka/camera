package com.android.camera.p001ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import com.android.camera.CameraActivity;
import com.android.camera.Log;

/* loaded from: classes.dex */
public class FrameView extends View implements FocusIndicator {
    protected CameraActivity mContext;
    protected int mDisplayOrientation;
    protected boolean mEnableBeauty;
    protected Camera.Face mFace;
    protected Drawable mFaceIndicator;
    protected Drawable[] mFaceStatusIndicator;
    protected Camera.Face[] mFaces;
    protected Matrix mMatrix;
    protected boolean mMirror;
    protected int mOrientation;
    protected boolean mPause;
    protected RectF mRect;
    protected Drawable mTrackIndicator;
    protected Drawable[] mTrackStatusIndicator;

    public FrameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMatrix = new Matrix();
        this.mRect = new RectF();
        this.mContext = (CameraActivity) context;
        this.mFaceStatusIndicator = this.mContext.getFrameManager().getViewDrawable(1);
        this.mTrackStatusIndicator = this.mContext.getFrameManager().getViewDrawable(0);
    }

    public FrameView(Context context) {
        super(context);
        this.mMatrix = new Matrix();
        this.mRect = new RectF();
    }

    public void setDisplayOrientation(int i) {
        this.mDisplayOrientation = i;
        Log.m5d("BoxView", "mDisplayOrientation=" + i);
    }

    public void pause() {
        this.mPause = true;
    }

    public void resume() {
        this.mPause = false;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean faceExists() {
        return false;
    }

    public void showStart() {
    }

    public void showSuccess(boolean z) {
    }

    public void showFail(boolean z) {
    }

    @Override // com.android.camera.p001ui.FocusIndicator
    public void needDistanceInfoShow(boolean z) {
    }

    public void clear() {
    }

    public void setMirror(boolean z) {
        this.mMirror = z;
    }

    public void setFaces(Camera.Face[] faceArr) {
        this.mFaces = faceArr;
    }

    public void enableFaceBeauty(boolean z) {
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
        invalidate();
    }
}
