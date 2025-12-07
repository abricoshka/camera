package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public final class AppleCommentBox extends AbstractAppleMetaDataBox {
    public AppleCommentBox() {
        super("\u00a9cmt");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
