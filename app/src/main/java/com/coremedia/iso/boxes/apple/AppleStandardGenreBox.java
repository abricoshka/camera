package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleStandardGenreBox extends AbstractAppleMetaDataBox {
    public AppleStandardGenreBox() {
        super("gnre");
        this.appleDataBox = AppleDataBox.getUint16AppleDataBox();
    }
}
