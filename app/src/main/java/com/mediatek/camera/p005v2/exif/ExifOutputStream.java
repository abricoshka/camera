package com.mediatek.camera.p005v2.exif;

import android.support.v4.app.FrameMetricsAggregator;
import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
class ExifOutputStream extends FilterOutputStream {
    private ByteBuffer mBuffer;
    private int mByteToCopy;
    private int mByteToSkip;
    private ExifData mExifData;
    private final ExifInterface mInterface;
    private byte[] mSingleByteArray;
    private int mState;

    protected ExifOutputStream(OutputStream outputStream, ExifInterface exifInterface) {
        super(new BufferedOutputStream(outputStream, 65536));
        this.mState = 0;
        this.mSingleByteArray = new byte[1];
        this.mBuffer = ByteBuffer.allocate(4);
        this.mInterface = exifInterface;
    }

    protected void setExifData(ExifData exifData) {
        this.mExifData = exifData;
    }

    private int requestByteToBuffer(int i, byte[] bArr, int i2, int i3) {
        int iPosition = i - this.mBuffer.position();
        if (i3 > iPosition) {
            i3 = iPosition;
        }
        this.mBuffer.put(bArr, i2, i3);
        return i3;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x003e, code lost:
    
        if (r9 <= 0) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0040, code lost:
    
        r6.out.write(r7, r8, r9);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0045, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:?, code lost:
    
        return;
     */
    @Override // java.io.FilterOutputStream, java.io.OutputStream
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void write(byte[] r7, int r8, int r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 278
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.p005v2.exif.ExifOutputStream.write(byte[], int, int):void");
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int i) throws IOException {
        this.mSingleByteArray[0] = (byte) (i & 255);
        write(this.mSingleByteArray);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    private void writeExifData() throws IOException {
        if (this.mExifData == null) {
            return;
        }
        ArrayList<ExifTag> arrayListStripNullValueTags = stripNullValueTags(this.mExifData);
        createRequiredIfdAndTag();
        int iCalculateAllOffset = calculateAllOffset();
        if (iCalculateAllOffset + 8 > 65535) {
            throw new IOException("Exif header is too large (>64Kb)");
        }
        OrderedDataOutputStream orderedDataOutputStream = new OrderedDataOutputStream(this.out);
        orderedDataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        orderedDataOutputStream.writeShort((short) -31);
        orderedDataOutputStream.writeShort((short) (iCalculateAllOffset + 8));
        orderedDataOutputStream.writeInt(1165519206);
        orderedDataOutputStream.writeShort((short) 0);
        if (this.mExifData.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            orderedDataOutputStream.writeShort((short) 19789);
        } else {
            orderedDataOutputStream.writeShort((short) 18761);
        }
        orderedDataOutputStream.setByteOrder(this.mExifData.getByteOrder());
        orderedDataOutputStream.writeShort((short) 42);
        orderedDataOutputStream.writeInt(8);
        writeAllTags(orderedDataOutputStream);
        writeThumbnail(orderedDataOutputStream);
        Iterator<T> it = arrayListStripNullValueTags.iterator();
        while (it.hasNext()) {
            this.mExifData.addTag((ExifTag) it.next());
        }
    }

    private ArrayList<ExifTag> stripNullValueTags(ExifData exifData) {
        ArrayList<ExifTag> arrayList = new ArrayList<>();
        for (ExifTag exifTag : exifData.getAllTags()) {
            if (exifTag.getValue() == null && (!ExifInterface.isOffsetTag(exifTag.getTagId()))) {
                exifData.removeTag(exifTag.getTagId(), exifTag.getIfd());
                arrayList.add(exifTag);
            }
        }
        return arrayList;
    }

    private void writeThumbnail(OrderedDataOutputStream orderedDataOutputStream) throws IOException {
        if (this.mExifData.hasCompressedThumbnail()) {
            orderedDataOutputStream.write(this.mExifData.getCompressedThumbnail());
        } else if (this.mExifData.hasUncompressedStrip()) {
            for (int i = 0; i < this.mExifData.getStripCount(); i++) {
                orderedDataOutputStream.write(this.mExifData.getStrip(i));
            }
        }
    }

    private void writeAllTags(OrderedDataOutputStream orderedDataOutputStream) throws IOException {
        writeIfd(this.mExifData.getIfdData(0), orderedDataOutputStream);
        writeIfd(this.mExifData.getIfdData(2), orderedDataOutputStream);
        IfdData ifdData = this.mExifData.getIfdData(3);
        if (ifdData != null) {
            writeIfd(ifdData, orderedDataOutputStream);
        }
        IfdData ifdData2 = this.mExifData.getIfdData(4);
        if (ifdData2 != null) {
            writeIfd(ifdData2, orderedDataOutputStream);
        }
        if (this.mExifData.getIfdData(1) != null) {
            writeIfd(this.mExifData.getIfdData(1), orderedDataOutputStream);
        }
    }

    private void writeIfd(IfdData ifdData, OrderedDataOutputStream orderedDataOutputStream) throws IOException {
        ExifTag[] allTags = ifdData.getAllTags();
        orderedDataOutputStream.writeShort((short) allTags.length);
        for (ExifTag exifTag : allTags) {
            orderedDataOutputStream.writeShort(exifTag.getTagId());
            orderedDataOutputStream.writeShort(exifTag.getDataType());
            orderedDataOutputStream.writeInt(exifTag.getComponentCount());
            if (exifTag.getDataSize() > 4) {
                orderedDataOutputStream.writeInt(exifTag.getOffset());
            } else {
                writeTagValue(exifTag, orderedDataOutputStream);
                int dataSize = 4 - exifTag.getDataSize();
                for (int i = 0; i < dataSize; i++) {
                    orderedDataOutputStream.write(0);
                }
            }
        }
        orderedDataOutputStream.writeInt(ifdData.getOffsetToNextIfd());
        for (ExifTag exifTag2 : allTags) {
            if (exifTag2.getDataSize() > 4) {
                writeTagValue(exifTag2, orderedDataOutputStream);
            }
        }
    }

    private int calculateOffsetOfIfd(IfdData ifdData, int i) {
        int tagCount = i + (ifdData.getTagCount() * 12) + 2 + 4;
        int dataSize = tagCount;
        for (ExifTag exifTag : ifdData.getAllTags()) {
            if (exifTag.getDataSize() > 4) {
                exifTag.setOffset(dataSize);
                dataSize += exifTag.getDataSize();
            }
        }
        return dataSize;
    }

    private void createRequiredIfdAndTag() throws IOException {
        IfdData ifdData = this.mExifData.getIfdData(0);
        if (ifdData == null) {
            ifdData = new IfdData(0);
            this.mExifData.addIfdData(ifdData);
        }
        ExifTag exifTagBuildUninitializedTag = this.mInterface.buildUninitializedTag(ExifInterface.TAG_EXIF_IFD);
        if (exifTagBuildUninitializedTag == null) {
            throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_EXIF_IFD);
        }
        ifdData.setTag(exifTagBuildUninitializedTag);
        IfdData ifdData2 = this.mExifData.getIfdData(2);
        if (ifdData2 == null) {
            ifdData2 = new IfdData(2);
            this.mExifData.addIfdData(ifdData2);
        }
        if (this.mExifData.getIfdData(4) != null) {
            ExifTag exifTagBuildUninitializedTag2 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_GPS_IFD);
            if (exifTagBuildUninitializedTag2 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_GPS_IFD);
            }
            ifdData.setTag(exifTagBuildUninitializedTag2);
        }
        if (this.mExifData.getIfdData(3) != null) {
            ExifTag exifTagBuildUninitializedTag3 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_INTEROPERABILITY_IFD);
            if (exifTagBuildUninitializedTag3 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_INTEROPERABILITY_IFD);
            }
            ifdData2.setTag(exifTagBuildUninitializedTag3);
        }
        IfdData ifdData3 = this.mExifData.getIfdData(1);
        if (this.mExifData.hasCompressedThumbnail()) {
            if (ifdData3 == null) {
                ifdData3 = new IfdData(1);
                this.mExifData.addIfdData(ifdData3);
            }
            ExifTag exifTagBuildUninitializedTag4 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT);
            if (exifTagBuildUninitializedTag4 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT);
            }
            ifdData3.setTag(exifTagBuildUninitializedTag4);
            ExifTag exifTagBuildUninitializedTag5 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
            if (exifTagBuildUninitializedTag5 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
            }
            exifTagBuildUninitializedTag5.setValue(this.mExifData.getCompressedThumbnail().length);
            ifdData3.setTag(exifTagBuildUninitializedTag5);
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_OFFSETS));
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_BYTE_COUNTS));
            return;
        }
        if (this.mExifData.hasUncompressedStrip()) {
            if (ifdData3 == null) {
                ifdData3 = new IfdData(1);
                this.mExifData.addIfdData(ifdData3);
            }
            int stripCount = this.mExifData.getStripCount();
            ExifTag exifTagBuildUninitializedTag6 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_STRIP_OFFSETS);
            if (exifTagBuildUninitializedTag6 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_STRIP_OFFSETS);
            }
            ExifTag exifTagBuildUninitializedTag7 = this.mInterface.buildUninitializedTag(ExifInterface.TAG_STRIP_BYTE_COUNTS);
            if (exifTagBuildUninitializedTag7 == null) {
                throw new IOException("No definition for crucial exif tag: " + ExifInterface.TAG_STRIP_BYTE_COUNTS);
            }
            long[] jArr = new long[stripCount];
            for (int i = 0; i < this.mExifData.getStripCount(); i++) {
                jArr[i] = this.mExifData.getStrip(i).length;
            }
            exifTagBuildUninitializedTag7.setValue(jArr);
            ifdData3.setTag(exifTagBuildUninitializedTag6);
            ifdData3.setTag(exifTagBuildUninitializedTag7);
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT));
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH));
            return;
        }
        if (ifdData3 != null) {
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_OFFSETS));
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_BYTE_COUNTS));
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT));
            ifdData3.removeTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH));
        }
    }

    private int calculateAllOffset() {
        IfdData ifdData = this.mExifData.getIfdData(0);
        int iCalculateOffsetOfIfd = calculateOffsetOfIfd(ifdData, 8);
        ifdData.getTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_EXIF_IFD)).setValue(iCalculateOffsetOfIfd);
        IfdData ifdData2 = this.mExifData.getIfdData(2);
        int iCalculateOffsetOfIfd2 = calculateOffsetOfIfd(ifdData2, iCalculateOffsetOfIfd);
        IfdData ifdData3 = this.mExifData.getIfdData(3);
        if (ifdData3 != null) {
            ifdData2.getTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_INTEROPERABILITY_IFD)).setValue(iCalculateOffsetOfIfd2);
            iCalculateOffsetOfIfd2 = calculateOffsetOfIfd(ifdData3, iCalculateOffsetOfIfd2);
        }
        IfdData ifdData4 = this.mExifData.getIfdData(4);
        if (ifdData4 != null) {
            ifdData.getTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_GPS_IFD)).setValue(iCalculateOffsetOfIfd2);
            iCalculateOffsetOfIfd2 = calculateOffsetOfIfd(ifdData4, iCalculateOffsetOfIfd2);
        }
        IfdData ifdData5 = this.mExifData.getIfdData(1);
        if (ifdData5 != null) {
            ifdData.setOffsetToNextIfd(iCalculateOffsetOfIfd2);
            iCalculateOffsetOfIfd2 = calculateOffsetOfIfd(ifdData5, iCalculateOffsetOfIfd2);
        }
        if (this.mExifData.hasCompressedThumbnail()) {
            ifdData5.getTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT)).setValue(iCalculateOffsetOfIfd2);
            return this.mExifData.getCompressedThumbnail().length + iCalculateOffsetOfIfd2;
        }
        if (!this.mExifData.hasUncompressedStrip()) {
            return iCalculateOffsetOfIfd2;
        }
        long[] jArr = new long[this.mExifData.getStripCount()];
        int length = iCalculateOffsetOfIfd2;
        for (int i = 0; i < this.mExifData.getStripCount(); i++) {
            jArr[i] = length;
            length += this.mExifData.getStrip(i).length;
        }
        ifdData5.getTag(ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_OFFSETS)).setValue(jArr);
        return length;
    }

    static void writeTagValue(ExifTag exifTag, OrderedDataOutputStream orderedDataOutputStream) throws IOException {
        int i = 0;
        switch (exifTag.getDataType()) {
            case 1:
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                byte[] bArr = new byte[exifTag.getComponentCount()];
                exifTag.getBytes(bArr);
                orderedDataOutputStream.write(bArr);
                break;
            case 2:
                byte[] stringByte = exifTag.getStringByte();
                if (stringByte.length == exifTag.getComponentCount()) {
                    stringByte[stringByte.length - 1] = 0;
                    orderedDataOutputStream.write(stringByte);
                    break;
                } else {
                    orderedDataOutputStream.write(stringByte);
                    orderedDataOutputStream.write(0);
                    break;
                }
            case 3:
                int componentCount = exifTag.getComponentCount();
                while (i < componentCount) {
                    orderedDataOutputStream.writeShort((short) exifTag.getValueAt(i));
                    i++;
                }
                break;
            case 4:
            case 9:
                int componentCount2 = exifTag.getComponentCount();
                while (i < componentCount2) {
                    orderedDataOutputStream.writeInt((int) exifTag.getValueAt(i));
                    i++;
                }
                break;
            case 5:
            case 10:
                int componentCount3 = exifTag.getComponentCount();
                while (i < componentCount3) {
                    orderedDataOutputStream.writeRational(exifTag.getRational(i));
                    i++;
                }
                break;
        }
    }
}
