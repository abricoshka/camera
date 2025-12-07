package com.mediatek.camera.p005v2.stream.pip.pipwrapping;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.mediatek.camera.debug.LogHelper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/* loaded from: classes.dex */
public class Renderer {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(Renderer.class.getSimpleName());
    private Activity mActivity;
    private int mRendererWidth = 0;
    private int mRendererHeight = 0;
    private CropBox mCropBox = new CropBox(this, null);
    private int mDrawFrameCount = 0;
    private long mDrawStartTime = 0;

    public Renderer(Activity activity) {
        this.mActivity = activity;
    }

    protected FloatBuffer createFloatBuffer(FloatBuffer floatBuffer, float[] fArr) {
        if (floatBuffer == null) {
            LogHelper.m27v(TAG, "ByteBuffer.allocateDirect");
            floatBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        floatBuffer.clear();
        floatBuffer.put(fArr);
        floatBuffer.position(0);
        return floatBuffer;
    }

    protected void setRendererSize(int i, int i2) {
        this.mRendererWidth = i;
        this.mRendererHeight = i2;
    }

    protected int initBitmapTexture(int i, boolean z) throws Resources.NotFoundException, IOException {
        Bitmap bitmapCreateBitmap;
        int[] iArr = new int[1];
        LogHelper.m23d(TAG, "Renderer initBitmapTexture glGenTextures num = 1");
        GLES20.glGenTextures(1, iArr, 0);
        int i2 = iArr[0];
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtil.checkGlError("initBitmapTexture GL_TEXTURE_MAG_FILTER");
        GLES20.glTexParameteri(3553, 10241, 9985);
        GLUtil.checkGlError("initBitmapTexture GL_TEXTURE_MIN_FILTER");
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        InputStream inputStreamOpenRawResource = this.mActivity.getResources().openRawResource(i);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenRawResource, null, options);
            bitmapDecodeStream.setHasAlpha(false);
            if (z) {
                int width = bitmapDecodeStream.getWidth();
                int height = bitmapDecodeStream.getHeight();
                int i3 = 0;
                int i4 = 0;
                int iRgb = Color.rgb(0, 0, 0);
                long jCurrentTimeMillis = System.currentTimeMillis();
                int i5 = height;
                int i6 = width;
                for (int i7 = 0; i7 < width; i7++) {
                    for (int i8 = 0; i8 < height; i8++) {
                        if (bitmapDecodeStream.getPixel(i7, i8) != iRgb) {
                            if (i6 > i7) {
                                i6 = i7;
                            }
                            if (i3 < i7) {
                                i3 = i7;
                            }
                            if (i5 > i8) {
                                i5 = i8;
                            }
                            if (i4 < i8) {
                                i4 = i8;
                            }
                        }
                    }
                }
                LogHelper.m27v(TAG, "read cousume " + (System.currentTimeMillis() - jCurrentTimeMillis) + "ms bitmap width = " + width + " bitmap height = " + height + " minX = " + i6 + " minY = " + i5 + " maxX = " + i3 + " maxY = " + i4);
                int iMax = Math.max(i3 - i6, i4 - i5);
                float f = ((i6 + (iMax / 2.0f)) - (width / 2.0f)) / width;
                float f2 = ((i5 + (iMax / 2.0f)) - (height / 2.0f)) / height;
                float f3 = iMax / width;
                this.mCropBox.setTranslateXRatio(f);
                this.mCropBox.setTranslateYRatio(f2);
                this.mCropBox.setScaleRatio(f3);
                bitmapCreateBitmap = Bitmap.createBitmap(bitmapDecodeStream, i6, i5, iMax, iMax);
                LogHelper.m27v(TAG, "new bitmap width = " + bitmapCreateBitmap.getWidth() + " height = " + bitmapCreateBitmap.getHeight() + " translateXRatio = " + f + " translateYRatio = " + f2 + " scaleRatio = " + f3);
            } else {
                bitmapCreateBitmap = bitmapDecodeStream;
            }
            GLUtils.texImage2D(3553, 0, bitmapCreateBitmap, 0);
            bitmapCreateBitmap.recycle();
            GLES20.glGenerateMipmap(3553);
            return i2;
        } finally {
            try {
                inputStreamOpenRawResource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void releaseBitmapTexture(int i) {
        GLES20.glDeleteTextures(1, new int[]{i}, 0);
        LogHelper.m23d(TAG, "Renderer releaseBitmapTexture glDeleteTextures num = 1");
    }

    public Activity getActivity() {
        return this.mActivity;
    }

    public int getRendererWidth() {
        return this.mRendererWidth;
    }

    public int getRendererHeight() {
        return this.mRendererHeight;
    }

    public int createProgram(String str, String str2) {
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

    public int loadShader(int i, String str) {
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

    public void debugFrameRate(LogHelper.Tag tag) {
        this.mDrawFrameCount++;
        if (this.mDrawFrameCount % 300 == 0) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            int i = (int) (jCurrentTimeMillis - this.mDrawStartTime);
            LogHelper.m23d(tag, "[Wrapping-->" + tag + "][Preview] Drawing frame, fps = " + ((this.mDrawFrameCount * 1000.0f) / i) + " in last " + i + " millisecond.");
            this.mDrawStartTime = jCurrentTimeMillis;
            this.mDrawFrameCount = 0;
        }
    }

    public CropBox getCropBox() {
        return this.mCropBox;
    }

    class CropBox {
        private float mScaleRatio;
        private float mTranslateXRatio;
        private float mTranslateYRatio;

        /* synthetic */ CropBox(Renderer renderer, CropBox cropBox) {
            this();
        }

        private CropBox() {
            this.mTranslateXRatio = 0.0f;
            this.mTranslateYRatio = 0.0f;
            this.mScaleRatio = 1.0f;
        }

        public void setTranslateXRatio(float f) {
            this.mTranslateXRatio = f;
        }

        public float getTranslateXRatio() {
            return this.mTranslateXRatio;
        }

        public void setTranslateYRatio(float f) {
            this.mTranslateYRatio = f;
        }

        public float getTranslateYRatio() {
            return this.mTranslateYRatio;
        }

        public void setScaleRatio(float f) {
            this.mScaleRatio = f;
        }

        public float getScaleRatio() {
            return this.mScaleRatio;
        }
    }

    public void release() {
    }
}
