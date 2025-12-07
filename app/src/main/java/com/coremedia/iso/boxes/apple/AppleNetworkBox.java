package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleNetworkBox extends AbstractAppleMetaDataBox {
    public AppleNetworkBox() {
        super("tvnn");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
