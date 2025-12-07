package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleEncoderBox extends AbstractAppleMetaDataBox {
    public AppleEncoderBox() {
        super("\u00a9too");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
