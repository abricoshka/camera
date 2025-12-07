package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleTrackAuthorBox extends AbstractAppleMetaDataBox {
    public AppleTrackAuthorBox() {
        super("\u00a9wrt");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
