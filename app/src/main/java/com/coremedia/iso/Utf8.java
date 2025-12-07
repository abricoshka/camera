package com.coremedia.iso;

import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public final class Utf8 {
    public static String convert(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        try {
            return new String(bArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
