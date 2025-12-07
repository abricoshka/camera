package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class GLUtil {
    public static final LogHelper.Tag TAG = new LogHelper.Tag(GLUtil.class.getSimpleName());

    public static float[] createIdentityMtx() {
        float[] fArr = new float[16];
        Matrix.setIdentityM(fArr, 0);
        return fArr;
    }

    public static float[] createFullSquareVtx(float f, float f2) {
        return new float[]{0.0f, 0.0f, 0.0f, 0.0f, f2, 0.0f, f, f2, 0.0f, f, f2, 0.0f, f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    }

    public static float[] createTopRightRect(int i, int i2, float f) {
        int iMin = Math.min(i, i2);
        float f2 = iMin * 0.5f;
        return new float[]{iMin * 0.4f, 0.0f + f, 0.0f, iMin * 0.4f, f2 + f, 0.0f, (iMin * 0.4f) + f2, f2 + f, 0.0f, (iMin * 0.4f) + f2, f2 + f, 0.0f, f2 + (iMin * 0.4f), 0.0f + f, 0.0f, iMin * 0.4f, 0.0f + f, 0.0f};
    }

    public static float[] createTopRightRect(AnimationRect animationRect) {
        return new float[]{animationRect.getLeftTop()[0], animationRect.getLeftTop()[1], 0.0f, animationRect.getLeftBottom()[0], animationRect.getLeftBottom()[1], 0.0f, animationRect.getRightBottom()[0], animationRect.getRightBottom()[1], 0.0f, animationRect.getRightBottom()[0], animationRect.getRightBottom()[1], 0.0f, animationRect.getRightTop()[0], animationRect.getRightTop()[1], 0.0f, animationRect.getLeftTop()[0], animationRect.getLeftTop()[1], 0.0f};
    }

    public static float[] createSquareVtxByCenterEdge(float f, float f2, float f3) {
        return new float[]{f - (f3 / 2.0f), f2 - (f3 / 2.0f), 0.0f, f - (f3 / 2.0f), (f3 / 2.0f) + f2, 0.0f, (f3 / 2.0f) + f, (f3 / 2.0f) + f2, 0.0f, (f3 / 2.0f) + f, (f3 / 2.0f) + f2, 0.0f, (f3 / 2.0f) + f, f2 - (f3 / 2.0f), 0.0f, f - (f3 / 2.0f), f2 - (f3 / 2.0f), 0.0f};
    }

    public static float[] createTexCoord() {
        return new float[]{0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    }

    private static float[] createHorizontalFlipTexCoord(float f, float f2, float f3, float f4) {
        return new float[]{f2, f3, f2, f4, f, f4, f, f4, f, f3, f2, f3};
    }

    public static float[] createTexCoord(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            return createHorizontalFlipTexCoord(f, f2, f3, f4);
        }
        return createTexCoord(f, f2, f3, f4);
    }

    private static float[] createTexCoord(float f, float f2, float f3, float f4) {
        return new float[]{f, f3, f, f4, f2, f4, f2, f4, f2, f3, f, f3};
    }

    public static float[] createStandTexCoord(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            return new float[]{f, f3, f, f4, f2, f4, f2, f4, f2, f3, f, f3};
        }
        return new float[]{f2, f3, f2, f4, f, f4, f, f4, f, f3, f2, f3};
    }

    public static float[] createReverseStandTexCoord(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            return new float[]{f2, f4, f2, f3, f, f3, f, f3, f, f4, f2, f4};
        }
        return new float[]{f, f4, f, f3, f2, f3, f2, f3, f2, f4, f, f4};
    }

    public static float[] createRightTexCoord(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            return new float[]{f, f4, f2, f4, f2, f3, f2, f3, f, f3, f, f4};
        }
        return new float[]{f, f3, f2, f3, f2, f4, f2, f4, f, f4, f, f3};
    }

    public static float[] createLeftTexCoord(float f, float f2, float f3, float f4, boolean z) {
        if (z) {
            return new float[]{f2, f3, f, f3, f, f4, f, f4, f2, f4, f2, f3};
        }
        return new float[]{f2, f4, f, f4, f, f3, f, f3, f2, f3, f2, f4};
    }

    public static int createProgram(String str, String str2) {
        int iLoadShader = loadShader(35633, str);
        int iLoadShader2 = loadShader(35632, str2);
        int iGlCreateProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(iGlCreateProgram, iLoadShader);
        GLES20.glAttachShader(iGlCreateProgram, iLoadShader2);
        GLES20.glLinkProgram(iGlCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
        if (iArr[0] == 1) {
            return iGlCreateProgram;
        }
        LogHelper.m24e(TAG, "Could not link program:");
        LogHelper.m24e(TAG, GLES20.glGetProgramInfoLog(iGlCreateProgram));
        GLES20.glDeleteProgram(iGlCreateProgram);
        return 0;
    }

    public static int loadShader(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        GLES20.glShaderSource(iGlCreateShader, str);
        GLES20.glCompileShader(iGlCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return iGlCreateShader;
        }
        LogHelper.m24e(TAG, "Could not compile shader(TYPE=" + i + "):");
        LogHelper.m24e(TAG, GLES20.glGetShaderInfoLog(iGlCreateShader));
        GLES20.glDeleteShader(iGlCreateShader);
        return 0;
    }

    public static void checkGlError(String str) {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            LogHelper.m26i(TAG, str + ":glGetError:0x" + Integer.toHexString(iGlGetError));
            throw new IllegalStateException("glGetError encountered (see log)");
        }
    }

    public static void checkEglError(String str) {
        int iEglGetError = EGL14.eglGetError();
        if (iEglGetError != 12288) {
            LogHelper.m24e(TAG, str + ":eglGetError:0x" + Integer.toHexString(iEglGetError));
            throw new RuntimeException("eglGetError encountered (see log)");
        }
    }

    public static int[] generateTextureIds(int i) {
        LogHelper.m23d(TAG, "GLUtil glGenTextures num = " + i);
        int[] iArr = new int[i];
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glGenTextures(i, iArr, 0);
        int[] iArr2 = new int[2];
        GLES20.glGetIntegerv(3379, iArr2, 0);
        LogHelper.m23d(TAG, "GL_MAX_TEXTURE_SIZE sizes[0] = " + iArr2[0] + " size[1] = " + iArr2[1]);
        return iArr;
    }

    public static void deleteTextures(int[] iArr) {
        LogHelper.m23d(TAG, "GLUtil glDeleteTextures num = " + iArr.length);
        GLES20.glDeleteTextures(iArr.length, iArr, 0);
    }

    public static void bindPreviewTexure(int i) {
        GLES20.glBindTexture(36197, i);
        GLES20.glTexParameterf(36197, 10241, 9729.0f);
        GLES20.glTexParameterf(36197, 10240, 9729.0f);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
    }
}
