package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public class AppleTvEpisodeNumberBox extends AbstractAppleMetaDataBox {
    public AppleTvEpisodeNumberBox() {
        super("tven");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
