package com.android.camera.bridge;

import com.android.camera.FeatureSwitcher;
import com.mediatek.camera.platform.IFeatureConfig;

/* loaded from: classes.dex */
public class FeatureConfigImpl implements IFeatureConfig {
    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isVfbEnable() {
        return FeatureSwitcher.isVfbEnable();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isCfbEnable() {
        return FeatureSwitcher.isCfbEnable();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isSlowMotionSupport() {
        return FeatureSwitcher.isSlowMotionSupport();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isGmoRamOptSupport() {
        return FeatureSwitcher.isGmoRAM();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isGmoRomOptSupport() {
        return FeatureSwitcher.isGmoROM();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isLowRamOptSupport() {
        return FeatureSwitcher.isLowRAM();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isMtkFatOnNandSupport() {
        return FeatureSwitcher.isMtkFatOnNand();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isLomoEffectSupport() {
        return FeatureSwitcher.isLomoEffectEnabled();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isDualCameraEnable() {
        return FeatureSwitcher.isDualCameraEnable();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public String whichDeanliChip() {
        return FeatureSwitcher.whichDeanliChip();
    }

    @Override // com.mediatek.camera.platform.IFeatureConfig
    public boolean isZSDHDRSupported() {
        return FeatureSwitcher.isZSDHDRSupported();
    }
}
