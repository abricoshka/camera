package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleArtistBox extends AbstractAppleMetaDataBox {
    public AppleArtistBox() {
        super("\u00a9ART");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
