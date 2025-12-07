package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.mediatek.camera.debug.LogHelper;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class BottomGraphicRenderer extends Renderer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(BottomGraphicRenderer.class.getSimpleName());
    final String fragmentShader;
    private float[] mEditMtx;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private int mProgram;
    private FloatBuffer mTexCoordBuf;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muIsPreviewHandle;
    private int muPictureSampleHandle;
    private int muPosMtxHandle;
    private int muPreviewSamplerHandle;
    private int muTexMtxHandle;
    private int muTexRotateMtxHandle;
    final String vertexShader;

    public BottomGraphicRenderer(Activity activity) {
        super(activity);
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mEditMtx = GLUtil.createIdentityMtx();
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.maTexCoordHandle = -1;
        this.muPosMtxHandle = -1;
        this.muTexMtxHandle = -1;
        this.muTexRotateMtxHandle = -1;
        this.muIsPreviewHandle = -1;
        this.muPreviewSamplerHandle = -1;
        this.muPictureSampleHandle = -1;
        this.vertexShader = "attribute vec4    aPosition;\nattribute vec4    aTexCoord;\nuniform   float   uIsPreview;\nuniform   mat4    uPosMtx;\nuniform   mat4    uTexMtx;\nuniform   mat4    uTexRotateMtx;\nvarying   vec2    vTexCoord;\nvarying   float   vfIsPreview;\nvoid main() {\n    gl_Position   = uPosMtx * aPosition;\n    vTexCoord     = (uTexRotateMtx * uTexMtx * aTexCoord).xy;\n    vfIsPreview   = uIsPreview;\n}\n";
        this.fragmentShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform   sampler2D            uPictureSampler;\nuniform   samplerExternalOES   uPreviewSampler;\nvarying   vec2                 vTexCoord;\nvarying   float                vfIsPreview;\nvoid main() {\n    if (vfIsPreview > 0.0) {\n        gl_FragColor = texture2D(uPreviewSampler, vTexCoord);\n    } else { \n        gl_FragColor = texture2D(uPictureSampler, vTexCoord);\n    }\n}\n";
        initProgram();
    }

    public void setRendererSize(int i, int i2, boolean z) {
        LogHelper.m23d(TAG, "setRendererSize width = " + i + " height = " + i2 + " needReverse = " + z);
        resetMatrix();
        super.setRendererSize(i, i2);
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i, 0.0f, i2, -1.0f, 1.0f);
        if (z) {
            Matrix.translateM(this.mMMtx, 0, 0.0f, i2, 0.0f);
            Matrix.scaleM(this.mMMtx, 0, this.mMMtx, 0, 1.0f, -1.0f, 1.0f);
        }
        Matrix.multiplyMM(this.mPosMtx, 0, this.mEditMtx, 0, this.mMMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mVMtx, 0, this.mPosMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mPMtx, 0, this.mPosMtx, 0);
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createFullSquareVtx(i, i2));
    }

    public void draw(int i, float[] fArr, float[] fArr2, boolean z) {
        GLUtil.checkGlError("BottomGraphicRenderer draw start");
        int i2 = fArr == null ? 2 : 1;
        GLES20.glUseProgram(this.mProgram);
        this.mVtxBuf.position(0);
        GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 12, (Buffer) this.mVtxBuf);
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord(0.0f, 1.0f, 0.0f, 1.0f, z));
        this.mTexCoordBuf.position(0);
        GLES20.glVertexAttribPointer(this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) this.mTexCoordBuf);
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        GLES20.glEnableVertexAttribArray(this.maTexCoordHandle);
        GLES20.glUniformMatrix4fv(this.muPosMtxHandle, 1, false, this.mPosMtx, 0);
        GLES20.glUniform1f(this.muIsPreviewHandle, fArr == null ? 0.0f : 1.0f);
        GLES20.glUniformMatrix4fv(this.muTexMtxHandle, 1, false, fArr, 0);
        GLES20.glUniformMatrix4fv(this.muTexRotateMtxHandle, 1, false, fArr2, 0);
        GLES20.glUniform1i(fArr == null ? this.muPictureSampleHandle : this.muPreviewSamplerHandle, i2);
        GLES20.glActiveTexture(33984 + i2);
        GLES20.glBindTexture(fArr == null ? 3553 : 36197, i);
        GLES20.glDrawArrays(5, 0, 6);
        GLUtil.checkGlError("BottomGraphicRenderer draw end");
    }

    private void initProgram() {
        LogHelper.m23d(TAG, "initProgram");
        this.mProgram = createProgram("attribute vec4    aPosition;\nattribute vec4    aTexCoord;\nuniform   float   uIsPreview;\nuniform   mat4    uPosMtx;\nuniform   mat4    uTexMtx;\nuniform   mat4    uTexRotateMtx;\nvarying   vec2    vTexCoord;\nvarying   float   vfIsPreview;\nvoid main() {\n    gl_Position   = uPosMtx * aPosition;\n    vTexCoord     = (uTexRotateMtx * uTexMtx * aTexCoord).xy;\n    vfIsPreview   = uIsPreview;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform   sampler2D            uPictureSampler;\nuniform   samplerExternalOES   uPreviewSampler;\nvarying   vec2                 vTexCoord;\nvarying   float                vfIsPreview;\nvoid main() {\n    if (vfIsPreview > 0.0) {\n        gl_FragColor = texture2D(uPreviewSampler, vTexCoord);\n    } else { \n        gl_FragColor = texture2D(uPictureSampler, vTexCoord);\n    }\n}\n");
        GLUtil.checkGlError("BottomGraphicRenderer after mProgram");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muTexMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexMtx");
        this.muIsPreviewHandle = GLES20.glGetUniformLocation(this.mProgram, "uIsPreview");
        this.muTexRotateMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexRotateMtx");
        this.muPreviewSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uPreviewSampler");
        this.muPictureSampleHandle = GLES20.glGetUniformLocation(this.mProgram, "uPictureSampler");
        GLUtil.checkGlError("BottomGraphicRenderer initProgram");
    }

    private void resetMatrix() {
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mEditMtx = GLUtil.createIdentityMtx();
    }
}
