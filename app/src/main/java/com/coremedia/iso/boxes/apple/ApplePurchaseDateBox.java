package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class ApplePurchaseDateBox extends AbstractAppleMetaDataBox {
    public ApplePurchaseDateBox() {
        super("purd");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
