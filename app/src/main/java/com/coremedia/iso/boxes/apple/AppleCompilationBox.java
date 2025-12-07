package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleCompilationBox extends AbstractAppleMetaDataBox {
    public AppleCompilationBox() {
        super("cpil");
        this.appleDataBox = AppleDataBox.getUint8AppleDataBox();
    }
}
