package com.mediatek.camera.p005v2.module;

import android.app.Activity;
import android.view.ViewGroup;
import com.mediatek.camera.p005v2.stream.IPreviewStream;

/* loaded from: classes.dex */
public class CameraModuleUi extends AbstractModuleUi {
    public CameraModuleUi(Activity activity, CameraModule cameraModule, ViewGroup viewGroup, IPreviewStream.PreviewCallback previewCallback) {
        super(activity, cameraModule, viewGroup, previewCallback);
    }
}
