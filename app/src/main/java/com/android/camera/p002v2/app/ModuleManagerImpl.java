package com.android.camera.p002v2.app;

import android.util.SparseArray;
import com.android.camera.p002v2.app.ModuleManager;

/* loaded from: classes.dex */
public class ModuleManagerImpl implements ModuleManager {
    private final SparseArray<ModuleManager.ModuleAgent> mRegisteredModuleAgents = new SparseArray<>(2);
    private int mDefaultModuleId = -1;

    @Override // com.android.camera.p002v2.app.ModuleManager
    public void registerModule(ModuleManager.ModuleAgent moduleAgent) {
        if (moduleAgent == null) {
            throw new NullPointerException("Registering a null ModuleAgent.");
        }
        int moduleId = moduleAgent.getModuleId();
        if (moduleId == -1) {
            throw new IllegalArgumentException("ModuleManager: The module ID can not be MODULE_INDEX_NONE");
        }
        if (this.mRegisteredModuleAgents.get(moduleId) != null) {
            throw new IllegalArgumentException("Module ID is registered already:" + moduleId);
        }
        this.mRegisteredModuleAgents.put(moduleId, moduleAgent);
    }

    @Override // com.android.camera.p002v2.app.ModuleManager
    public ModuleManager.ModuleAgent getModuleAgent(int i) {
        ModuleManager.ModuleAgent moduleAgent = this.mRegisteredModuleAgents.get(i);
        if (moduleAgent == null) {
            return this.mRegisteredModuleAgents.get(this.mDefaultModuleId);
        }
        return moduleAgent;
    }
}
