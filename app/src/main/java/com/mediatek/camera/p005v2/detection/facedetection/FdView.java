package com.mediatek.camera.p005v2.detection.facedetection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class FdView extends View {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdView.class.getSimpleName());
    private volatile boolean mBlocked;
    private int mBufferCenterX;
    private int mBufferCenterY;
    private int mBufferHeight;
    private int mBufferWidth;
    private final Context mContext;
    private int mCropRegionHeight;
    private int mCropRegionLeft;
    private int mCropRegionTop;
    private int mCropRegionWidth;
    private int mDisplayRotation;
    private int mDisplaycompensation;
    private FdUtil mFaceDetectionUtil;
    private Drawable mFaceIndicator;
    private Drawable[] mFaceStatusIndicator;
    private Camera.Face[] mFaces;
    private boolean mIsFbEnabled;
    private int mLastFaceNum;
    private Matrix mMatrix;
    private boolean mMirror;
    private Point mPreviewBeginingPoint;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private RectF mRect;

    public FdView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMirror = false;
        this.mDisplaycompensation = 0;
        this.mPreviewBeginingPoint = new Point();
        this.mDisplayRotation = 0;
        this.mIsFbEnabled = false;
        this.mRect = new RectF();
        this.mMatrix = new Matrix();
        this.mContext = context;
        this.mFaceDetectionUtil = new FdUtil((Activity) context);
        this.mFaceStatusIndicator = this.mFaceDetectionUtil.getViewDrawable();
        this.mFaceIndicator = this.mFaceStatusIndicator[0];
    }

    protected void onPreviewAreaChanged(RectF rectF) {
        this.mPreviewBeginingPoint.x = Math.round(rectF.left);
        this.mPreviewBeginingPoint.y = Math.round(rectF.top);
        this.mBufferWidth = Math.round(rectF.width());
        this.mBufferHeight = Math.round(rectF.height());
        this.mPreviewWidth = Math.round(this.mBufferWidth + (this.mPreviewBeginingPoint.x * 2));
        this.mPreviewHeight = Math.round(this.mBufferHeight + (this.mPreviewBeginingPoint.y * 2));
        this.mBufferCenterX = this.mPreviewBeginingPoint.x + (this.mBufferWidth / 2);
        this.mBufferCenterY = this.mPreviewBeginingPoint.y + (this.mBufferHeight / 2);
    }

    protected void onOrientationChanged(int i) {
        updateDisplayRotation(this.mContext);
    }

    protected void setMirror(boolean z) {
        this.mMirror = z;
    }

    protected void setFaces(int[] iArr, Rect[] rectArr, byte[] bArr, Point[][] pointArr, Rect rect) {
        int length = bArr != null ? bArr.length : 0;
        Camera.Face[] faceArr = new Camera.Face[length];
        this.mCropRegionLeft = rect.left;
        this.mCropRegionTop = rect.top;
        this.mCropRegionWidth = rect.width();
        this.mCropRegionHeight = rect.height();
        if (bArr != null && pointArr != null) {
            for (int i = 0; i < length; i++) {
                Camera.Face face = new Camera.Face();
                if (pointArr[i][0] != null) {
                    face.leftEye = pointArr[i][0];
                }
                if (pointArr[i][1] != null) {
                    face.rightEye = pointArr[i][1];
                }
                if (pointArr[i][2] != null) {
                    face.mouth = pointArr[i][2];
                }
                if (rectArr[i] != null) {
                    face.rect = rectArr[i];
                }
                face.score = bArr[i];
                faceArr[i] = face;
            }
        }
        faceDetected(faceArr);
    }

    protected void setBlockDraw(boolean z) {
        this.mBlocked = z;
    }

    protected void clear() {
        this.mFaces = null;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int iRound;
        float f;
        int iRound2;
        if (!this.mBlocked && this.mFaces != null && this.mFaces.length > 0) {
            int i = this.mBufferWidth > this.mBufferHeight ? this.mBufferWidth : this.mBufferHeight;
            int i2 = this.mBufferWidth > this.mBufferHeight ? this.mBufferHeight : this.mBufferWidth;
            if (i / i2 > this.mCropRegionWidth / this.mCropRegionHeight) {
                f = this.mCropRegionWidth / i;
                iRound2 = Math.round((this.mCropRegionHeight - (i2 * f)) / 2.0f);
                iRound = 0;
            } else {
                float f2 = this.mCropRegionHeight / i2;
                iRound = Math.round((this.mCropRegionWidth - (i * f2)) / 2.0f);
                f = f2;
                iRound2 = 0;
            }
            for (int i3 = 0; i3 < this.mFaces.length; i3++) {
                updateIndicator(this.mFaces[i3].score == 100);
                this.mRect.set(this.mFaces[i3].rect);
                this.mMatrix.reset();
                this.mMatrix.postTranslate(-this.mCropRegionLeft, -this.mCropRegionTop);
                this.mMatrix.postTranslate(-iRound, -iRound2);
                this.mMatrix.postScale(1.0f / f, 1.0f / f);
                this.mMatrix.mapRect(this.mRect);
                rotateFacePosition();
                mirrorFacePosition();
                this.mFaceIndicator.setBounds(this.mFaceDetectionUtil.rectFToRect(this.mRect));
                this.mFaceIndicator.draw(canvas);
            }
            canvas.save();
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    private void faceDetected(Camera.Face[] faceArr) {
        this.mFaces = faceArr;
        if (faceArr != null) {
            int length = this.mFaces.length;
            if (length == 0 && this.mLastFaceNum == 0) {
                return;
            } else {
                this.mLastFaceNum = length;
            }
        }
        invalidate();
    }

    private void updateDisplayRotation(Context context) {
        switch (((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 0:
                this.mDisplayRotation = 0;
                break;
            case 1:
                this.mDisplayRotation = 90;
                break;
            case 2:
                this.mDisplayRotation = 180;
                break;
            case 3:
                this.mDisplayRotation = 270;
                break;
        }
        this.mDisplaycompensation = this.mDisplayRotation;
    }

    private void rotateFacePosition() {
        float f = this.mRect.right - this.mRect.left;
        float f2 = this.mRect.bottom - this.mRect.top;
        if (this.mDisplaycompensation == 0) {
            float f3 = this.mRect.left;
            this.mRect.left = this.mPreviewWidth - this.mRect.bottom;
            this.mRect.top = f3;
            this.mRect.right = f2 + this.mRect.left;
            this.mRect.bottom = f + this.mRect.top;
            this.mRect.offset(this.mPreviewBeginingPoint.x, this.mPreviewBeginingPoint.y);
            return;
        }
        if (this.mDisplaycompensation == 180) {
            this.mRect.left = this.mRect.top;
            this.mRect.top = this.mPreviewHeight - this.mRect.right;
            this.mRect.right = f2 + this.mRect.left;
            this.mRect.bottom = f + this.mRect.top;
            this.mRect.offset(this.mPreviewBeginingPoint.x, this.mPreviewBeginingPoint.y);
            return;
        }
        if (this.mDisplaycompensation == 270) {
            this.mRect.left = this.mPreviewWidth - this.mRect.right;
            this.mRect.top = this.mPreviewHeight - this.mRect.bottom;
            this.mRect.right = f + this.mRect.left;
            this.mRect.bottom = f2 + this.mRect.top;
            this.mRect.offset(-this.mPreviewBeginingPoint.x, -this.mPreviewBeginingPoint.y);
            return;
        }
        if (this.mDisplaycompensation == 90) {
            this.mRect.offset(this.mPreviewBeginingPoint.x, this.mPreviewBeginingPoint.y);
        }
    }

    private void mirrorFacePosition() {
        if (this.mMirror) {
            float f = this.mRect.right - this.mRect.left;
            float f2 = this.mRect.bottom - this.mRect.top;
            if (this.mDisplaycompensation == 90 || this.mDisplaycompensation == 270) {
                this.mRect.left = this.mRect.right + ((this.mBufferCenterX - this.mRect.right) * 2.0f);
                this.mRect.right = f + this.mRect.left;
                return;
            }
            if (this.mDisplaycompensation == 0 || this.mDisplaycompensation == 180) {
                this.mRect.top = this.mRect.bottom + ((this.mBufferCenterY - this.mRect.bottom) * 2.0f);
                this.mRect.bottom = f2 + this.mRect.top;
            }
        }
    }

    private void updateIndicator(boolean z) {
        if (this.mIsFbEnabled && z) {
            this.mFaceIndicator = this.mFaceStatusIndicator[this.mFaceStatusIndicator.length - 1];
        } else {
            this.mFaceIndicator = this.mFaceStatusIndicator[0];
        }
    }
}
