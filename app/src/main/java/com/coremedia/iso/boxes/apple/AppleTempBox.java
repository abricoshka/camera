package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleTempBox extends AbstractAppleMetaDataBox {
    public AppleTempBox() {
        super("tmpo");
        this.appleDataBox = AppleDataBox.getUint16AppleDataBox();
    }
}
