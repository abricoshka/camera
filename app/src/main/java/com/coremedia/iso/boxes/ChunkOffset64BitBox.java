package com.coremedia.iso.boxes;

/* loaded from: classes.dex */
public class ChunkOffset64BitBox extends ChunkOffsetBox {
    private long[] chunkOffsets;

    public ChunkOffset64BitBox() {
        super("co64");
    }

    @Override // com.coremedia.iso.boxes.ChunkOffsetBox
    public long[] getChunkOffsets() {
        return this.chunkOffsets;
    }
}
