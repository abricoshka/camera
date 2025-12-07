package com.mediatek.camera.mode.pip.pipwrapping;

import com.mediatek.camera.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class JpegHeaderWrapper {
    private static final String TAG = JpegHeaderWrapper.class.getSimpleName();

    public byte[] readJpegHeader(byte[] bArr) throws Exception {
        int jpegHeaderLength = readJpegHeaderLength(bArr);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        byte[] bArr2 = new byte[jpegHeaderLength];
        int i = byteArrayInputStream.read(bArr2, 0, jpegHeaderLength);
        byteArrayInputStream.close();
        Log.m31d(TAG, "readJpegHeader jpegHeader length = " + bArr2.length + ",readLength = " + i + ",jpegHeaderLength = " + jpegHeaderLength);
        return bArr2;
    }

    public byte[] writeJpegHeader(byte[] bArr, byte[] bArr2) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        try {
            int jpegHeaderLength = readJpegHeaderLength(bArr);
            int length = bArr.length - jpegHeaderLength;
            Log.m31d(TAG, "[writeJpegHeader]jpegHeaderLength = " + jpegHeaderLength + " jpegDataLength = " + length);
            byte[] bArr3 = new byte[length];
            byteArrayInputStream.skip(jpegHeaderLength);
            Log.m31d(TAG, "[writeJpegHeader]read raw jpage data length = " + bArr3.length + ",readLength = " + byteArrayInputStream.read(bArr3));
            byteArrayOutputStream.write(bArr2);
            byteArrayOutputStream.write(bArr3);
            byteArrayOutputStream.flush();
            System.gc();
        } catch (Exception e) {
            Log.m32e(TAG, "[writeJpegHeader]exceptioin " + e.toString());
        } finally {
            byteArrayInputStream.close();
            byteArrayOutputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private int readJpegHeaderLength(byte[] bArr) throws Exception {
        if (bArr == null) {
            Log.m32e(TAG, "[readJpegHeaderLength]jpeg is null!");
            throw new IllegalArgumentException("Argument is null");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        CountedDataInputStream countedDataInputStream = new CountedDataInputStream(byteArrayInputStream);
        if (countedDataInputStream.readShort() != -40) {
            Log.m32e(TAG, "[readJpegHeaderLength]Invalid Jpeg Format!");
            throw new Exception("Invalid Jpeg Format");
        }
        int i = 2;
        for (short s = countedDataInputStream.readShort(); s != -39 && (!isSofMarker(s)); s = countedDataInputStream.readShort()) {
            int unsignedShort = countedDataInputStream.readUnsignedShort();
            if (s != -32 && s != -31 && s != -30 && s != -29 && s != -28 && s != -27 && s != -26 && s != -25 && s != -24 && s != -23 && s != -22 && s != -21 && s != -20 && s != -19 && s != -18 && s != -17) {
                break;
            }
            i += unsignedShort + 2;
            Log.m31d(TAG, "Read marker = " + Integer.toHexString(s) + " jpegHeaderLength = " + i);
            if (unsignedShort < 2 || unsignedShort - 2 != countedDataInputStream.skip(unsignedShort - 2)) {
                Log.m32e(TAG, "[readJpegHeaderLength]Invalid Marker Length = " + unsignedShort);
                throw new Exception("Invalid Marker Length = " + unsignedShort);
            }
        }
        byteArrayInputStream.close();
        countedDataInputStream.close();
        return i;
    }

    private boolean isSofMarker(short s) {
        return (s < -64 || s > -49 || s == -60 || s == -56 || s == -52) ? false : true;
    }

    private class CountedDataInputStream extends FilterInputStream {
        private final byte[] mByteArray;
        private final ByteBuffer mByteBuffer;
        private int mCount;

        public CountedDataInputStream(InputStream inputStream) {
            super(inputStream);
            this.mCount = 0;
            this.mByteArray = new byte[8];
            this.mByteBuffer = ByteBuffer.wrap(this.mByteArray);
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr) throws IOException {
            int i = this.in.read(bArr);
            this.mCount = (i >= 0 ? i : 0) + this.mCount;
            return i;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            int i3 = this.in.read(bArr, i, i2);
            this.mCount = (i3 >= 0 ? i3 : 0) + this.mCount;
            return i3;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            int i = this.in.read();
            this.mCount = (i >= 0 ? 1 : 0) + this.mCount;
            return i;
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public long skip(long j) throws IOException {
            long jSkip = this.in.skip(j);
            this.mCount = (int) (this.mCount + jSkip);
            return jSkip;
        }

        public void readOrThrow(byte[] bArr, int i, int i2) throws IOException {
            if (read(bArr, i, i2) != i2) {
                throw new EOFException();
            }
        }

        public short readShort() throws IOException {
            readOrThrow(this.mByteArray, 0, 2);
            this.mByteBuffer.rewind();
            return this.mByteBuffer.getShort();
        }

        public int readUnsignedShort() throws IOException {
            return readShort() & 65535;
        }
    }
}
