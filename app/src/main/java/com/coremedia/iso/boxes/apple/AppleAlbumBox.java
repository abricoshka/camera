package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleAlbumBox extends AbstractAppleMetaDataBox {
    public AppleAlbumBox() {
        super("\u00a9alb");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
