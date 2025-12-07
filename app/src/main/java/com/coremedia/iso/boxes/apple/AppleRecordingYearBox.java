package com.coremedia.iso.boxes.apple;

/* loaded from: classes.dex */
public class AppleRecordingYearBox extends AbstractAppleMetaDataBox {
    public AppleRecordingYearBox() {
        super("\u00a9day");
        this.appleDataBox = AppleDataBox.getStringAppleDataBox();
    }
}
