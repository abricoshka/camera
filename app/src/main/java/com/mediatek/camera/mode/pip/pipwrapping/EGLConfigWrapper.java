package com.mediatek.camera.mode.pip.pipwrapping;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLDisplay;
import com.mediatek.camera.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class EGLConfigWrapper {
    private static final String TAG = EGLConfigWrapper.class.getSimpleName();
    private EGLConfigChooser mEGLConfigChooser;
    private int mSelectedPixelFormat = -1;
    private ArrayList<Integer> mSupportedFormats = new ArrayList<>();

    private interface EGLConfigChooser {
        EGLConfig chooseConfigEGL14(EGLDisplay eGLDisplay, boolean z);
    }

    private enum EglConfigFormat {
        YUV,
        RGB,
        RGBA;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static EglConfigFormat[] valuesCustom() {
            return values();
        }
    }

    public void setSupportedFormats(int[] iArr) {
        for (int i : iArr) {
            Log.m31d(TAG, "setSupportedFormats,format:" + i);
            this.mSupportedFormats.add(Integer.valueOf(i));
        }
    }

    public EGLConfig chooseConfigEGL14(EGLDisplay eGLDisplay, boolean z) {
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser();
        }
        if (this.mSupportedFormats.size() <= 0) {
            this.mSupportedFormats.add(1);
        }
        return this.mEGLConfigChooser.chooseConfigEGL14(eGLDisplay, z);
    }

    public int getSelectedPixelFormat() {
        return this.mSelectedPixelFormat;
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        abstract EGLConfig chooseConfigEGL14(EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr, int i, boolean z);

        public BaseConfigChooser(int[] iArr) {
            this.mConfigSpec = iArr;
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.EGLConfigWrapper.EGLConfigChooser
        public EGLConfig chooseConfigEGL14(EGLDisplay eGLDisplay, boolean z) {
            EGLConfig[] eGLConfigArr = new EGLConfig[100];
            int[] iArr = new int[1];
            int i = 5;
            if (z) {
                i = 4;
                this.mConfigSpec[this.mConfigSpec.length - 3] = 12610;
                this.mConfigSpec[this.mConfigSpec.length - 2] = 1;
            }
            this.mConfigSpec[this.mConfigSpec.length - 5] = 12339;
            this.mConfigSpec[this.mConfigSpec.length - 4] = i;
            if (!EGL14.eglChooseConfig(eGLDisplay, this.mConfigSpec, 0, eGLConfigArr, 0, eGLConfigArr.length, iArr, 0)) {
                throw new RuntimeException("unable to find ES2 EGL config in EGL14");
            }
            EGLConfig eGLConfigChooseConfigEGL14 = chooseConfigEGL14(eGLDisplay, eGLConfigArr, iArr[0], z);
            if (eGLConfigChooseConfigEGL14 == null) {
                throw new IllegalArgumentException("No config chosen");
            }
            return eGLConfigChooseConfigEGL14;
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {

        /* renamed from: -com-mediatek-camera-mode-pip-pipwrapping-EGLConfigWrapper$EglConfigFormatSwitchesValues */
        private static final /* synthetic */ int[] f71x2340a97a = null;
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;

        /* renamed from: -getcom-mediatek-camera-mode-pip-pipwrapping-EGLConfigWrapper$EglConfigFormatSwitchesValues */
        private static /* synthetic */ int[] m30xb1f6ee1e() {
            if (f71x2340a97a != null) {
                return f71x2340a97a;
            }
            int[] iArr = new int[EglConfigFormat.valuesCustom().length];
            try {
                iArr[EglConfigFormat.RGB.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[EglConfigFormat.RGBA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[EglConfigFormat.YUV.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            f71x2340a97a = iArr;
            return iArr;
        }

        public ComponentSizeChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12352, 4, 12344, 0, 12344, 0, 12344});
            this.mValue = new int[1];
            this.mRedSize = i;
            this.mGreenSize = i2;
            this.mBlueSize = i3;
            this.mAlphaSize = i4;
            this.mDepthSize = i5;
            this.mStencilSize = i6;
            Log.m31d(EGLConfigWrapper.TAG, "R:" + this.mRedSize + ",G:" + this.mGreenSize + "B:" + this.mBlueSize + ",A:" + this.mAlphaSize + "Depth:" + this.mDepthSize + ",Stencil:" + this.mStencilSize);
        }

        @Override // com.mediatek.camera.mode.pip.pipwrapping.EGLConfigWrapper.BaseConfigChooser
        EGLConfig chooseConfigEGL14(EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr, int i, boolean z) {
            EGLConfig eGLConfigFindClosestEglConfig = findClosestEglConfig(eGLDisplay, eGLConfigArr, i, EglConfigFormat.YUV);
            if (eGLConfigFindClosestEglConfig == null) {
                eGLConfigFindClosestEglConfig = findClosestEglConfig(eGLDisplay, eGLConfigArr, i, EglConfigFormat.RGB);
            }
            if (eGLConfigFindClosestEglConfig == null) {
                return findClosestEglConfig(eGLDisplay, eGLConfigArr, i, EglConfigFormat.RGBA);
            }
            return eGLConfigFindClosestEglConfig;
        }

        private int findConfigAttrib(EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            if (EGL14.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue, 0)) {
                return this.mValue[0];
            }
            return i2;
        }

        private EGLConfig findClosestEglConfig(EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr, int i, EglConfigFormat eglConfigFormat) {
            int iAbs;
            EGLConfig eGLConfig;
            EGLConfig eGLConfig2 = null;
            int i2 = 1000;
            int i3 = 0;
            while (i3 < i) {
                int iFindConfigAttrib = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12325, 0);
                int iFindConfigAttrib2 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12326, 0);
                int iFindConfigAttrib3 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12334, 0);
                int iFindConfigAttrib4 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12339, 0);
                if (iFindConfigAttrib < this.mDepthSize || iFindConfigAttrib2 < this.mStencilSize) {
                    iAbs = i2;
                    eGLConfig = eGLConfig2;
                } else {
                    int iFindConfigAttrib5 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12324, 0);
                    int iFindConfigAttrib6 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12323, 0);
                    int iFindConfigAttrib7 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12322, 0);
                    int iFindConfigAttrib8 = findConfigAttrib(eGLDisplay, eGLConfigArr[i3], 12321, 0);
                    iAbs = Math.abs(iFindConfigAttrib5 - this.mRedSize) + Math.abs(iFindConfigAttrib6 - this.mGreenSize) + Math.abs(iFindConfigAttrib7 - this.mBlueSize) + Math.abs(iFindConfigAttrib8 - this.mAlphaSize);
                    Log.m31d(EGLConfigWrapper.TAG, "Try to find EglConfig, want format:" + eglConfigFormat + " r: " + iFindConfigAttrib5 + " g: " + iFindConfigAttrib6 + " b: " + iFindConfigAttrib7 + " a: " + iFindConfigAttrib8 + " visual id = " + iFindConfigAttrib3 + " surfaceType = " + iFindConfigAttrib4 + " depth = " + iFindConfigAttrib + " stencil = " + iFindConfigAttrib2 + " distance = " + iAbs);
                    if (EGLConfigWrapper.this.isInSupportedFormats(iFindConfigAttrib3) && isVisualIdValide(iFindConfigAttrib3, eglConfigFormat) && iAbs < i2) {
                        eGLConfig = eGLConfigArr[i3];
                        EGLConfigWrapper.this.mSelectedPixelFormat = iFindConfigAttrib3;
                    } else {
                        iAbs = i2;
                        eGLConfig = eGLConfig2;
                    }
                }
                i3++;
                eGLConfig2 = eGLConfig;
                i2 = iAbs;
            }
            Log.m31d(EGLConfigWrapper.TAG, "Find format: " + EGLConfigWrapper.this.mSelectedPixelFormat);
            return eGLConfig2;
        }

        private boolean isVisualIdValide(int i, EglConfigFormat eglConfigFormat) {
            switch (m30xb1f6ee1e()[eglConfigFormat.ordinal()]) {
                case 1:
                    return isRGBFormat(i);
                case 2:
                    return i == 1;
                case 3:
                    return isYuvFormat(i);
                default:
                    return false;
            }
        }

        private boolean isYuvFormat(int i) {
            switch (i) {
                case 17:
                case 35:
                case 842094169:
                    return true;
                default:
                    return false;
            }
        }

        private boolean isRGBFormat(int i) {
            switch (i) {
                case 3:
                case 4:
                    return true;
                default:
                    return false;
            }
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser() {
            super(8, 8, 8, 0, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInSupportedFormats(int i) {
        return this.mSupportedFormats.contains(Integer.valueOf(i));
    }
}
