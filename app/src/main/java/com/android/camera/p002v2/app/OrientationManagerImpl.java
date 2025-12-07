package com.android.camera.p002v2.app;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.view.OrientationEventListener;
import com.android.camera.p002v2.app.OrientationManager;
import com.android.camera.p002v2.util.ApiHelper;
import com.mediatek.camera.debug.LogHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class OrientationManagerImpl implements OrientationManager {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(OrientationManagerImpl.class.getSimpleName());
    private final Activity mActivity;
    private final MyOrientationEventListener mOrientationListener;
    private boolean mOrientationLocked = false;
    private boolean mRotationLockedSetting = false;
    private final List<OrientationChangeCallback> mListeners = new ArrayList();

    private static class OrientationChangeCallback {
        private final Handler mHandler;
        private final OrientationManager.OnOrientationChangeListener mListener;

        OrientationChangeCallback(Handler handler, OrientationManager.OnOrientationChangeListener onOrientationChangeListener) {
            this.mHandler = handler;
            this.mListener = onOrientationChangeListener;
        }

        public void postOrientationChangeCallback(final int i) {
            this.mHandler.post(new Runnable() { // from class: com.android.camera.v2.app.OrientationManagerImpl.OrientationChangeCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    OrientationChangeCallback.this.mListener.onOrientationChanged(i);
                }
            });
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof OrientationChangeCallback)) {
                return false;
            }
            OrientationChangeCallback orientationChangeCallback = (OrientationChangeCallback) obj;
            return this.mHandler == orientationChangeCallback.mHandler && this.mListener == orientationChangeCallback.mListener;
        }
    }

    public OrientationManagerImpl(Activity activity) {
        this.mActivity = activity;
        this.mOrientationListener = new MyOrientationEventListener(activity);
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void resume() {
        this.mRotationLockedSetting = Settings.System.getInt(this.mActivity.getContentResolver(), "accelerometer_rotation", 0) != 1;
        this.mOrientationListener.enable();
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void pause() {
        this.mOrientationListener.disable();
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void addOnOrientationChangeListener(Handler handler, OrientationManager.OnOrientationChangeListener onOrientationChangeListener) {
        OrientationChangeCallback orientationChangeCallback = new OrientationChangeCallback(handler, onOrientationChangeListener);
        if (this.mListeners.contains(orientationChangeCallback)) {
            return;
        }
        this.mListeners.add(orientationChangeCallback);
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void removeOnOrientationChangeListener(Handler handler, OrientationManager.OnOrientationChangeListener onOrientationChangeListener) {
        if (!this.mListeners.remove(new OrientationChangeCallback(handler, onOrientationChangeListener))) {
            LogHelper.m27v(TAG, "Removing non-existing listener.");
        }
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void lockOrientation() {
        if (this.mOrientationLocked || this.mRotationLockedSetting) {
            return;
        }
        this.mOrientationLocked = true;
        if (ApiHelper.HAS_ORIENTATION_LOCK) {
            this.mActivity.setRequestedOrientation(14);
        } else {
            this.mActivity.setRequestedOrientation(calculateCurrentScreenOrientation());
        }
    }

    @Override // com.android.camera.p002v2.app.OrientationManager
    public void unlockOrientation() {
        if (!this.mOrientationLocked || this.mRotationLockedSetting) {
            return;
        }
        this.mOrientationLocked = false;
        LogHelper.m23d(TAG, "unlock orientation");
        this.mActivity.setRequestedOrientation(10);
    }

    private int calculateCurrentScreenOrientation() {
        int displayRotation = getDisplayRotation();
        boolean z = displayRotation < 180;
        if (this.mActivity.getResources().getConfiguration().orientation == 2) {
            return z ? 0 : 8;
        }
        if (displayRotation == 90 || displayRotation == 270) {
            z = !z;
        }
        return z ? 1 : 9;
    }

    private class MyOrientationEventListener extends OrientationEventListener {
        private int mRestoreOrientation;

        public MyOrientationEventListener(Context context) {
            super(context);
            this.mRestoreOrientation = 0;
        }

        @Override // android.view.OrientationEventListener
        public void onOrientationChanged(int i) {
            if (i == -1) {
                return;
            }
            int iRoundOrientation = OrientationManagerImpl.roundOrientation(i, this.mRestoreOrientation);
            this.mRestoreOrientation = iRoundOrientation;
            Iterator it = OrientationManagerImpl.this.mListeners.iterator();
            while (it.hasNext()) {
                ((OrientationChangeCallback) it.next()).postOrientationChangeCallback(iRoundOrientation);
            }
        }
    }

    public int getDisplayRotation() {
        return getDisplayRotation(this.mActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int roundOrientation(int i, int i2) {
        boolean z = true;
        if (i2 != -1) {
            int iAbs = Math.abs(i - i2);
            if (Math.min(iAbs, 360 - iAbs) < 50) {
                z = false;
            }
        }
        if (z) {
            return (((i + 45) / 90) * 90) % 360;
        }
        return i2;
    }

    private static int getDisplayRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
        }
        return 0;
    }
}
