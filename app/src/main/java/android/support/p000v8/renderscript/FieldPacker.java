package android.support.p000v8.renderscript;

import android.util.Log;
import java.util.BitSet;

/* loaded from: classes.dex */
public class FieldPacker {
    private BitSet mAlignment;
    private byte[] mData;
    private int mLen;
    private int mPos;

    public FieldPacker(int i) {
        this.mPos = 0;
        this.mLen = i;
        this.mData = new byte[i];
        this.mAlignment = new BitSet();
    }

    public FieldPacker(byte[] bArr) {
        this.mPos = bArr.length;
        this.mLen = bArr.length;
        this.mData = bArr;
        this.mAlignment = new BitSet();
    }

    static FieldPacker createFromArray(Object[] objArr) {
        FieldPacker fieldPacker = new FieldPacker(RenderScript.sPointerSize * 8);
        for (Object obj : objArr) {
            fieldPacker.addSafely(obj);
        }
        fieldPacker.resize(fieldPacker.mPos);
        return fieldPacker;
    }

    public void align(int i) {
        if (i <= 0 || ((i - 1) & i) != 0) {
            throw new RSIllegalArgumentException("argument must be a non-negative non-zero power of 2: " + i);
        }
        while ((this.mPos & (i - 1)) != 0) {
            this.mAlignment.flip(this.mPos);
            byte[] bArr = this.mData;
            int i2 = this.mPos;
            this.mPos = i2 + 1;
            bArr[i2] = 0;
        }
    }

    public void subalign(int i) {
        if (((i - 1) & i) != 0) {
            throw new RSIllegalArgumentException("argument must be a non-negative non-zero power of 2: " + i);
        }
        while ((this.mPos & (i - 1)) != 0) {
            this.mPos--;
        }
        if (this.mPos > 0) {
            while (this.mAlignment.get(this.mPos - 1)) {
                this.mPos--;
                this.mAlignment.flip(this.mPos);
            }
        }
    }

    public void reset() {
        this.mPos = 0;
    }

    public void reset(int i) {
        if (i < 0 || i > this.mLen) {
            throw new RSIllegalArgumentException("out of range argument: " + i);
        }
        this.mPos = i;
    }

    public void skip(int i) {
        int i2 = this.mPos + i;
        if (i2 < 0 || i2 > this.mLen) {
            throw new RSIllegalArgumentException("out of range argument: " + i);
        }
        this.mPos = i2;
    }

    public void addI8(byte b) {
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = b;
    }

    public byte subI8() {
        subalign(1);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        return bArr[i];
    }

    public void addI16(short s) {
        align(2);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (s & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) (s >> 8);
    }

    public short subI16() {
        subalign(2);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        short s = (short) ((bArr[i] & 255) << 8);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos - 1;
        this.mPos = i2;
        return (short) (s | ((short) (bArr2[i2] & 255)));
    }

    public void addI32(int i) {
        align(4);
        byte[] bArr = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr[i2] = (byte) (i & 255);
        byte[] bArr2 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr2[i3] = (byte) ((i >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr3[i4] = (byte) ((i >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i5 = this.mPos;
        this.mPos = i5 + 1;
        bArr4[i5] = (byte) ((i >> 24) & 255);
    }

    public int subI32() {
        subalign(4);
        byte[] bArr = this.mData;
        int i = this.mPos - 1;
        this.mPos = i;
        int i2 = (bArr[i] & 255) << 24;
        byte[] bArr2 = this.mData;
        int i3 = this.mPos - 1;
        this.mPos = i3;
        int i4 = i2 | ((bArr2[i3] & 255) << 16);
        byte[] bArr3 = this.mData;
        int i5 = this.mPos - 1;
        this.mPos = i5;
        int i6 = i4 | ((bArr3[i5] & 255) << 8);
        byte[] bArr4 = this.mData;
        int i7 = this.mPos - 1;
        this.mPos = i7;
        return i6 | (bArr4[i7] & 255);
    }

    public void addI64(long j) {
        align(8);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (j & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((j >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((j >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((j >> 24) & 255);
        byte[] bArr5 = this.mData;
        int i5 = this.mPos;
        this.mPos = i5 + 1;
        bArr5[i5] = (byte) ((j >> 32) & 255);
        byte[] bArr6 = this.mData;
        int i6 = this.mPos;
        this.mPos = i6 + 1;
        bArr6[i6] = (byte) ((j >> 40) & 255);
        byte[] bArr7 = this.mData;
        int i7 = this.mPos;
        this.mPos = i7 + 1;
        bArr7[i7] = (byte) ((j >> 48) & 255);
        byte[] bArr8 = this.mData;
        int i8 = this.mPos;
        this.mPos = i8 + 1;
        bArr8[i8] = (byte) ((j >> 56) & 255);
    }

    public long subI64() {
        subalign(8);
        byte[] bArr = this.mData;
        this.mPos = this.mPos - 1;
        byte[] bArr2 = this.mData;
        this.mPos = this.mPos - 1;
        long j = ((bArr[r1] & 255) << 56) | 0 | ((bArr2[r3] & 255) << 48);
        byte[] bArr3 = this.mData;
        this.mPos = this.mPos - 1;
        long j2 = j | ((bArr3[r3] & 255) << 40);
        byte[] bArr4 = this.mData;
        this.mPos = this.mPos - 1;
        long j3 = j2 | ((bArr4[r3] & 255) << 32);
        byte[] bArr5 = this.mData;
        this.mPos = this.mPos - 1;
        long j4 = j3 | ((bArr5[r3] & 255) << 24);
        byte[] bArr6 = this.mData;
        this.mPos = this.mPos - 1;
        long j5 = j4 | ((bArr6[r3] & 255) << 16);
        byte[] bArr7 = this.mData;
        this.mPos = this.mPos - 1;
        long j6 = j5 | ((bArr7[r3] & 255) << 8);
        byte[] bArr8 = this.mData;
        this.mPos = this.mPos - 1;
        return j6 | (bArr8[r3] & 255);
    }

    public void addU8(short s) {
        if (s < 0 || s > 255) {
            Log.e("rs", "FieldPacker.addU8( " + ((int) s) + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) s;
    }

    public void addU16(int i) {
        if (i < 0 || i > 65535) {
            Log.e("rs", "FieldPacker.addU16( " + i + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(2);
        byte[] bArr = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr[i2] = (byte) (i & 255);
        byte[] bArr2 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr2[i3] = (byte) (i >> 8);
    }

    public void addU32(long j) {
        if (j < 0 || j > 4294967295L) {
            Log.e("rs", "FieldPacker.addU32( " + j + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(4);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (j & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((j >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((j >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((j >> 24) & 255);
    }

    public void addU64(long j) {
        if (j < 0) {
            Log.e("rs", "FieldPacker.addU64( " + j + " )");
            throw new IllegalArgumentException("Saving value out of range for type");
        }
        align(8);
        byte[] bArr = this.mData;
        int i = this.mPos;
        this.mPos = i + 1;
        bArr[i] = (byte) (j & 255);
        byte[] bArr2 = this.mData;
        int i2 = this.mPos;
        this.mPos = i2 + 1;
        bArr2[i2] = (byte) ((j >> 8) & 255);
        byte[] bArr3 = this.mData;
        int i3 = this.mPos;
        this.mPos = i3 + 1;
        bArr3[i3] = (byte) ((j >> 16) & 255);
        byte[] bArr4 = this.mData;
        int i4 = this.mPos;
        this.mPos = i4 + 1;
        bArr4[i4] = (byte) ((j >> 24) & 255);
        byte[] bArr5 = this.mData;
        int i5 = this.mPos;
        this.mPos = i5 + 1;
        bArr5[i5] = (byte) ((j >> 32) & 255);
        byte[] bArr6 = this.mData;
        int i6 = this.mPos;
        this.mPos = i6 + 1;
        bArr6[i6] = (byte) ((j >> 40) & 255);
        byte[] bArr7 = this.mData;
        int i7 = this.mPos;
        this.mPos = i7 + 1;
        bArr7[i7] = (byte) ((j >> 48) & 255);
        byte[] bArr8 = this.mData;
        int i8 = this.mPos;
        this.mPos = i8 + 1;
        bArr8[i8] = (byte) ((j >> 56) & 255);
    }

    public void addF32(float f) {
        addI32(Float.floatToRawIntBits(f));
    }

    public float subF32() {
        return Float.intBitsToFloat(subI32());
    }

    public void addF64(double d) {
        addI64(Double.doubleToRawLongBits(d));
    }

    public double subF64() {
        return Double.longBitsToDouble(subI64());
    }

    public void addObj(BaseObj baseObj) {
        if (baseObj != null) {
            if (RenderScript.sPointerSize == 8) {
                addI64(baseObj.getID(null));
                addI64(0L);
                addI64(0L);
                addI64(0L);
                return;
            }
            addI32((int) baseObj.getID(null));
            return;
        }
        if (RenderScript.sPointerSize == 8) {
            addI64(0L);
            addI64(0L);
            addI64(0L);
            addI64(0L);
            return;
        }
        addI32(0);
    }

    public void addF32(Float2 float2) {
        addF32(float2.f20x);
        addF32(float2.f21y);
    }

    public void addF32(Float3 float3) {
        addF32(float3.f22x);
        addF32(float3.f23y);
        addF32(float3.f24z);
    }

    public void addF32(Float4 float4) {
        addF32(float4.f26x);
        addF32(float4.f27y);
        addF32(float4.f28z);
        addF32(float4.f25w);
    }

    public void addF64(Double2 double2) {
        addF64(double2.f11x);
        addF64(double2.f12y);
    }

    public void addF64(Double3 double3) {
        addF64(double3.f13x);
        addF64(double3.f14y);
        addF64(double3.f15z);
    }

    public void addF64(Double4 double4) {
        addF64(double4.f17x);
        addF64(double4.f18y);
        addF64(double4.f19z);
        addF64(double4.f16w);
    }

    public void addI8(Byte2 byte2) {
        addI8(byte2.f2x);
        addI8(byte2.f3y);
    }

    public void addI8(Byte3 byte3) {
        addI8(byte3.f4x);
        addI8(byte3.f5y);
        addI8(byte3.f6z);
    }

    public void addI8(Byte4 byte4) {
        addI8(byte4.f8x);
        addI8(byte4.f9y);
        addI8(byte4.f10z);
        addI8(byte4.f7w);
    }

    public void addU8(Short2 short2) {
        addU8(short2.f49x);
        addU8(short2.f50y);
    }

    public void addU8(Short3 short3) {
        addU8(short3.f51x);
        addU8(short3.f52y);
        addU8(short3.f53z);
    }

    public void addU8(Short4 short4) {
        addU8(short4.f55x);
        addU8(short4.f56y);
        addU8(short4.f57z);
        addU8(short4.f54w);
    }

    public void addI16(Short2 short2) {
        addI16(short2.f49x);
        addI16(short2.f50y);
    }

    public void addI16(Short3 short3) {
        addI16(short3.f51x);
        addI16(short3.f52y);
        addI16(short3.f53z);
    }

    public void addI16(Short4 short4) {
        addI16(short4.f55x);
        addI16(short4.f56y);
        addI16(short4.f57z);
        addI16(short4.f54w);
    }

    public void addU16(Int2 int2) {
        addU16(int2.f29x);
        addU16(int2.f30y);
    }

    public void addU16(Int3 int3) {
        addU16(int3.f31x);
        addU16(int3.f32y);
        addU16(int3.f33z);
    }

    public void addU16(Int4 int4) {
        addU16(int4.f35x);
        addU16(int4.f36y);
        addU16(int4.f37z);
        addU16(int4.f34w);
    }

    public void addI32(Int2 int2) {
        addI32(int2.f29x);
        addI32(int2.f30y);
    }

    public void addI32(Int3 int3) {
        addI32(int3.f31x);
        addI32(int3.f32y);
        addI32(int3.f33z);
    }

    public void addI32(Int4 int4) {
        addI32(int4.f35x);
        addI32(int4.f36y);
        addI32(int4.f37z);
        addI32(int4.f34w);
    }

    public void addU32(Long2 long2) {
        addU32(long2.f38x);
        addU32(long2.f39y);
    }

    public void addU32(Long3 long3) {
        addU32(long3.f40x);
        addU32(long3.f41y);
        addU32(long3.f42z);
    }

    public void addU32(Long4 long4) {
        addU32(long4.f44x);
        addU32(long4.f45y);
        addU32(long4.f46z);
        addU32(long4.f43w);
    }

    public void addI64(Long2 long2) {
        addI64(long2.f38x);
        addI64(long2.f39y);
    }

    public void addI64(Long3 long3) {
        addI64(long3.f40x);
        addI64(long3.f41y);
        addI64(long3.f42z);
    }

    public void addI64(Long4 long4) {
        addI64(long4.f44x);
        addI64(long4.f45y);
        addI64(long4.f46z);
        addI64(long4.f43w);
    }

    public void addU64(Long2 long2) {
        addU64(long2.f38x);
        addU64(long2.f39y);
    }

    public void addU64(Long3 long3) {
        addU64(long3.f40x);
        addU64(long3.f41y);
        addU64(long3.f42z);
    }

    public void addU64(Long4 long4) {
        addU64(long4.f44x);
        addU64(long4.f45y);
        addU64(long4.f46z);
        addU64(long4.f43w);
    }

    public Float2 subFloat2() {
        Float2 float2 = new Float2();
        float2.f21y = subF32();
        float2.f20x = subF32();
        return float2;
    }

    public Float3 subFloat3() {
        Float3 float3 = new Float3();
        float3.f24z = subF32();
        float3.f23y = subF32();
        float3.f22x = subF32();
        return float3;
    }

    public Float4 subFloat4() {
        Float4 float4 = new Float4();
        float4.f25w = subF32();
        float4.f28z = subF32();
        float4.f27y = subF32();
        float4.f26x = subF32();
        return float4;
    }

    public Double2 subDouble2() {
        Double2 double2 = new Double2();
        double2.f12y = subF64();
        double2.f11x = subF64();
        return double2;
    }

    public Double3 subDouble3() {
        Double3 double3 = new Double3();
        double3.f15z = subF64();
        double3.f14y = subF64();
        double3.f13x = subF64();
        return double3;
    }

    public Double4 subDouble4() {
        Double4 double4 = new Double4();
        double4.f16w = subF64();
        double4.f19z = subF64();
        double4.f18y = subF64();
        double4.f17x = subF64();
        return double4;
    }

    public Byte2 subByte2() {
        Byte2 byte2 = new Byte2();
        byte2.f3y = subI8();
        byte2.f2x = subI8();
        return byte2;
    }

    public Byte3 subByte3() {
        Byte3 byte3 = new Byte3();
        byte3.f6z = subI8();
        byte3.f5y = subI8();
        byte3.f4x = subI8();
        return byte3;
    }

    public Byte4 subByte4() {
        Byte4 byte4 = new Byte4();
        byte4.f7w = subI8();
        byte4.f10z = subI8();
        byte4.f9y = subI8();
        byte4.f8x = subI8();
        return byte4;
    }

    public Short2 subShort2() {
        Short2 short2 = new Short2();
        short2.f50y = subI16();
        short2.f49x = subI16();
        return short2;
    }

    public Short3 subShort3() {
        Short3 short3 = new Short3();
        short3.f53z = subI16();
        short3.f52y = subI16();
        short3.f51x = subI16();
        return short3;
    }

    public Short4 subShort4() {
        Short4 short4 = new Short4();
        short4.f54w = subI16();
        short4.f57z = subI16();
        short4.f56y = subI16();
        short4.f55x = subI16();
        return short4;
    }

    public Int2 subInt2() {
        Int2 int2 = new Int2();
        int2.f30y = subI32();
        int2.f29x = subI32();
        return int2;
    }

    public Int3 subInt3() {
        Int3 int3 = new Int3();
        int3.f33z = subI32();
        int3.f32y = subI32();
        int3.f31x = subI32();
        return int3;
    }

    public Int4 subInt4() {
        Int4 int4 = new Int4();
        int4.f34w = subI32();
        int4.f37z = subI32();
        int4.f36y = subI32();
        int4.f35x = subI32();
        return int4;
    }

    public Long2 subLong2() {
        Long2 long2 = new Long2();
        long2.f39y = subI64();
        long2.f38x = subI64();
        return long2;
    }

    public Long3 subLong3() {
        Long3 long3 = new Long3();
        long3.f42z = subI64();
        long3.f41y = subI64();
        long3.f40x = subI64();
        return long3;
    }

    public Long4 subLong4() {
        Long4 long4 = new Long4();
        long4.f43w = subI64();
        long4.f46z = subI64();
        long4.f45y = subI64();
        long4.f44x = subI64();
        return long4;
    }

    public void addMatrix(Matrix4f matrix4f) {
        for (int i = 0; i < matrix4f.mMat.length; i++) {
            addF32(matrix4f.mMat[i]);
        }
    }

    public Matrix4f subMatrix4f() {
        Matrix4f matrix4f = new Matrix4f();
        for (int length = matrix4f.mMat.length - 1; length >= 0; length--) {
            matrix4f.mMat[length] = subF32();
        }
        return matrix4f;
    }

    public void addMatrix(Matrix3f matrix3f) {
        for (int i = 0; i < matrix3f.mMat.length; i++) {
            addF32(matrix3f.mMat[i]);
        }
    }

    public Matrix3f subMatrix3f() {
        Matrix3f matrix3f = new Matrix3f();
        for (int length = matrix3f.mMat.length - 1; length >= 0; length--) {
            matrix3f.mMat[length] = subF32();
        }
        return matrix3f;
    }

    public void addMatrix(Matrix2f matrix2f) {
        for (int i = 0; i < matrix2f.mMat.length; i++) {
            addF32(matrix2f.mMat[i]);
        }
    }

    public Matrix2f subMatrix2f() {
        Matrix2f matrix2f = new Matrix2f();
        for (int length = matrix2f.mMat.length - 1; length >= 0; length--) {
            matrix2f.mMat[length] = subF32();
        }
        return matrix2f;
    }

    public void addBoolean(boolean z) {
        addI8((byte) (z ? 1 : 0));
    }

    public boolean subBoolean() {
        return subI8() == 1;
    }

    public final byte[] getData() {
        return this.mData;
    }

    public int getPos() {
        return this.mPos;
    }

    private static void addToPack(FieldPacker fieldPacker, Object obj) {
        if (obj instanceof Boolean) {
            fieldPacker.addBoolean(((Boolean) obj).booleanValue());
            return;
        }
        if (obj instanceof Byte) {
            fieldPacker.addI8(((Byte) obj).byteValue());
            return;
        }
        if (obj instanceof Short) {
            fieldPacker.addI16(((Short) obj).shortValue());
            return;
        }
        if (obj instanceof Integer) {
            fieldPacker.addI32(((Integer) obj).intValue());
            return;
        }
        if (obj instanceof Long) {
            fieldPacker.addI64(((Long) obj).longValue());
            return;
        }
        if (obj instanceof Float) {
            fieldPacker.addF32(((Float) obj).floatValue());
            return;
        }
        if (obj instanceof Double) {
            fieldPacker.addF64(((Double) obj).doubleValue());
            return;
        }
        if (obj instanceof Byte2) {
            fieldPacker.addI8((Byte2) obj);
            return;
        }
        if (obj instanceof Byte3) {
            fieldPacker.addI8((Byte3) obj);
            return;
        }
        if (obj instanceof Byte4) {
            fieldPacker.addI8((Byte4) obj);
            return;
        }
        if (obj instanceof Short2) {
            fieldPacker.addI16((Short2) obj);
            return;
        }
        if (obj instanceof Short3) {
            fieldPacker.addI16((Short3) obj);
            return;
        }
        if (obj instanceof Short4) {
            fieldPacker.addI16((Short4) obj);
            return;
        }
        if (obj instanceof Int2) {
            fieldPacker.addI32((Int2) obj);
            return;
        }
        if (obj instanceof Int3) {
            fieldPacker.addI32((Int3) obj);
            return;
        }
        if (obj instanceof Int4) {
            fieldPacker.addI32((Int4) obj);
            return;
        }
        if (obj instanceof Long2) {
            fieldPacker.addI64((Long2) obj);
            return;
        }
        if (obj instanceof Long3) {
            fieldPacker.addI64((Long3) obj);
            return;
        }
        if (obj instanceof Long4) {
            fieldPacker.addI64((Long4) obj);
            return;
        }
        if (obj instanceof Float2) {
            fieldPacker.addF32((Float2) obj);
            return;
        }
        if (obj instanceof Float3) {
            fieldPacker.addF32((Float3) obj);
            return;
        }
        if (obj instanceof Float4) {
            fieldPacker.addF32((Float4) obj);
            return;
        }
        if (obj instanceof Double2) {
            fieldPacker.addF64((Double2) obj);
            return;
        }
        if (obj instanceof Double3) {
            fieldPacker.addF64((Double3) obj);
            return;
        }
        if (obj instanceof Double4) {
            fieldPacker.addF64((Double4) obj);
            return;
        }
        if (obj instanceof Matrix2f) {
            fieldPacker.addMatrix((Matrix2f) obj);
            return;
        }
        if (obj instanceof Matrix3f) {
            fieldPacker.addMatrix((Matrix3f) obj);
        } else if (obj instanceof Matrix4f) {
            fieldPacker.addMatrix((Matrix4f) obj);
        } else if (obj instanceof BaseObj) {
            fieldPacker.addObj((BaseObj) obj);
        }
    }

    private static int getPackedSize(Object obj) {
        if ((obj instanceof Boolean) || (obj instanceof Byte)) {
            return 1;
        }
        if (obj instanceof Short) {
            return 2;
        }
        if (obj instanceof Integer) {
            return 4;
        }
        if (obj instanceof Long) {
            return 8;
        }
        if (obj instanceof Float) {
            return 4;
        }
        if (obj instanceof Double) {
            return 8;
        }
        if (obj instanceof Byte2) {
            return 2;
        }
        if (obj instanceof Byte3) {
            return 3;
        }
        if ((obj instanceof Byte4) || (obj instanceof Short2)) {
            return 4;
        }
        if (obj instanceof Short3) {
            return 6;
        }
        if ((obj instanceof Short4) || (obj instanceof Int2)) {
            return 8;
        }
        if (obj instanceof Int3) {
            return 12;
        }
        if ((obj instanceof Int4) || (obj instanceof Long2)) {
            return 16;
        }
        if (obj instanceof Long3) {
            return 24;
        }
        if (obj instanceof Long4) {
            return 32;
        }
        if (obj instanceof Float2) {
            return 8;
        }
        if (obj instanceof Float3) {
            return 12;
        }
        if ((obj instanceof Float4) || (obj instanceof Double2)) {
            return 16;
        }
        if (obj instanceof Double3) {
            return 24;
        }
        if (obj instanceof Double4) {
            return 32;
        }
        if (obj instanceof Matrix2f) {
            return 16;
        }
        if (obj instanceof Matrix3f) {
            return 36;
        }
        if (obj instanceof Matrix4f) {
            return 64;
        }
        if (obj instanceof BaseObj) {
            return RenderScript.sPointerSize == 8 ? 32 : 4;
        }
        return 0;
    }

    static FieldPacker createFieldPack(Object[] objArr) {
        int packedSize = 0;
        for (Object obj : objArr) {
            packedSize += getPackedSize(obj);
        }
        FieldPacker fieldPacker = new FieldPacker(packedSize);
        for (Object obj2 : objArr) {
            addToPack(fieldPacker, obj2);
        }
        return fieldPacker;
    }

    private boolean resize(int i) {
        if (i == this.mLen) {
            return false;
        }
        byte[] bArr = new byte[i];
        System.arraycopy(this.mData, 0, bArr, 0, this.mPos);
        this.mData = bArr;
        this.mLen = i;
        return true;
    }

    private void addSafely(Object obj) {
        boolean z;
        int i = this.mPos;
        do {
            z = false;
            try {
                addToPack(this, obj);
            } catch (ArrayIndexOutOfBoundsException e) {
                this.mPos = i;
                resize(this.mLen * 2);
                z = true;
            }
        } while (z);
    }
}
