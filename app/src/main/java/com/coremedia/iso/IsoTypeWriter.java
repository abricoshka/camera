package com.coremedia.iso;

import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public final class IsoTypeWriter {

    /* renamed from: -assertionsDisabled, reason: not valid java name */
    static final /* synthetic */ boolean f94assertionsDisabled = !IsoTypeWriter.class.desiredAssertionStatus();

    public static void writeUInt32BE(ByteBuffer byteBuffer, long j) {
        boolean z = false;
        if (!f94assertionsDisabled) {
            if (j >= 0 && j <= 4294967296L) {
                z = true;
            }
            if (!z) {
                throw new AssertionError("The given long is not in the range of uint32 (" + j + ")");
            }
        }
        writeUInt16BE(byteBuffer, ((int) j) & 65535);
        writeUInt16BE(byteBuffer, (int) ((j >> 16) & 65535));
    }

    public static void writeUInt16BE(ByteBuffer byteBuffer, int i) {
        int i2 = 65535 & i;
        writeUInt8(byteBuffer, i2 & 255);
        writeUInt8(byteBuffer, i2 >> 8);
    }

    public static void writeUInt8(ByteBuffer byteBuffer, int i) {
        byteBuffer.put((byte) (i & 255));
    }
}
