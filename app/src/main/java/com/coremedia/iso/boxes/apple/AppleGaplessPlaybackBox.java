package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleGaplessPlaybackBox extends AbstractAppleMetaDataBox {
    public AppleGaplessPlaybackBox() {
        super("pgap");
        this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }
}
