package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.boxes.Box;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public final class MediaDataBox implements Box {
    private static Logger LOG = Logger.getLogger(MediaDataBox.class.getName());
    private Map<Long, Reference<ByteBuffer>> cache = new HashMap();
}
