package com.android.camera.p002v2.bridge;

import com.mediatek.camera.p005v2.platform.app.AppContext;
import com.mediatek.camera.p005v2.platform.app.AppController;
import junit.framework.Assert;

/* loaded from: classes.dex */
public class AppContextAdapter {
    private final AppContext mAppContext;

    public AppContextAdapter(AppController appController) {
        Assert.assertNotNull(appController);
        this.mAppContext = appController.getAppContext();
    }

    public void onCreate() {
        this.mAppContext.onCreate();
    }

    public void onResume() {
        this.mAppContext.onResume();
    }

    public void onPause() {
        this.mAppContext.onPause();
    }

    public void onDestroy() {
        this.mAppContext.onDestroy();
    }
}
