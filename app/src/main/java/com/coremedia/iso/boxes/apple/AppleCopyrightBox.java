package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleCopyrightBox extends AbstractAppleMetaDataBox {
    public AppleCopyrightBox() {
        super("cprt");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
