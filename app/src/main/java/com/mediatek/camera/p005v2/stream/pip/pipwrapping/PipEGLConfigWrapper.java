package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import com.mediatek.camera.debug.LogHelper;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/* loaded from: classes.dex */
public class PipEGLConfigWrapper {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PipEGLConfigWrapper.class.getSimpleName());
    private static PipEGLConfigWrapper sPipEGLConfigWrapper;
    private Bitmap.Config mBitmapConfig;
    private EGLConfigChooser mEGLConfigChooser = new SimpleEGLConfigChooser(false);
    private int mPixelFormat;

    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay);

        android.opengl.EGLConfig chooseConfigEGL14(android.opengl.EGLDisplay eGLDisplay, boolean z);
    }

    public static PipEGLConfigWrapper getInstance() {
        if (sPipEGLConfigWrapper == null) {
            sPipEGLConfigWrapper = new PipEGLConfigWrapper();
        }
        return sPipEGLConfigWrapper;
    }

    private PipEGLConfigWrapper() {
    }

    public EGLConfigChooser getEGLConfigChooser() {
        return this.mEGLConfigChooser;
    }

    public int getPixelFormat() {
        return this.mPixelFormat;
    }

    public Bitmap.Config getBitmapConfig() {
        return this.mBitmapConfig;
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        abstract android.opengl.EGLConfig chooseConfigEGL14(android.opengl.EGLDisplay eGLDisplay, android.opengl.EGLConfig[] eGLConfigArr, int i);

        public BaseConfigChooser(int[] iArr) {
            this.mConfigSpec = iArr;
        }

        @Override // com.mediatek.camera.v2.stream.pip.pipwrapping.PipEGLConfigWrapper.EGLConfigChooser
        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, null, 0, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, eGLConfigArr, i, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig eGLConfigChooseConfig = chooseConfig(egl10, eGLDisplay, eGLConfigArr);
            if (eGLConfigChooseConfig == null) {
                throw new IllegalArgumentException("No config chosen");
            }
            return eGLConfigChooseConfig;
        }

        @Override // com.mediatek.camera.v2.stream.pip.pipwrapping.PipEGLConfigWrapper.EGLConfigChooser
        public android.opengl.EGLConfig chooseConfigEGL14(android.opengl.EGLDisplay eGLDisplay, boolean z) {
            android.opengl.EGLConfig[] eGLConfigArr = new android.opengl.EGLConfig[100];
            int[] iArr = new int[1];
            if (z) {
                this.mConfigSpec[this.mConfigSpec.length - 3] = 12610;
                this.mConfigSpec[this.mConfigSpec.length - 2] = 1;
            }
            if (!EGL14.eglChooseConfig(eGLDisplay, this.mConfigSpec, 0, eGLConfigArr, 0, eGLConfigArr.length, iArr, 0)) {
                throw new RuntimeException("unable to find ES2 EGL config in EGL14");
            }
            android.opengl.EGLConfig eGLConfigChooseConfigEGL14 = chooseConfigEGL14(eGLDisplay, eGLConfigArr, iArr[0]);
            if (eGLConfigChooseConfigEGL14 == null) {
                throw new IllegalArgumentException("No config chosen");
            }
            return eGLConfigChooseConfigEGL14;
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;

        public ComponentSizeChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12352, 4, 12344, 0, 12344});
            this.mValue = new int[1];
            this.mRedSize = i;
            this.mGreenSize = i2;
            this.mBlueSize = i3;
            this.mAlphaSize = i4;
            this.mDepthSize = i5;
            this.mStencilSize = i6;
        }

        @Override // com.mediatek.camera.v2.stream.pip.pipwrapping.PipEGLConfigWrapper.BaseConfigChooser
        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            int iAbs;
            EGLConfig eGLConfig = null;
            int i = 1000;
            int length = eGLConfigArr.length;
            int i2 = 0;
            while (i2 < length) {
                EGLConfig eGLConfig2 = eGLConfigArr[i2];
                int iFindConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12325, 0);
                int iFindConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12326, 0);
                if (iFindConfigAttrib < this.mDepthSize || iFindConfigAttrib2 < this.mStencilSize) {
                    iAbs = i;
                    eGLConfig2 = eGLConfig;
                } else {
                    int iFindConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12324, 0);
                    int iFindConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12323, 0);
                    int iFindConfigAttrib5 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12322, 0);
                    int iFindConfigAttrib6 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12321, 0);
                    iAbs = Math.abs(iFindConfigAttrib3 - this.mRedSize) + Math.abs(iFindConfigAttrib4 - this.mGreenSize) + Math.abs(iFindConfigAttrib5 - this.mBlueSize) + Math.abs(iFindConfigAttrib6 - this.mAlphaSize);
                    LogHelper.m23d(PipEGLConfigWrapper.TAG, "Try choose EGL10: depth = " + iFindConfigAttrib + " stencil = " + iFindConfigAttrib2 + " red = " + iFindConfigAttrib3 + " green = " + iFindConfigAttrib4 + " blue = " + iFindConfigAttrib5 + " alpha = " + iFindConfigAttrib6 + " distance = " + iAbs);
                    if (iAbs < i) {
                        LogHelper.m23d(PipEGLConfigWrapper.TAG, "find closer!");
                    } else {
                        iAbs = i;
                        eGLConfig2 = eGLConfig;
                    }
                }
                i2++;
                i = iAbs;
                eGLConfig = eGLConfig2;
            }
            return eGLConfig;
        }

        @Override // com.mediatek.camera.v2.stream.pip.pipwrapping.PipEGLConfigWrapper.BaseConfigChooser
        android.opengl.EGLConfig chooseConfigEGL14(android.opengl.EGLDisplay eGLDisplay, android.opengl.EGLConfig[] eGLConfigArr, int i) {
            int iAbs;
            android.opengl.EGLConfig eGLConfig;
            android.opengl.EGLConfig eGLConfig2 = null;
            LogHelper.m23d(PipEGLConfigWrapper.TAG, "chooseConfigEGL14 config number = " + i);
            int i2 = 1000;
            int i3 = 0;
            while (i3 < i) {
                int iFindConfigAttrib = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12325, 0);
                int iFindConfigAttrib2 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12326, 0);
                if (iFindConfigAttrib < this.mDepthSize || iFindConfigAttrib2 < this.mStencilSize) {
                    iAbs = i2;
                    eGLConfig = eGLConfig2;
                } else {
                    int iFindConfigAttrib3 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12324, 0);
                    int iFindConfigAttrib4 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12323, 0);
                    int iFindConfigAttrib5 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12322, 0);
                    int iFindConfigAttrib6 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12321, 0);
                    iAbs = Math.abs(iFindConfigAttrib3 - this.mRedSize) + Math.abs(iFindConfigAttrib4 - this.mGreenSize) + Math.abs(iFindConfigAttrib5 - this.mBlueSize) + Math.abs(iFindConfigAttrib6 - this.mAlphaSize);
                    LogHelper.m26i(PipEGLConfigWrapper.TAG, "Try EGL14 choose: depth = " + iFindConfigAttrib + " stencil = " + iFindConfigAttrib2 + " red = " + iFindConfigAttrib3 + " green = " + iFindConfigAttrib4 + " blue = " + iFindConfigAttrib5 + " alpha = " + iFindConfigAttrib6 + " distance = " + iAbs);
                    if (iAbs < i2) {
                        eGLConfig = eGLConfigArr[i3];
                        LogHelper.m26i(PipEGLConfigWrapper.TAG, "find closer!");
                    } else {
                        iAbs = i2;
                        eGLConfig = eGLConfig2;
                    }
                }
                i3++;
                eGLConfig2 = eGLConfig;
                i2 = iAbs;
            }
            int iFindConfigAttrib7 = findConfigAttrib(eGLDisplay, eGLConfig2, 12324, 0);
            int iFindConfigAttrib8 = findConfigAttrib(eGLDisplay, eGLConfig2, 12323, 0);
            int iFindConfigAttrib9 = findConfigAttrib(eGLDisplay, eGLConfig2, 12322, 0);
            int iFindConfigAttrib10 = findConfigAttrib(eGLDisplay, eGLConfig2, 12321, 0);
            if (iFindConfigAttrib7 == 8 && iFindConfigAttrib8 == 8 && iFindConfigAttrib9 == 8 && iFindConfigAttrib10 == 8) {
                PipEGLConfigWrapper.this.mPixelFormat = 1;
                PipEGLConfigWrapper.this.mBitmapConfig = Bitmap.Config.ARGB_8888;
            } else if (iFindConfigAttrib7 == 5 && iFindConfigAttrib8 == 6 && iFindConfigAttrib9 == 5) {
                PipEGLConfigWrapper.this.mPixelFormat = 4;
                PipEGLConfigWrapper.this.mBitmapConfig = Bitmap.Config.RGB_565;
            }
            return eGLConfig2;
        }

        private int findConfigAttrib(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue)) {
                return this.mValue[0];
            }
            return i2;
        }

        private int findConfigAttrib(android.opengl.EGLDisplay eGLDisplay, android.opengl.EGLConfig eGLConfig, int i, int i2) {
            if (EGL14.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue, 0)) {
                return this.mValue[0];
            }
            return i2;
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean z) {
            super(8, 8, 8, 8, z ? 16 : 0, 0);
        }
    }
}
