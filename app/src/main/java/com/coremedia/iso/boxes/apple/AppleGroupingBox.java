package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleGroupingBox extends AbstractAppleMetaDataBox {
    public AppleGroupingBox() {
        super("\u00a9grp");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
