package com.mediatek.camera.p005v2.setting;

import java.util.List;

/* loaded from: classes.dex */
public interface ISettingRule {
    void addLimitation(String str, List<String> list);

    void execute();
}
