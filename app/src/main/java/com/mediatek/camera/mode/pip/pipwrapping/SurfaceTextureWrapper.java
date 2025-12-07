package com.mediatek.camera.mode.pip.pipwrapping;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import com.mediatek.camera.util.Log;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class SurfaceTextureWrapper {
    private static final String TAG = SurfaceTextureWrapper.class.getSimpleName();
    private SurfaceTexture mSurfaceTexture = null;
    private int mWidth = -1;
    private int mHeight = -1;
    private int mTextureId = -12345;
    private float[] mSTTransformMatrix = new float[16];
    private long mSTTimeStamp = 0;

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener, Handler handler) {
        if (this.mSurfaceTexture == null) {
            throw new IllegalStateException("SurfaceTexure not created, pls call setDefaultBufferSize or use SurfaceTextureWrapper(int surfaceTexId) firstly!");
        }
        this.mSurfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener, handler);
    }

    public void setDefaultBufferSize(int i, int i2) {
        Assert.assertTrue(i > 0);
        Assert.assertTrue(i2 > 0);
        if (this.mWidth == i && this.mHeight == i2 && this.mSurfaceTexture != null) {
            Log.m34i(TAG, "skip setDefaultBufferSize w = " + i + " h = " + i2);
            return;
        }
        this.mWidth = i;
        this.mHeight = i2;
        if (this.mSurfaceTexture == null) {
            if (this.mTextureId < 0) {
                this.mTextureId = GLUtil.generateTextureIds(1)[0];
                GLUtil.bindPreviewTexure(this.mTextureId);
            }
            this.mSurfaceTexture = new SurfaceTexture(this.mTextureId);
        }
        this.mSurfaceTexture.setDefaultBufferSize(i, i2);
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getTextureId() {
        return this.mTextureId;
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurfaceTexture;
    }

    public float[] getBufferTransformMatrix() {
        return this.mSTTransformMatrix;
    }

    public long getBufferTimeStamp() {
        return this.mSTTimeStamp;
    }

    public void updateTexImage() {
        if (this.mSurfaceTexture != null) {
            this.mSurfaceTexture.updateTexImage();
            this.mSTTimeStamp = this.mSurfaceTexture.getTimestamp();
            this.mSurfaceTexture.getTransformMatrix(this.mSTTransformMatrix);
        }
    }

    public void resetSTStatus() {
        this.mSTTimeStamp = 0L;
        this.mSTTransformMatrix = new float[16];
    }

    public void release() {
        Log.m31d(TAG, "release");
        resetSTStatus();
        if (this.mSurfaceTexture != null) {
            this.mSurfaceTexture.setOnFrameAvailableListener(null);
            this.mSurfaceTexture.release();
            this.mSurfaceTexture = null;
        }
        this.mWidth = 0;
        this.mHeight = 0;
        if (this.mTextureId >= 0) {
            GLUtil.deleteTextures(new int[]{this.mTextureId});
            this.mTextureId = -12345;
        }
    }
}
