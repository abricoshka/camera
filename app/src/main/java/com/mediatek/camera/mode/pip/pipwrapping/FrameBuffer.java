package com.mediatek.camera.mode.pip.pipwrapping;

import android.opengl.GLES20;
import com.mediatek.camera.util.Log;

/* loaded from: classes.dex */
public class FrameBuffer {
    private final int[] mFboId = {0};
    private final int[] mFboTexId = {0};

    public FrameBuffer() {
        generateFBO();
    }

    public void setRendererSize(int i, int i2) {
        Log.m35v("FrameBuffer", "initFBO width = " + i + " height = " + i2);
        if (i <= 0 || i2 <= 0) {
            return;
        }
        GLUtil.checkGlError("initFBO start");
        GLES20.glBindFramebuffer(36160, this.mFboId[0]);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mFboTexId[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.mFboTexId[0], 0);
        if (GLES20.glCheckFramebufferStatus(36160) != 36053) {
            throw new RuntimeException("glCheckFramebufferStatus() " + GLES20.glCheckFramebufferStatus(36160));
        }
        GLUtil.checkGlError("initFBO end");
    }

    public int getFboTexId() {
        return this.mFboTexId[0];
    }

    public void setupFrameBufferGraphics(int i, int i2) {
        Log.m35v("FrameBuffer", "setupFrameBufferGraphics width = " + i + " height = " + i2);
        GLES20.glBindFramebuffer(36160, this.mFboId[0]);
        GLES20.glViewport(0, 0, i, i2);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(16640);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(1, 771);
        GLUtil.checkEglError("setupFrameBufferGraphics");
    }

    public void setScreenBufferGraphics() {
        GLES20.glBindFramebuffer(36160, 0);
    }

    public void release() {
        Log.m31d("FrameBuffer", "release");
        deleteFBO();
    }

    private void generateFBO() {
        GLES20.glGenFramebuffers(1, this.mFboId, 0);
        GLES20.glGenTextures(1, this.mFboTexId, 0);
        Log.m31d("FrameBuffer", "FrameBuffer glGenTextures texture num = 1");
    }

    private void deleteFBO() {
        GLES20.glDeleteFramebuffers(1, this.mFboId, 0);
        GLES20.glDeleteTextures(1, this.mFboTexId, 0);
        Log.m31d("FrameBuffer", "FrameBuffer glDeleteTextures texture num = 1");
    }
}
