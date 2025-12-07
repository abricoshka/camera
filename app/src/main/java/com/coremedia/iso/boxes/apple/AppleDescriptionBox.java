package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleDescriptionBox extends AbstractAppleMetaDataBox {
    public AppleDescriptionBox() {
        super("desc");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
