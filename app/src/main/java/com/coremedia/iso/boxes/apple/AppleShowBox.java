package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleShowBox extends AbstractAppleMetaDataBox {
    public AppleShowBox() {
        super("tvsh");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
