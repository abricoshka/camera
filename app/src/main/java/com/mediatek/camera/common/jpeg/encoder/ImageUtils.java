package com.mediatek.camera.common.jpeg.encoder;

/* loaded from: classes.dex */
class ImageUtils {
    ImageUtils() {
    }

    public static int getNumPlanesForFormat(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 20:
            case 32:
            case 36:
            case 37:
            case 38:
            case 256:
            case 257:
            case 538982489:
            case 540422489:
            case 1144402265:
                return 1;
            case 16:
                return 2;
            case 17:
            case 35:
            case 842094169:
                return 3;
            case 34:
                return 0;
            default:
                throw new UnsupportedOperationException(String.format("Invalid format specified %d", Integer.valueOf(i)));
        }
    }

    public static int getEstimatedNativeAllocBytes(int i, int i2, int i3, int i4) {
        double d;
        switch (i3) {
            case 1:
            case 2:
                d = 4.0d;
                break;
            case 3:
                d = 3.0d;
                break;
            case 4:
            case 16:
            case 20:
            case 32:
            case 36:
            case 540422489:
            case 1144402265:
                d = 2.0d;
                break;
            case 17:
            case 34:
            case 35:
            case 38:
            case 842094169:
                d = 1.5d;
                break;
            case 37:
                d = 1.25d;
                break;
            case 256:
            case 257:
                d = 0.3d;
                break;
            case 538982489:
                d = 1.0d;
                break;
            default:
                throw new UnsupportedOperationException(String.format("Invalid format specified %d", Integer.valueOf(i3)));
        }
        return (int) (d * i * i2 * i4);
    }
}
