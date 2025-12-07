package com.coremedia.iso;

import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

@DoNotParseDetail
/* loaded from: classes.dex */
public class IsoFile extends AbstractContainerBox implements Closeable {
    protected BoxParser boxParser;
    ReadableByteChannel byteChannel;

    public IsoFile() {
        super("");
        this.boxParser = new PropertyBoxParserImpl(new String[0]);
    }

    @Override // com.googlecode.mp4parser.AbstractContainerBox
    @DoNotParseDetail
    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("IsoFile[");
        if (this.boxes != null) {
            while (true) {
                int i2 = i;
                if (i2 >= this.boxes.size()) {
                    break;
                }
                if (i2 > 0) {
                    sb.append(";");
                }
                sb.append(this.boxes.get(i2).toString());
                i = i2 + 1;
            }
        } else {
            sb.append("unparsed");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.byteChannel.close();
    }
}
