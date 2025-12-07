package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public class AppleTvEpisodeBox extends AbstractAppleMetaDataBox {
    public AppleTvEpisodeBox() {
        super("tves");
        this.appleDataBox = AppleDataBox.getUint32AppleDataBox();
    }
}
