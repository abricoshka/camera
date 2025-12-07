package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.ContainerBox;
import com.googlecode.mp4parser.AbstractBox;
import java.math.BigInteger;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public abstract class AbstractAppleMetaDataBox extends AbstractBox implements ContainerBox {
    private static Logger LOG = Logger.getLogger(AbstractAppleMetaDataBox.class.getName());
    AppleDataBox appleDataBox;

    public AbstractAppleMetaDataBox(String str) {
        super(str);
        this.appleDataBox = new AppleDataBox();
    }

    public String toString() {
        return getClass().getSimpleName() + "{appleDataBox=" + getValue() + '}';
    }

    static long toLong(byte b) {
        int i = b;
        if (b < 0) {
            i = b + 256;
        }
        return i;
    }

    public String getValue() {
        int i = 1;
        int i2 = 0;
        if (this.appleDataBox.getFlags() == 1) {
            return Utf8.convert(this.appleDataBox.getData());
        }
        if (this.appleDataBox.getFlags() == 21) {
            byte[] data = this.appleDataBox.getData();
            int length = data.length;
            int length2 = data.length;
            long j = 0;
            while (i2 < length2) {
                j += toLong(data[i2]) << ((length - i) * 8);
                i2++;
                i++;
            }
            return "" + j;
        }
        if (this.appleDataBox.getFlags() == 0) {
            return String.format("%x", new BigInteger(this.appleDataBox.getData()));
        }
        return "unknown";
    }
}
