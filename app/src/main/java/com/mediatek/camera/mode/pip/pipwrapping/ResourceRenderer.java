package com.mediatek.camera.mode.pip.pipwrapping;

import android.app.Activity;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.mediatek.camera.util.Log;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class ResourceRenderer extends Renderer {
    final String mFragmentShader;
    private float[] mMMtx;
    private float[] mPMtx;
    private float[] mPosMtx;
    private int mProgram;
    private int mResourceId;
    private RectF mResourceRect;
    private int mResourceTexId;
    private FloatBuffer mTexCoordBuf;
    private float[] mVMtx;
    final String mVertexShader;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTexCoordHandle;
    private int muPosMtxHandle;
    private int muResourceSamplerHandle;
    private int muTexRotateMtxHandle;

    public ResourceRenderer(Activity activity) {
        super(activity);
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.maTexCoordHandle = -1;
        this.muPosMtxHandle = -1;
        this.muResourceSamplerHandle = -1;
        this.muTexRotateMtxHandle = -1;
        this.mResourceId = -1;
        this.mResourceTexId = -12345;
        this.mVertexShader = "attribute vec4 aPosition;\nattribute vec4 aTexCoord;\nuniform   mat4 uPosMtx;\nuniform   mat4 uTexRotateMtx;\nvarying   vec2 vTexCoord;\nvoid main() {\n  gl_Position = uPosMtx * aPosition;\n  vTexCoord   = (uTexRotateMtx * aTexCoord).xy;\n}\n";
        this.mFragmentShader = "precision mediump float;\nuniform sampler2D uResourceSampler;\nvarying vec2               vTexCoord;\nvoid main() {\n        gl_FragColor = texture2D(uResourceSampler, vTexCoord);\n}\n";
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
        this.mResourceRect = new RectF();
    }

    public void init() {
        Log.m31d("ResourceRendrer", "initResource");
        initProgram();
    }

    public void updateTemplate(int i) {
        Log.m31d("ResourceRendrer", "updateTemplate resourceId = " + i);
        if (i == this.mResourceId) {
            return;
        }
        releaseResource();
        try {
            this.mResourceTexId = initBitmapTexture(i, false);
        } catch (IOException e) {
            Log.m32e("ResourceRendrer", "initBitmapTexture faile + " + e);
        }
    }

    public void releaseResource() {
        if (this.mResourceTexId > 0) {
            releaseBitmapTexture(this.mResourceTexId);
            this.mResourceTexId = -12345;
        }
    }

    @Override // com.mediatek.camera.mode.pip.pipwrapping.Renderer
    public void setRendererSize(int i, int i2) {
        Log.m31d("ResourceRendrer", "setRendererSize width = " + i + " height = " + i2);
        if (i == getRendererWidth() && i2 == getRendererHeight()) {
            return;
        }
        resetMatrix();
        super.setRendererSize(i, i2);
        Matrix.orthoM(this.mPMtx, 0, 0.0f, getRendererWidth(), 0.0f, getRendererHeight(), -1.0f, 1.0f);
        Matrix.translateM(this.mMMtx, 0, 0.0f, getRendererHeight(), 0.0f);
        Matrix.scaleM(this.mMMtx, 0, this.mMMtx, 0, 1.0f, -1.0f, 1.0f);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mMMtx, 0, this.mVMtx, 0);
        Matrix.multiplyMM(this.mPosMtx, 0, this.mPMtx, 0, this.mPosMtx, 0);
    }

    private void initVertexData(float f, float f2, float f3) {
        Log.m31d("ResourceRendrer", "initVertexData rCenterX = " + f + " rCenterY = " + f2 + " edge = " + f3);
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createSquareVtxByCenterEdge(f, f2, f3));
        this.mResourceRect.set(f - (f3 / 2.0f), f2 - (f3 / 2.0f), (f3 / 2.0f) + f, (f3 / 2.0f) + f2);
        dumpVertex(this.mResourceRect);
    }

    public void draw(float f, float f2, float f3, FloatBuffer floatBuffer, float[] fArr) {
        Log.m31d("ResourceRendrer", "ResourceRendrer draw start rCenterX = " + f + " rCenterY = " + f2 + " edge = " + f3 + " vtxBuf = " + floatBuffer);
        if (getRendererWidth() <= 0 || getRendererHeight() <= 0) {
            return;
        }
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(1, 771);
        GLUtil.checkGlError("ResourceRendrer draw start");
        GLES20.glUseProgram(this.mProgram);
        if (floatBuffer == null) {
            initVertexData(f, f2, f3);
            this.mVtxBuf.position(0);
            GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 12, (Buffer) this.mVtxBuf);
        } else {
            floatBuffer.position(0);
            GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 12, (Buffer) floatBuffer);
        }
        this.mTexCoordBuf.position(0);
        GLES20.glVertexAttribPointer(this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) this.mTexCoordBuf);
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        GLES20.glEnableVertexAttribArray(this.maTexCoordHandle);
        GLES20.glUniformMatrix4fv(this.muPosMtxHandle, 1, false, this.mPosMtx, 0);
        int i = this.muTexRotateMtxHandle;
        if (fArr == null) {
            fArr = GLUtil.createIdentityMtx();
        }
        GLES20.glUniformMatrix4fv(i, 1, false, fArr, 0);
        GLES20.glUniform1i(this.muResourceSamplerHandle, 0);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.mResourceTexId);
        GLES20.glDrawArrays(5, 0, 6);
        GLUtil.checkGlError("ResourceRendrer draw end");
        Log.m31d("ResourceRendrer", "ResourceRendrer draw end");
    }

    private void initProgram() {
        this.mProgram = createProgram("attribute vec4 aPosition;\nattribute vec4 aTexCoord;\nuniform   mat4 uPosMtx;\nuniform   mat4 uTexRotateMtx;\nvarying   vec2 vTexCoord;\nvoid main() {\n  gl_Position = uPosMtx * aPosition;\n  vTexCoord   = (uTexRotateMtx * aTexCoord).xy;\n}\n", "precision mediump float;\nuniform sampler2D uResourceSampler;\nvarying vec2               vTexCoord;\nvoid main() {\n        gl_FragColor = texture2D(uResourceSampler, vTexCoord);\n}\n");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muTexRotateMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexRotateMtx");
        this.muResourceSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uResourceSampler");
    }

    private void dumpVertex(RectF rectF) {
        Log.m31d("ResourceRendrer", "Dump Vertex Animation Rect begin");
        Log.m31d("ResourceRendrer", "(" + rectF.left + " , " + rectF.top + ") , (" + (rectF.left + rectF.width()) + " , " + rectF.top + ")");
        Log.m31d("ResourceRendrer", "(" + rectF.left + " , " + (rectF.top + rectF.height()) + ") , (" + (rectF.left + rectF.width()) + " , " + (rectF.top + rectF.height()) + ")");
        Log.m31d("ResourceRendrer", "Dump Vertex Animation Rect end");
        Log.m31d("ResourceRendrer", "(centerX , centerY) = (" + rectF.centerX() + " , " + rectF.centerY() + ")");
    }

    private void resetMatrix() {
        this.mPosMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
    }
}
