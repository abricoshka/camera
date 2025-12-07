package com.googlecode.mp4parser;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public abstract class AbstractBox implements Box {
    private ByteBuffer deadBytes = null;
    protected String type;
    private byte[] userType;
    public static int MEM_MAP_THRESHOLD = 102400;
    private static Logger LOG = Logger.getLogger(AbstractBox.class.getName());

    protected AbstractBox(String str) {
        this.type = str;
    }

    protected AbstractBox(String str, byte[] bArr) {
        this.type = str;
        this.userType = bArr;
    }

    @DoNotParseDetail
    public String getType() {
        return this.type;
    }

    @DoNotParseDetail
    public byte[] getUserType() {
        return this.userType;
    }
}
