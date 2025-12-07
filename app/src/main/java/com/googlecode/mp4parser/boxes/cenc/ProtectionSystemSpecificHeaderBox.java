package com.googlecode.mp4parser.boxes.cenc;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.util.UUID;

/* loaded from: classes.dex */
public class ProtectionSystemSpecificHeaderBox extends AbstractFullBox {
    public static byte[] OMA2_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("A2B55680-6F43-11E0-9A3F-0002A5D5C51B"));
    public static byte[] PLAYREADY_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"));

    public ProtectionSystemSpecificHeaderBox() {
        super("pssh");
    }
}
