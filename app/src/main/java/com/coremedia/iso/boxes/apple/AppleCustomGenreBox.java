package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleCustomGenreBox extends AbstractAppleMetaDataBox {
    public AppleCustomGenreBox() {
        super("\u00a9gen");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
