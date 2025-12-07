package com.mediatek.camera.p005v2.stream;

import android.os.ConditionVariable;
import android.util.Size;
import android.view.Surface;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.stream.IPreviewStream;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class PreviewStream implements IPreviewStream, IPreviewStream.PreviewCallback {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(PreviewStream.class.getSimpleName());
    private IPreviewStream.PreviewCallback mPreviewCallback;
    private int mPreviewHeight;
    private Surface mPreviewSurface;
    private int mPreviewWidth;
    private IPreviewStream.PreviewStreamCallback mStreamCallback;
    private IPreviewStream.PreviewSurfaceCallback mSurfaceCallback;
    private ConditionVariable mSurfaceReadySync = new ConditionVariable();

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public boolean updatePreviewSize(Size size) {
        LogHelper.m26i(TAG, "[updatePreviewSize]+ size:" + size.getWidth() + " x " + size.getHeight());
        Assert.assertNotNull(size);
        int width = size.getWidth();
        int height = size.getHeight();
        if (width == this.mPreviewWidth && height == this.mPreviewHeight && this.mPreviewSurface != null) {
            if (this.mSurfaceCallback != null) {
                this.mSurfaceCallback.onPreviewSufaceIsReady(false);
                this.mSurfaceCallback = null;
            }
            LogHelper.m26i(TAG, "[updatePreviewSize]- with the same preview size");
            return false;
        }
        this.mPreviewSurface = null;
        this.mSurfaceReadySync.close();
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        LogHelper.m26i(TAG, "[updatePreviewSize]-");
        return true;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public Map<String, Surface> getPreviewInputSurfaces() {
        LogHelper.m26i(TAG, "[getPreviewInputSurfaces] +");
        HashMap map = new HashMap();
        if (this.mPreviewSurface == null) {
            this.mSurfaceReadySync.block(2500L);
        }
        map.put("PreviewStream.Surface", this.mPreviewSurface);
        LogHelper.m26i(TAG, "[getPreviewInputSurfaces]- mPreviewSurface:" + this.mPreviewSurface);
        return map;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setPreviewStreamCallback(IPreviewStream.PreviewStreamCallback previewStreamCallback) {
        this.mStreamCallback = previewStreamCallback;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setOneShotPreviewSurfaceCallback(IPreviewStream.PreviewSurfaceCallback previewSurfaceCallback) {
        this.mSurfaceCallback = previewSurfaceCallback;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void setPreviewCallback(IPreviewStream.PreviewCallback previewCallback) {
        this.mPreviewCallback = previewCallback;
    }

    @Override // com.mediatek.camera.p005v2.stream.IPreviewStream
    public void onFirstFrameAvailable() {
        LogHelper.m26i(TAG, "onFirstFrameAvailable mStreamCallback:" + this.mStreamCallback);
        if (this.mStreamCallback != null) {
            this.mStreamCallback.onFirstFrameAvailable();
        }
    }

    @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
    public void surfaceAvailable(Surface surface, int i, int i2) {
        LogHelper.m26i(TAG, "surfaceAvailable surface = " + surface + " width = " + i + " height = " + i2);
        if (i == this.mPreviewWidth && i2 == this.mPreviewHeight) {
            this.mPreviewSurface = surface;
            this.mSurfaceReadySync.open();
            if (this.mSurfaceCallback != null) {
                this.mSurfaceCallback.onPreviewSufaceIsReady(true);
                this.mSurfaceCallback = null;
            }
            if (this.mPreviewCallback != null) {
                this.mPreviewCallback.surfaceAvailable(surface, i, i2);
            }
        }
    }

    @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
    public void surfaceSizeChanged(Surface surface, int i, int i2) {
        LogHelper.m26i(TAG, "surfaceSizeChanged surface = " + surface + " width = " + i + " height = " + i2);
        if (i == this.mPreviewWidth && i2 == this.mPreviewHeight) {
            this.mPreviewSurface = surface;
            this.mSurfaceReadySync.open();
            if (this.mSurfaceCallback != null) {
                this.mSurfaceCallback.onPreviewSufaceIsReady(true);
                this.mSurfaceCallback = null;
            }
            if (this.mPreviewCallback != null) {
                this.mPreviewCallback.surfaceSizeChanged(surface, i, i2);
                return;
            }
            return;
        }
        if (this.mPreviewSurface == null && this.mPreviewWidth == 0 && this.mPreviewHeight == 0) {
            this.mPreviewSurface = surface;
            this.mPreviewWidth = i;
            this.mPreviewHeight = i2;
            if (this.mPreviewCallback != null) {
                this.mPreviewCallback.surfaceSizeChanged(surface, i, i2);
            }
        }
    }

    @Override // com.mediatek.camera.v2.stream.IPreviewStream.PreviewCallback
    public void surfaceDestroyed(Surface surface) {
        LogHelper.m26i(TAG, "surfaceDestroyed surface = " + surface + ",mPreviewSurface:" + this.mPreviewSurface + ",mPreviewCallback:" + this.mPreviewCallback);
        if (surface == this.mPreviewSurface) {
            if (this.mPreviewCallback != null) {
                this.mPreviewCallback.surfaceDestroyed(surface);
            }
            this.mPreviewSurface = null;
            this.mPreviewWidth = 0;
            this.mPreviewHeight = 0;
        }
    }

    public void releasePreviewStream() {
    }
}
