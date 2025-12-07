package com.googlecode.mp4parser.boxes.piff;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.UUID;

/* loaded from: classes.dex */
public class UuidBasedProtectionSystemSpecificHeaderBox extends AbstractFullBox {
    public static byte[] USER_TYPE = {-48, -118, 79, 24, 16, -13, 74, -126, -74, -56, 50, -40, -85, -95, -125, -45};
    ProtectionSpecificHeader protectionSpecificHeader;
    UUID systemId;

    public UuidBasedProtectionSystemSpecificHeaderBox() {
        super("uuid", USER_TYPE);
    }

    @Override // com.googlecode.mp4parser.AbstractBox
    public byte[] getUserType() {
        return USER_TYPE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UuidBasedProtectionSystemSpecificHeaderBox");
        sb.append("{systemId=").append(this.systemId.toString());
        sb.append(", dataSize=").append(this.protectionSpecificHeader.getData().limit());
        sb.append('}');
        return sb.toString();
    }
}
