package com.googlecode.mp4parser.boxes.ultraviolet;

import com.googlecode.mp4parser.AbstractFullBox;

/* loaded from: classes.dex */
public class BaseLocationBox extends AbstractFullBox {
    String baseLocation;
    String purchaseLocation;

    public BaseLocationBox() {
        super("bloc");
        this.baseLocation = "";
        this.purchaseLocation = "";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseLocationBox baseLocationBox = (BaseLocationBox) obj;
        if (this.baseLocation == null ? baseLocationBox.baseLocation != null : (!this.baseLocation.equals(baseLocationBox.baseLocation))) {
            return false;
        }
        return this.purchaseLocation == null ? baseLocationBox.purchaseLocation == null : !(this.purchaseLocation.equals(baseLocationBox.purchaseLocation) ^ true);
    }

    public int hashCode() {
        return ((this.baseLocation != null ? this.baseLocation.hashCode() : 0) * 31) + (this.purchaseLocation != null ? this.purchaseLocation.hashCode() : 0);
    }
}
