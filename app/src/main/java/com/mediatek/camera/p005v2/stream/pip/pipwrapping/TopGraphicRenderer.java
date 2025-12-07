package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class TopGraphicRenderer extends Renderer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(TopGraphicRenderer.class.getSimpleName());
    final String fragmentShader;
    private int mBackTempResId;
    private int mBackTempTexId;
    private float[] mMMtx;
    private float[] mMVPMtx;
    private float[] mPMtx;
    private int mProgram;
    private FloatBuffer mTempTexCoordBuf;
    private FloatBuffer mTexCoordBuf;
    private ResourceRenderer mTopTemplateRenderer;
    private float[] mVMtx;
    private FloatBuffer mVtxBuf;
    private int maPositionHandle;
    private int maTempTexCoordHandle;
    private int maTexCoordHandle;
    private int muBackTempSamplerHandle;
    private int muIsPreviewHandle;
    private int muPictureSampleHandle;
    private int muPosMtxHandle;
    private int muPreviewSamplerHandle;
    private int muTexMtxHandle;
    private int muTexRotateMtxHandle;
    final String vertexShader;

    public TopGraphicRenderer(Activity activity) {
        super(activity);
        this.mMVPMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mBackTempResId = 0;
        this.mBackTempTexId = -12345;
        this.mProgram = -1;
        this.maPositionHandle = -1;
        this.maTexCoordHandle = -1;
        this.maTempTexCoordHandle = -1;
        this.muPosMtxHandle = -1;
        this.muTexMtxHandle = -1;
        this.muTexRotateMtxHandle = -1;
        this.muIsPreviewHandle = -1;
        this.muPictureSampleHandle = -1;
        this.muPreviewSamplerHandle = -1;
        this.muBackTempSamplerHandle = -1;
        this.vertexShader = "attribute vec4   aPosition;\nattribute vec4   aTexCoord;\nattribute vec4   aTempTexCoord;\nuniform   float  uIsPreview;\nuniform   mat4   uPosMtx;\nuniform   mat4   uTexMtx;\nuniform   mat4   uTexRotateMtx;\nvarying   vec2   vTexCoord;\nvarying   vec2   vTempTexCoord;\nvarying   float  vIsPreview;\nvoid main() {\n    gl_Position    = uPosMtx * aPosition;\n    vTexCoord     = (uTexRotateMtx * uTexMtx * aTexCoord).xy;\n    vTempTexCoord  = aTempTexCoord.xy;\n    vIsPreview     = uIsPreview;\n}\n";
        this.fragmentShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform   samplerExternalOES uPreviewSampler;\nuniform   sampler2D uPictureSampler;\nuniform   sampler2D uBackSampler;\nvarying   vec2               vTexCoord;\nvarying   vec2       vTempTexCoord;\nvarying   float  vIsPreview;\nconst vec3 black = vec3(0, 0, 0);  \nvoid main() {\n    vec3 texture1 = vec3(texture2D(uBackSampler,vTempTexCoord).rgb);\n    if((equal(texture1, black)).r) {\n        gl_FragColor = vec4(0, 0, 0, 0);\n    } else {\n        if (vIsPreview > 0.0) {\n            gl_FragColor = texture2D(uPreviewSampler, vTexCoord);\n        } else { \n            gl_FragColor = texture2D(uPictureSampler, vTexCoord);\n        }\n    }\n}\n";
        LogHelper.m23d(TAG, "TopGraphicRenderer");
        initProgram();
        this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createTexCoord());
        this.mTempTexCoordBuf = createFloatBuffer(this.mTempTexCoordBuf, GLUtil.createTexCoord());
        this.mTopTemplateRenderer = new ResourceRenderer(getActivity());
        this.mTopTemplateRenderer.init();
    }

    public void initTemplateTexture(int i, int i2) {
        LogHelper.m23d(TAG, "initTemplateTexture");
        if (this.mBackTempResId != i) {
            if (this.mBackTempTexId > 0) {
                releaseBitmapTexture(this.mBackTempTexId);
                this.mBackTempTexId = -12345;
            }
            if (i > 0) {
                try {
                    this.mBackTempTexId = initBitmapTexture(i, true);
                } catch (IOException e) {
                    LogHelper.m24e(TAG, "initBitmapTexture faile + " + e);
                }
            }
            this.mBackTempResId = i;
        }
        if (this.mTopTemplateRenderer != null && i2 > 0) {
            this.mTopTemplateRenderer.updateTemplate(i2);
        }
    }

    public void setRendererSize(int i, int i2, boolean z) {
        int iMin;
        int i3;
        LogHelper.m23d(TAG, "setRendererSize width = " + i + " height = " + i2);
        if (z) {
            int iMin2 = Math.min(i, i2);
            iMin = Math.max(i, i2);
            i3 = iMin2;
        } else {
            int iMax = Math.max(i, i2);
            iMin = Math.min(i, i2);
            i3 = iMax;
        }
        resetMatrix();
        Matrix.orthoM(this.mPMtx, 0, 0.0f, i3, 0.0f, iMin, -1.0f, 1.0f);
        if (z) {
            Matrix.translateM(this.mMMtx, 0, 0.0f, iMin, 0.0f);
            Matrix.scaleM(this.mMMtx, 0, this.mMMtx, 0, 1.0f, -1.0f, 1.0f);
        }
        Matrix.multiplyMM(this.mMVPMtx, 0, this.mMMtx, 0, this.mMVPMtx, 0);
        Matrix.multiplyMM(this.mMVPMtx, 0, this.mVMtx, 0, this.mMVPMtx, 0);
        Matrix.multiplyMM(this.mMVPMtx, 0, this.mPMtx, 0, this.mMVPMtx, 0);
        super.setRendererSize(i3, iMin);
        this.mTopTemplateRenderer.setRendererSize(i3, iMin, false);
    }

    public void draw(int i, float[] fArr, float[] fArr2, AnimationRect animationRect, int i2, boolean z) {
        float f;
        float f2;
        if (i <= 0 || animationRect == null) {
            return;
        }
        AnimationRect animationRectCopy = animationRect.copy();
        float fCenterX = animationRectCopy.getRectF().centerX();
        float fCenterY = animationRectCopy.getRectF().centerY();
        Renderer.CropBox cropBox = getCropBox();
        animationRectCopy.translate(cropBox.getTranslateXRatio() * animationRectCopy.getRectF().width(), cropBox.getTranslateYRatio() * animationRectCopy.getRectF().height(), false);
        animationRectCopy.rotate(animationRectCopy.getCurrrentRotationValue());
        animationRectCopy.scale(cropBox.getScaleRatio(), false);
        animationRectCopy.rotate(animationRectCopy.getCurrrentRotationValue(), fCenterX, fCenterY);
        GLUtil.checkGlError("TopGraphicRenderer draw start");
        boolean z2 = getRendererWidth() > getRendererHeight();
        int iMax = Math.max(getRendererWidth(), getRendererHeight());
        int iMin = Math.min(getRendererWidth(), getRendererHeight());
        if (!z || i2 % 180 == 0) {
            f = 0.75f;
        } else {
            f = 0.25f;
        }
        float f3 = z2 ? 0.0f : ((iMax - iMin) * (1.0f - f)) / iMax;
        float f4 = z2 ? 1.0f : (((iMax - iMin) * (1.0f - f)) + iMin) / iMax;
        float f5 = z2 ? ((iMax - iMin) * (1.0f - f)) / iMax : 0.0f;
        if (z2) {
            f2 = (((iMax - iMin) * (1.0f - f)) + iMin) / iMax;
        } else {
            f2 = 1.0f;
        }
        switch (i2) {
            case 0:
                this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createReverseStandTexCoord(f5, f2, f3, f4, z));
                break;
            case 90:
                this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createRightTexCoord(f5, f2, f3, f4, z));
                break;
            case 180:
                this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createStandTexCoord(f5, f2, f3, f4, z));
                break;
            case 270:
                this.mTexCoordBuf = createFloatBuffer(this.mTexCoordBuf, GLUtil.createLeftTexCoord(f5, f2, f3, f4, z));
                break;
        }
        GLES20.glUseProgram(this.mProgram);
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createTopRightRect(animationRectCopy));
        this.mVtxBuf.position(0);
        GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 12, (Buffer) this.mVtxBuf);
        this.mTexCoordBuf.position(0);
        GLES20.glVertexAttribPointer(this.maTexCoordHandle, 2, 5126, false, 8, (Buffer) this.mTexCoordBuf);
        this.mTempTexCoordBuf.position(0);
        GLES20.glVertexAttribPointer(this.maTempTexCoordHandle, 2, 5126, false, 8, (Buffer) this.mTempTexCoordBuf);
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        GLES20.glEnableVertexAttribArray(this.maTexCoordHandle);
        GLES20.glEnableVertexAttribArray(this.maTempTexCoordHandle);
        GLES20.glUniformMatrix4fv(this.muPosMtxHandle, 1, false, this.mMVPMtx, 0);
        GLES20.glUniformMatrix4fv(this.muTexMtxHandle, 1, false, fArr == null ? GLUtil.createIdentityMtx() : fArr, 0);
        GLES20.glUniformMatrix4fv(this.muTexRotateMtxHandle, 1, false, fArr2, 0);
        GLES20.glUniform1f(this.muIsPreviewHandle, fArr == null ? 0.0f : 1.0f);
        GLES20.glUniform1i(fArr == null ? this.muPictureSampleHandle : this.muPreviewSamplerHandle, 0);
        GLES20.glUniform1i(this.muBackTempSamplerHandle, 1);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(fArr == null ? 3553 : 36197, i);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, this.mBackTempTexId);
        GLES20.glDrawArrays(5, 0, 6);
        this.mVtxBuf = createFloatBuffer(this.mVtxBuf, GLUtil.createTopRightRect(animationRect));
        this.mTopTemplateRenderer.draw(0.0f, 0.0f, 0.0f, getVtxFloatBuffer());
        GLUtil.checkGlError("TopGraphicRenderer draw end");
    }

    public FloatBuffer getVtxFloatBuffer() {
        return this.mVtxBuf;
    }

    @Override // com.mediatek.camera.p005v2.stream.pip.pipwrapping.Renderer
    public void release() {
        if (this.mBackTempTexId > 0) {
            releaseBitmapTexture(this.mBackTempTexId);
            this.mBackTempTexId = -12345;
        }
        if (this.mTopTemplateRenderer != null) {
            this.mTopTemplateRenderer.releaseResource();
            this.mTopTemplateRenderer = null;
        }
    }

    private void initProgram() {
        this.mProgram = createProgram("attribute vec4   aPosition;\nattribute vec4   aTexCoord;\nattribute vec4   aTempTexCoord;\nuniform   float  uIsPreview;\nuniform   mat4   uPosMtx;\nuniform   mat4   uTexMtx;\nuniform   mat4   uTexRotateMtx;\nvarying   vec2   vTexCoord;\nvarying   vec2   vTempTexCoord;\nvarying   float  vIsPreview;\nvoid main() {\n    gl_Position    = uPosMtx * aPosition;\n    vTexCoord     = (uTexRotateMtx * uTexMtx * aTexCoord).xy;\n    vTempTexCoord  = aTempTexCoord.xy;\n    vIsPreview     = uIsPreview;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nuniform   samplerExternalOES uPreviewSampler;\nuniform   sampler2D uPictureSampler;\nuniform   sampler2D uBackSampler;\nvarying   vec2               vTexCoord;\nvarying   vec2       vTempTexCoord;\nvarying   float  vIsPreview;\nconst vec3 black = vec3(0, 0, 0);  \nvoid main() {\n    vec3 texture1 = vec3(texture2D(uBackSampler,vTempTexCoord).rgb);\n    if((equal(texture1, black)).r) {\n        gl_FragColor = vec4(0, 0, 0, 0);\n    } else {\n        if (vIsPreview > 0.0) {\n            gl_FragColor = texture2D(uPreviewSampler, vTexCoord);\n        } else { \n            gl_FragColor = texture2D(uPictureSampler, vTexCoord);\n        }\n    }\n}\n");
        this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
        this.maTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTexCoord");
        this.maTempTexCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "aTempTexCoord");
        this.muPosMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uPosMtx");
        this.muTexMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexMtx");
        this.muTexRotateMtxHandle = GLES20.glGetUniformLocation(this.mProgram, "uTexRotateMtx");
        this.muIsPreviewHandle = GLES20.glGetUniformLocation(this.mProgram, "uIsPreview");
        this.muPreviewSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uPreviewSampler");
        this.muBackTempSamplerHandle = GLES20.glGetUniformLocation(this.mProgram, "uBackSampler");
        this.muPictureSampleHandle = GLES20.glGetUniformLocation(this.mProgram, "uPictureSampler");
    }

    private void resetMatrix() {
        this.mMVPMtx = GLUtil.createIdentityMtx();
        this.mPMtx = GLUtil.createIdentityMtx();
        this.mVMtx = GLUtil.createIdentityMtx();
        this.mMMtx = GLUtil.createIdentityMtx();
    }
}
