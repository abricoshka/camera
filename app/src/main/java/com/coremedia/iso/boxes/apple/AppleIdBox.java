package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleIdBox extends AbstractAppleMetaDataBox {
    public AppleIdBox() {
        super("apID");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
