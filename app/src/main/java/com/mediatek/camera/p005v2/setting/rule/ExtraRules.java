package com.mediatek.camera.p005v2.setting.rule;

import android.util.Size;
import com.mediatek.camera.debug.LogHelper;
import com.mediatek.camera.p005v2.setting.ISettingRule;
import com.mediatek.camera.p005v2.setting.SettingCtrl;
import com.mediatek.camera.p005v2.setting.SettingItem;
import com.mediatek.camera.p005v2.util.Utils;
import java.util.List;

/* loaded from: classes.dex */
public class ExtraRules {
    private static final LogHelper.Tag TAG = new LogHelper.Tag(ExtraRules.class.getSimpleName());
    private SettingCtrl mSettingCtrl;

    public ExtraRules(SettingCtrl settingCtrl) {
        this.mSettingCtrl = settingCtrl;
    }

    public void createRules() {
        this.mSettingCtrl.addRule("pref_camera_picturesize_ratio_key", "pref_camera_picturesize_key", new PictureRatioSizeRule(this, null));
    }

    private class PictureRatioSizeRule implements ISettingRule {
        /* synthetic */ PictureRatioSizeRule(ExtraRules extraRules, PictureRatioSizeRule pictureRatioSizeRule) {
            this();
        }

        private PictureRatioSizeRule() {
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingRule
        public void execute() {
            String strBuildEnableList;
            LogHelper.m23d(ExtraRules.TAG, "[PictureRatioSizeRule], exectue");
            String value = ExtraRules.this.mSettingCtrl.getSettingItem("pref_camera_picturesize_ratio_key").getValue();
            SettingItem settingItem = ExtraRules.this.mSettingCtrl.getSettingItem("pref_camera_picturesize_key");
            String value2 = settingItem.getValue();
            List<String> supportedValues = ExtraRules.this.mSettingCtrl.getSupportedValues("pref_camera_picturesize_key");
            if (supportedValues == null) {
                LogHelper.m24e(ExtraRules.TAG, "supported picture size is null, return");
                return;
            }
            sortSizesInAscending(supportedValues);
            List<String> listFilterPictureSizesByRatio = Utils.filterPictureSizesByRatio(supportedValues, Double.parseDouble(value));
            if (!listFilterPictureSizesByRatio.contains(value2)) {
                settingItem.setValue(listFilterPictureSizesByRatio.get(listFilterPictureSizesByRatio.size() - 1));
            }
            if (listFilterPictureSizesByRatio.size() == 1) {
                strBuildEnableList = listFilterPictureSizesByRatio.get(0);
            } else {
                strBuildEnableList = listFilterPictureSizesByRatio.size() > 1 ? Utils.buildEnableList((String[]) listFilterPictureSizesByRatio.toArray(new String[listFilterPictureSizesByRatio.size()])) : null;
            }
            settingItem.setOverrideValue(strBuildEnableList);
        }

        @Override // com.mediatek.camera.p005v2.setting.ISettingRule
        public void addLimitation(String str, List<String> list) {
        }

        private void sortSizesInAscending(List<String> list) {
            for (int i = 0; i < list.size() - 1; i++) {
                String str = list.get(0);
                int i2 = 0;
                Size size = Utils.getSize(str);
                String str2 = str;
                String str3 = null;
                for (int i3 = 0; i3 < list.size() - i; i3++) {
                    str3 = list.get(i3);
                    Size size2 = Utils.getSize(str3);
                    if (size2.getWidth() * size2.getHeight() > size.getWidth() * size.getHeight()) {
                        i2 = i3;
                        size = size2;
                        str2 = str3;
                    }
                }
                list.set(i2, str3);
                list.set((list.size() - 1) - i, str2);
            }
        }
    }
}
