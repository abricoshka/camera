package com.mediatek.camera.p005v2.exif;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
class CountedDataInputStream extends FilterInputStream {

    /* renamed from: -assertionsDisabled, reason: not valid java name */
    static final /* synthetic */ boolean f111assertionsDisabled = !CountedDataInputStream.class.desiredAssertionStatus();
    private final byte[] mByteArray;
    private final ByteBuffer mByteBuffer;
    private int mCount;

    protected CountedDataInputStream(InputStream inputStream) {
        super(inputStream);
        this.mCount = 0;
        this.mByteArray = new byte[8];
        this.mByteBuffer = ByteBuffer.wrap(this.mByteArray);
    }

    public int getReadByteCount() {
        return this.mCount;
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

    public void skipOrThrow(long j) throws IOException {
        if (skip(j) != j) {
            throw new EOFException();
        }
    }

    public void skipTo(long j) throws IOException {
        long j2 = j - this.mCount;
        if (!f111assertionsDisabled && j2 < 0) {
            throw new AssertionError();
        }
        skipOrThrow(j2);
    }

    public void readOrThrow(byte[] bArr, int i, int i2) throws IOException {
        if (read(bArr, i, i2) != i2) {
            throw new EOFException();
        }
    }

    public void readOrThrow(byte[] bArr) throws IOException {
        readOrThrow(bArr, 0, bArr.length);
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.mByteBuffer.order(byteOrder);
    }

    public ByteOrder getByteOrder() {
        return this.mByteBuffer.order();
    }

    public short readShort() throws IOException {
        readOrThrow(this.mByteArray, 0, 2);
        this.mByteBuffer.rewind();
        return this.mByteBuffer.getShort();
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 65535;
    }

    public int readInt() throws IOException {
        readOrThrow(this.mByteArray, 0, 4);
        this.mByteBuffer.rewind();
        return this.mByteBuffer.getInt();
    }

    public long readUnsignedInt() throws IOException {
        return readInt() & 4294967295L;
    }

    public String readString(int i, Charset charset) throws IOException {
        byte[] bArr = new byte[i];
        readOrThrow(bArr);
        return new String(bArr, charset);
    }
}
