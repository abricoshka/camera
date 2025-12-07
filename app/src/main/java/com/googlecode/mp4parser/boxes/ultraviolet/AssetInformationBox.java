package com.googlecode.mp4parser.boxes.ultraviolet;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class AssetInformationBox extends AbstractFullBox {
    String apid;
    String profileVersion;

    public AssetInformationBox() {
        super("ainf");
        this.apid = "";
        this.profileVersion = "0000";
    }
}
