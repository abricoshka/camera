package com.android.camera.p002v2.app;

import com.android.camera.p002v2.module.ModuleController;

/* loaded from: classes.dex */
public interface ModuleManager {

    public interface ModuleAgent {
        ModuleController createModule(AppController appController);

        int getModuleId();
    }

    ModuleAgent getModuleAgent(int i);

    void registerModule(ModuleAgent moduleAgent);
}
