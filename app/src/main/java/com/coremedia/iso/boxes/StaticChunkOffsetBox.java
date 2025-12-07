package com.coremedia.iso.boxes;

/* loaded from: classes.dex */
public class StaticChunkOffsetBox extends ChunkOffsetBox {
    private long[] chunkOffsets;

    public StaticChunkOffsetBox() {
        super("stco");
        this.chunkOffsets = new long[0];
    }

    @Override // com.coremedia.iso.boxes.ChunkOffsetBox
    public long[] getChunkOffsets() {
        return this.chunkOffsets;
    }
}
