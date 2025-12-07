package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class PlayReadyHeader extends ProtectionSpecificHeader {
    private long length;
    private List<PlayReadyRecord> records;

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public ByteBuffer getData() {
        int i;
        int iLimit = 6;
        Iterator<T> it = this.records.iterator();
        while (true) {
            i = iLimit;
            if (!it.hasNext()) {
                break;
            }
            iLimit = ((PlayReadyRecord) it.next()).getValue().rewind().limit() + i + 4;
        }
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i);
        IsoTypeWriter.writeUInt32BE(byteBufferAllocate, i);
        IsoTypeWriter.writeUInt16BE(byteBufferAllocate, this.records.size());
        for (PlayReadyRecord playReadyRecord : this.records) {
            IsoTypeWriter.writeUInt16BE(byteBufferAllocate, playReadyRecord.type);
            IsoTypeWriter.writeUInt16BE(byteBufferAllocate, playReadyRecord.getValue().limit());
            byteBufferAllocate.put(playReadyRecord.getValue());
        }
        return byteBufferAllocate;
    }

    @Override // com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayReadyHeader");
        sb.append("{length=").append(this.length);
        sb.append(", recordCount=").append(this.records.size());
        sb.append(", records=").append(this.records);
        sb.append('}');
        return sb.toString();
    }

    public static abstract class PlayReadyRecord {
        int type;

        public abstract ByteBuffer getValue();

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PlayReadyRecord");
            sb.append("{type=").append(this.type);
            sb.append(", length=").append(getValue().limit());
            sb.append('}');
            return sb.toString();
        }
    }
}
