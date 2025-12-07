package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public class AppleStoreAccountTypeBox extends AbstractAppleMetaDataBox {
    public AppleStoreAccountTypeBox() {
        super("akID");
        this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }
}
