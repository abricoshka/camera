package com.coremedia.iso.boxes.threegpp26244;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class LocationInformationBox extends AbstractFullBox {
    private String additionalNotes;
    private String astronomicalBody;
    private String name;

    public LocationInformationBox() {
        super("loci");
        this.name = "";
        this.astronomicalBody = "";
        this.additionalNotes = "";
    }
}
