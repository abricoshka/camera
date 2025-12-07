package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleSynopsisBox extends AbstractAppleMetaDataBox {
    public AppleSynopsisBox() {
        super("ldes");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
