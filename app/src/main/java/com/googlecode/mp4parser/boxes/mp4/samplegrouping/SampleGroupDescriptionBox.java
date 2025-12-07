package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.LinkedList;
import java.util.List;

/* loaded from: classes.dex */
public class SampleGroupDescriptionBox extends AbstractFullBox {
    private int defaultLength;
    private List<GroupEntry> groupEntries;
    private String groupingType;

    public SampleGroupDescriptionBox() {
        super("sgpd");
        this.groupEntries = new LinkedList();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SampleGroupDescriptionBox sampleGroupDescriptionBox = (SampleGroupDescriptionBox) obj;
        if (this.defaultLength != sampleGroupDescriptionBox.defaultLength) {
            return false;
        }
        if (this.groupEntries == null ? sampleGroupDescriptionBox.groupEntries != null : (!this.groupEntries.equals(sampleGroupDescriptionBox.groupEntries))) {
            return false;
        }
        return this.groupingType == null ? sampleGroupDescriptionBox.groupingType == null : !(this.groupingType.equals(sampleGroupDescriptionBox.groupingType) ^ true);
    }

    public int hashCode() {
        return ((((this.groupingType != null ? this.groupingType.hashCode() : 0) * 31) + this.defaultLength) * 31) + (this.groupEntries != null ? this.groupEntries.hashCode() : 0);
    }

    public String toString() {
        return "SampleGroupDescriptionBox{groupingType='" + this.groupingType + "', defaultLength=" + this.defaultLength + ", groupEntries=" + this.groupEntries + '}';
    }
}
