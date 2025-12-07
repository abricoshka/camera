package com.mediatek.camera.p005v2.detection.facedetection;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.mediatek.camera.R;
import com.mediatek.camera.debug.LogHelper;

/* loaded from: classes.dex */
public class FdUtil {
    private Activity mActivity;
    private static final LogHelper.Tag TAG = new LogHelper.Tag(FdUtil.class.getSimpleName());
    private static final int[] FACE_DETECTION_ICON = {R.drawable.ic_face_detection_focusing, R.drawable.ic_face_detection_focused, R.drawable.ic_face_detection_failed};

    public FdUtil(Activity activity) {
        this.mActivity = activity;
    }

    public Drawable[] getViewDrawable() {
        Drawable[] drawableArr = new Drawable[3];
        for (int i = 0; i < 3; i++) {
            drawableArr[i] = this.mActivity.getResources().getDrawable(FACE_DETECTION_ICON[i]);
        }
        return drawableArr;
    }

    public Rect rectFToRect(RectF rectF) {
        Rect rect = new Rect();
        rect.left = Math.round(rectF.left);
        rect.top = Math.round(rectF.top);
        rect.right = Math.round(rectF.right);
        rect.bottom = Math.round(rectF.bottom);
        return rect;
    }
}
