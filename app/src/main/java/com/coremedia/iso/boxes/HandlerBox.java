package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class HandlerBox extends AbstractFullBox {
    public static final Map<String, String> readableTypes;
    private String handlerType;
    private String name;
    private boolean zeroTerm;

    static {
        HashMap map = new HashMap();
        map.put("odsm", "ObjectDescriptorStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("crsm", "ClockReferenceStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("sdsm", "SceneDescriptionStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("m7sm", "MPEG7Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("ocsm", "ObjectContentInfoStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("ipsm", "IPMP Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("mjsm", "MPEG-J Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        map.put("mdir", "Apple Meta Data iTunes Reader");
        map.put("mp7b", "MPEG-7 binary XML");
        map.put("mp7t", "MPEG-7 XML");
        map.put("vide", "Video Track");
        map.put("soun", "Sound Track");
        map.put("hint", "Hint Track");
        map.put("appl", "Apple specific");
        map.put("meta", "Timed Metadata track - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        readableTypes = Collections.unmodifiableMap(map);
    }

    public HandlerBox() {
        super("hdlr");
        this.name = null;
        this.zeroTerm = true;
    }

    public String getHandlerType() {
        return this.handlerType;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "HandlerBox[handlerType=" + getHandlerType() + ";name=" + getName() + "]";
    }
}
