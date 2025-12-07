package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleRatingBox extends AbstractAppleMetaDataBox {
    public AppleRatingBox() {
        super("rtng");
        this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }
}
