package com.mediatek.camera.p005v2.exif;

/* loaded from: classes.dex */
class JpegHeader {
    JpegHeader() {
    }

    public static final boolean isSofMarker(short s) {
        return (s < -64 || s > -49 || s == -60 || s == -56 || s == -52) ? false : true;
    }
}
