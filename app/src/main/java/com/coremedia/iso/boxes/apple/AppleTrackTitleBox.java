package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleTrackTitleBox extends AbstractAppleMetaDataBox {
    public AppleTrackTitleBox() {
        super("\u00a9nam");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
