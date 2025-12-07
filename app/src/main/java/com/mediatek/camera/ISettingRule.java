package com.mediatek.camera;

import java.util.List;

/* loaded from: classes.dex */
public interface ISettingRule {

    public interface MappingFinder {
        String find(String str, List<String> list);
    }

    void addLimitation(String str, List<String> list, MappingFinder mappingFinder);

    void execute();
}
