package com.coremedia.iso.boxes.apple;

import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class AppleCoverBox extends AbstractAppleMetaDataBox {
    private static Logger LOG = Logger.getLogger(AppleCoverBox.class.getName());

    public AppleCoverBox() {
        super("covr");
    }

    @Override // com.coremedia.iso.boxes.apple.AbstractAppleMetaDataBox
    public String getValue() {
        return "---";
    }
}
