package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.opengl.GLES20;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class FrameBuffer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FrameBuffer.class.getSimpleName());
    private final int[] mFboId = {0};
    private final int[] mFboTexId = {0};

    public void init() {
        generateFbo();
    }

    public void setRendererSize(int i, int i2) {
        LogHelper.m23d(TAG, "initFBO width = " + i + " height = " + i2);
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
        if (PipEGLConfigWrapper.getInstance().getPixelFormat() == 1) {
            GLES20.glTexImage2D(3553, 0, 6408, i, i2, 0, 6408, 5121, null);
        } else if (PipEGLConfigWrapper.getInstance().getPixelFormat() == 4) {
            GLES20.glTexImage2D(3553, 0, 6407, i, i2, 0, 6407, 33635, null);
        }
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
        GLUtil.checkGlError("[setupFrameBufferGraphics]+");
        LogHelper.m27v(TAG, "setupFrameBufferGraphics width = " + i + " height = " + i2);
        GLES20.glBindFramebuffer(36160, this.mFboId[0]);
        GLUtil.checkGlError("[setupFrameBufferGraphics]+ glBindFramebuffer");
        GLES20.glViewport(0, 0, i, i2);
        GLUtil.checkGlError("[setupFrameBufferGraphics]+ glViewport");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLUtil.checkGlError("[setupFrameBufferGraphics] glClearColor");
        GLES20.glClear(16640);
        GLUtil.checkGlError("[setupFrameBufferGraphics] glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT)");
        GLES20.glEnable(3042);
        GLUtil.checkGlError("[setupFrameBufferGraphics] glEnable");
        GLES20.glBlendFunc(1, 771);
        GLUtil.checkGlError("[setupFrameBufferGraphics]-");
        GLUtil.checkEglError("setupFrameBufferGraphics");
    }

    public void setScreenBufferGraphics() {
        GLES20.glBindFramebuffer(36160, 0);
    }

    public void unInit() {
        LogHelper.m23d(TAG, "[unInit]+");
        deleteFBO();
        LogHelper.m23d(TAG, "[unInit]-");
    }

    private void generateFbo() {
        GLES20.glGenFramebuffers(1, this.mFboId, 0);
        GLES20.glGenTextures(1, this.mFboTexId, 0);
        LogHelper.m23d(TAG, "FrameBuffer glGenTextures texture num = 1");
    }

    private void deleteFBO() {
        GLES20.glDeleteFramebuffers(1, this.mFboId, 0);
        GLES20.glDeleteTextures(1, this.mFboTexId, 0);
    }
}
