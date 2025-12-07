package com.mediatek.camera.mode;

import android.graphics.SurfaceTexture;
import com.mediatek.camera.ICameraMode;

/* loaded from: classes.dex */
public class DummyMode implements ICameraMode {
    @Override // com.mediatek.camera.ICameraMode
    public boolean open() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean close() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean execute(ICameraMode.ActionType actionType, Object... objArr) {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public void resume() {
    }

    @Override // com.mediatek.camera.ICameraMode
    public void pause() {
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isDisplayUseSurfaceView() {
        return true;
    }

    @Override // com.mediatek.camera.ICameraMode
    public SurfaceTexture getBottomSurfaceTexture() {
        return null;
    }

    @Override // com.mediatek.camera.ICameraMode
    public SurfaceTexture getTopSurfaceTexture() {
        return null;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isNeedDualCamera() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public boolean isDeviceUseSurfaceView() {
        return false;
    }

    @Override // com.mediatek.camera.ICameraMode
    public ICameraMode.ModeState getModeState() {
        return ICameraMode.ModeState.STATE_UNKNOWN;
    }
}
