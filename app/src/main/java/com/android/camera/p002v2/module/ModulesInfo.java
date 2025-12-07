package com.android.camera.p002v2.module;

import android.content.Context;
import com.android.camera.p002v2.app.AppController;
import com.android.camera.p002v2.app.ModuleManager;
import com.android.camera.p002v2.bridge.ModuleControllerAdapter;

/* loaded from: classes.dex */
public class ModulesInfo {
    public static void setupModules(Context context, ModuleManager moduleManager) {
        registerCameraModule(moduleManager, 0);
        registerDualCameraModule(moduleManager, 1);
    }

    private static void registerCameraModule(ModuleManager moduleManager, final int i) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() { // from class: com.android.camera.v2.module.ModulesInfo.1
            @Override // com.android.camera.v2.app.ModuleManager.ModuleAgent
            public int getModuleId() {
                return i;
            }

            @Override // com.android.camera.v2.app.ModuleManager.ModuleAgent
            public ModuleController createModule(AppController appController) {
                return new ModuleControllerAdapter(appController, i);
            }
        });
    }

    private static void registerDualCameraModule(ModuleManager moduleManager, final int i) {
        moduleManager.registerModule(new ModuleManager.ModuleAgent() { // from class: com.android.camera.v2.module.ModulesInfo.2
            @Override // com.android.camera.v2.app.ModuleManager.ModuleAgent
            public int getModuleId() {
                return i;
            }

            @Override // com.android.camera.v2.app.ModuleManager.ModuleAgent
            public ModuleController createModule(AppController appController) {
                return new ModuleControllerAdapter(appController, i);
            }
        });
    }
}
