package com.mediatek.camera.p005v2.exif;

import android.support.v4.app.FrameMetricsAggregator;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
class ExifParser {
    private int mApp1End;
    private boolean mContainExifData;
    private byte[] mDataAboveIfd0;
    private int mIfd0Position;
    private int mIfdType;
    private ImageEvent mImageEvent;
    private final ExifInterface mInterface;
    private ExifTag mJpegSizeTag;
    private boolean mNeedToParseOffsetsInCurrentIfd;
    private final int mOptions;
    private ExifTag mStripSizeTag;
    private ExifTag mTag;
    private int mTiffStartPosition;
    private final CountedDataInputStream mTiffStream;
    private static final Charset US_ASCII = Charset.forName("US-ASCII");
    private static final short TAG_EXIF_IFD = ExifInterface.getTrueTagKey(ExifInterface.TAG_EXIF_IFD);
    private static final short TAG_GPS_IFD = ExifInterface.getTrueTagKey(ExifInterface.TAG_GPS_IFD);
    private static final short TAG_INTEROPERABILITY_IFD = ExifInterface.getTrueTagKey(ExifInterface.TAG_INTEROPERABILITY_IFD);
    private static final short TAG_JPEG_INTERCHANGE_FORMAT = ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT);
    private static final short TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = ExifInterface.getTrueTagKey(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
    private static final short TAG_STRIP_OFFSETS = ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_OFFSETS);
    private static final short TAG_STRIP_BYTE_COUNTS = ExifInterface.getTrueTagKey(ExifInterface.TAG_STRIP_BYTE_COUNTS);
    private int mIfdStartOffset = 0;
    private int mNumOfTagInIfd = 0;
    private int mOffsetToApp1EndFromSOF = 0;
    private final TreeMap<Integer, Object> mCorrespondingEvent = new TreeMap<>();

    private boolean isIfdRequested(int i) {
        switch (i) {
            case 0:
                return (this.mOptions & 1) != 0;
            case 1:
                return (this.mOptions & 2) != 0;
            case 2:
                return (this.mOptions & 4) != 0;
            case 3:
                return (this.mOptions & 16) != 0;
            case 4:
                return (this.mOptions & 8) != 0;
            default:
                return false;
        }
    }

    private boolean isThumbnailRequested() {
        return (this.mOptions & 32) != 0;
    }

    private ExifParser(InputStream inputStream, int i, ExifInterface exifInterface) throws IOException, ExifInvalidFormatException {
        this.mContainExifData = false;
        if (inputStream == null) {
            throw new IOException("Null argument inputStream to ExifParser");
        }
        this.mInterface = exifInterface;
        this.mContainExifData = seekTiffData(inputStream);
        this.mTiffStream = new CountedDataInputStream(inputStream);
        this.mOptions = i;
        if (!this.mContainExifData) {
            return;
        }
        parseTiffHeader();
        long unsignedInt = this.mTiffStream.readUnsignedInt();
        if (unsignedInt > 2147483647L) {
            throw new ExifInvalidFormatException("Invalid offset " + unsignedInt);
        }
        this.mIfd0Position = (int) unsignedInt;
        this.mIfdType = 0;
        if (isIfdRequested(0) || needToParseOffsetsInCurrentIfd()) {
            registerIfd(0, unsignedInt);
            if (unsignedInt != 8) {
                this.mDataAboveIfd0 = new byte[((int) unsignedInt) - 8];
                read(this.mDataAboveIfd0);
            }
        }
    }

    protected static ExifParser parse(InputStream inputStream, ExifInterface exifInterface) throws IOException, ExifInvalidFormatException {
        return new ExifParser(inputStream, 63, exifInterface);
    }

    protected int next() throws IOException, ExifInvalidFormatException {
        if (!this.mContainExifData) {
            return 5;
        }
        int readByteCount = this.mTiffStream.getReadByteCount();
        int i = this.mIfdStartOffset + 2 + (this.mNumOfTagInIfd * 12);
        if (readByteCount < i) {
            this.mTag = readTag();
            if (this.mTag == null) {
                return next();
            }
            if (this.mNeedToParseOffsetsInCurrentIfd) {
                checkOffsetOrImageTag(this.mTag);
            }
            return 1;
        }
        if (readByteCount == i) {
            if (this.mIfdType == 0) {
                long unsignedLong = readUnsignedLong();
                if ((isIfdRequested(1) || isThumbnailRequested()) && unsignedLong != 0) {
                    registerIfd(1, unsignedLong);
                }
            } else {
                int iIntValue = this.mCorrespondingEvent.size() > 0 ? this.mCorrespondingEvent.firstEntry().getKey().intValue() - this.mTiffStream.getReadByteCount() : 4;
                if (iIntValue < 4) {
                    Log.w("ExifParser", "Invalid size of link to next IFD: " + iIntValue);
                } else {
                    long unsignedLong2 = readUnsignedLong();
                    if (unsignedLong2 != 0) {
                        Log.w("ExifParser", "Invalid link to next IFD: " + unsignedLong2);
                    }
                }
            }
        }
        while (this.mCorrespondingEvent.size() != 0) {
            Map.Entry<Integer, Object> entryPollFirstEntry = this.mCorrespondingEvent.pollFirstEntry();
            Object value = entryPollFirstEntry.getValue();
            try {
                skipTo(entryPollFirstEntry.getKey().intValue());
                if (value instanceof IfdEvent) {
                    this.mIfdType = ((IfdEvent) value).ifd;
                    this.mNumOfTagInIfd = this.mTiffStream.readUnsignedShort();
                    this.mIfdStartOffset = entryPollFirstEntry.getKey().intValue();
                    if ((this.mNumOfTagInIfd * 12) + this.mIfdStartOffset + 2 > this.mApp1End) {
                        Log.w("ExifParser", "Invalid size of IFD " + this.mIfdType);
                        return 5;
                    }
                    this.mNeedToParseOffsetsInCurrentIfd = needToParseOffsetsInCurrentIfd();
                    if (((IfdEvent) value).isRequested) {
                        return 0;
                    }
                    skipRemainingTagsInCurrentIfd();
                } else {
                    if (value instanceof ImageEvent) {
                        this.mImageEvent = (ImageEvent) value;
                        return this.mImageEvent.type;
                    }
                    ExifTagEvent exifTagEvent = (ExifTagEvent) value;
                    this.mTag = exifTagEvent.tag;
                    if (this.mTag.getDataType() != 7) {
                        readFullTagValue(this.mTag);
                        checkOffsetOrImageTag(this.mTag);
                    }
                    if (exifTagEvent.isRequested) {
                        return 2;
                    }
                }
            } catch (IOException e) {
                Log.w("ExifParser", "Failed to skip to data at: " + entryPollFirstEntry.getKey() + " for " + value.getClass().getName() + ", the file may be broken.");
            }
        }
        return 5;
    }

    protected void skipRemainingTagsInCurrentIfd() throws IOException, ExifInvalidFormatException {
        int i = (this.mNumOfTagInIfd * 12) + this.mIfdStartOffset + 2;
        int readByteCount = this.mTiffStream.getReadByteCount();
        if (readByteCount > i) {
            return;
        }
        if (this.mNeedToParseOffsetsInCurrentIfd) {
            while (readByteCount < i) {
                this.mTag = readTag();
                readByteCount += 12;
                if (this.mTag != null) {
                    checkOffsetOrImageTag(this.mTag);
                }
            }
        } else {
            skipTo(i);
        }
        long unsignedLong = readUnsignedLong();
        if (this.mIfdType == 0) {
            if ((isIfdRequested(1) || isThumbnailRequested()) && unsignedLong > 0) {
                registerIfd(1, unsignedLong);
            }
        }
    }

    private boolean needToParseOffsetsInCurrentIfd() {
        switch (this.mIfdType) {
            case 0:
                if (isIfdRequested(2) || isIfdRequested(4) || isIfdRequested(3)) {
                    return true;
                }
                return isIfdRequested(1);
            case 1:
                return isThumbnailRequested();
            case 2:
                return isIfdRequested(3);
            default:
                return false;
        }
    }

    protected ExifTag getTag() {
        return this.mTag;
    }

    protected int getCurrentIfd() {
        return this.mIfdType;
    }

    protected int getStripIndex() {
        return this.mImageEvent.stripIndex;
    }

    protected int getStripSize() {
        if (this.mStripSizeTag == null) {
            return 0;
        }
        return (int) this.mStripSizeTag.getValueAt(0);
    }

    protected int getCompressedImageSize() {
        if (this.mJpegSizeTag == null) {
            return 0;
        }
        return (int) this.mJpegSizeTag.getValueAt(0);
    }

    private void skipTo(int i) throws IOException {
        this.mTiffStream.skipTo(i);
        while (!this.mCorrespondingEvent.isEmpty() && this.mCorrespondingEvent.firstKey().intValue() < i) {
            this.mCorrespondingEvent.pollFirstEntry();
        }
    }

    protected void registerForTagValue(ExifTag exifTag) {
        if (exifTag.getOffset() >= this.mTiffStream.getReadByteCount()) {
            this.mCorrespondingEvent.put(Integer.valueOf(exifTag.getOffset()), new ExifTagEvent(exifTag, true));
        }
    }

    private void registerIfd(int i, long j) {
        this.mCorrespondingEvent.put(Integer.valueOf((int) j), new IfdEvent(i, isIfdRequested(i)));
    }

    private void registerCompressedImage(long j) {
        this.mCorrespondingEvent.put(Integer.valueOf((int) j), new ImageEvent(3));
    }

    private void registerUncompressedStrip(int i, long j) {
        this.mCorrespondingEvent.put(Integer.valueOf((int) j), new ImageEvent(4, i));
    }

    private ExifTag readTag() throws IOException, ExifInvalidFormatException {
        short s = this.mTiffStream.readShort();
        short s2 = this.mTiffStream.readShort();
        long unsignedInt = this.mTiffStream.readUnsignedInt();
        if (unsignedInt > 2147483647L) {
            throw new ExifInvalidFormatException("Number of component is larger then Integer.MAX_VALUE");
        }
        if (!ExifTag.isValidType(s2)) {
            Log.w("ExifParser", String.format("Tag %04x: Invalid data type %d", Short.valueOf(s), Short.valueOf(s2)));
            this.mTiffStream.skip(4L);
            return null;
        }
        ExifTag exifTag = new ExifTag(s, s2, (int) unsignedInt, this.mIfdType, ((int) unsignedInt) != 0);
        if (exifTag.getDataSize() > 4) {
            long unsignedInt2 = this.mTiffStream.readUnsignedInt();
            if (unsignedInt2 > 2147483647L) {
                throw new ExifInvalidFormatException("offset is larger then Integer.MAX_VALUE");
            }
            if (unsignedInt2 < this.mIfd0Position && s2 == 7) {
                byte[] bArr = new byte[(int) unsignedInt];
                System.arraycopy(this.mDataAboveIfd0, ((int) unsignedInt2) - 8, bArr, 0, (int) unsignedInt);
                exifTag.setValue(bArr);
            } else {
                exifTag.setOffset((int) unsignedInt2);
            }
        } else {
            boolean zHasDefinedCount = exifTag.hasDefinedCount();
            exifTag.setHasDefinedCount(false);
            readFullTagValue(exifTag);
            exifTag.setHasDefinedCount(zHasDefinedCount);
            this.mTiffStream.skip(4 - r1);
            exifTag.setOffset(this.mTiffStream.getReadByteCount() - 4);
        }
        return exifTag;
    }

    private void checkOffsetOrImageTag(ExifTag exifTag) {
        if (exifTag.getComponentCount() == 0) {
            return;
        }
        short tagId = exifTag.getTagId();
        int ifd = exifTag.getIfd();
        if (tagId == TAG_EXIF_IFD && checkAllowed(ifd, ExifInterface.TAG_EXIF_IFD)) {
            if (isIfdRequested(2) || isIfdRequested(3)) {
                registerIfd(2, exifTag.getValueAt(0));
                return;
            }
            return;
        }
        if (tagId == TAG_GPS_IFD && checkAllowed(ifd, ExifInterface.TAG_GPS_IFD)) {
            if (isIfdRequested(4)) {
                registerIfd(4, exifTag.getValueAt(0));
                return;
            }
            return;
        }
        if (tagId == TAG_INTEROPERABILITY_IFD && checkAllowed(ifd, ExifInterface.TAG_INTEROPERABILITY_IFD)) {
            if (isIfdRequested(3)) {
                registerIfd(3, exifTag.getValueAt(0));
                return;
            }
            return;
        }
        if (tagId == TAG_JPEG_INTERCHANGE_FORMAT && checkAllowed(ifd, ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT)) {
            if (isThumbnailRequested()) {
                registerCompressedImage(exifTag.getValueAt(0));
                return;
            }
            return;
        }
        if (tagId == TAG_JPEG_INTERCHANGE_FORMAT_LENGTH && checkAllowed(ifd, ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH)) {
            if (isThumbnailRequested()) {
                this.mJpegSizeTag = exifTag;
                return;
            }
            return;
        }
        if (tagId != TAG_STRIP_OFFSETS || !checkAllowed(ifd, ExifInterface.TAG_STRIP_OFFSETS)) {
            if (tagId == TAG_STRIP_BYTE_COUNTS && checkAllowed(ifd, ExifInterface.TAG_STRIP_BYTE_COUNTS) && isThumbnailRequested() && exifTag.hasValue()) {
                this.mStripSizeTag = exifTag;
                return;
            }
            return;
        }
        if (isThumbnailRequested()) {
            if (!exifTag.hasValue()) {
                this.mCorrespondingEvent.put(Integer.valueOf(exifTag.getOffset()), new ExifTagEvent(exifTag, false));
                return;
            }
            for (int i = 0; i < exifTag.getComponentCount(); i++) {
                if (exifTag.getDataType() == 3) {
                    registerUncompressedStrip(i, exifTag.getValueAt(i));
                } else {
                    registerUncompressedStrip(i, exifTag.getValueAt(i));
                }
            }
        }
    }

    private boolean checkAllowed(int i, int i2) {
        int i3 = this.mInterface.getTagInfo().get(i2);
        if (i3 == 0) {
            return false;
        }
        return ExifInterface.isIfdAllowed(i3, i);
    }

    protected void readFullTagValue(ExifTag exifTag) throws IOException {
        int i = 0;
        short dataType = exifTag.getDataType();
        if (dataType == 2 || dataType == 7 || dataType == 1) {
            int componentCount = exifTag.getComponentCount();
            if (this.mCorrespondingEvent.size() > 0 && this.mCorrespondingEvent.firstEntry().getKey().intValue() < componentCount + this.mTiffStream.getReadByteCount()) {
                Object value = this.mCorrespondingEvent.firstEntry().getValue();
                if (value instanceof ImageEvent) {
                    Log.w("ExifParser", "Thumbnail overlaps value for tag: \n" + exifTag.toString());
                    Log.w("ExifParser", "Invalid thumbnail offset: " + this.mCorrespondingEvent.pollFirstEntry().getKey());
                } else {
                    if (value instanceof IfdEvent) {
                        Log.w("ExifParser", "Ifd " + ((IfdEvent) value).ifd + " overlaps value for tag: \n" + exifTag.toString());
                    } else if (value instanceof ExifTagEvent) {
                        Log.w("ExifParser", "Tag value for tag: \n" + ((ExifTagEvent) value).tag.toString() + " overlaps value for tag: \n" + exifTag.toString());
                    }
                    int iIntValue = this.mCorrespondingEvent.firstEntry().getKey().intValue() - this.mTiffStream.getReadByteCount();
                    Log.w("ExifParser", "Invalid size of tag: \n" + exifTag.toString() + " setting count to: " + iIntValue);
                    exifTag.forceSetComponentCount(iIntValue);
                }
            }
        }
        switch (exifTag.getDataType()) {
            case 1:
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                byte[] bArr = new byte[exifTag.getComponentCount()];
                read(bArr);
                exifTag.setValue(bArr);
                break;
            case 2:
                exifTag.setValue(readString(exifTag.getComponentCount()));
                break;
            case 3:
                int[] iArr = new int[exifTag.getComponentCount()];
                int length = iArr.length;
                while (i < length) {
                    iArr[i] = readUnsignedShort();
                    i++;
                }
                exifTag.setValue(iArr);
                break;
            case 4:
                long[] jArr = new long[exifTag.getComponentCount()];
                int length2 = jArr.length;
                while (i < length2) {
                    jArr[i] = readUnsignedLong();
                    i++;
                }
                exifTag.setValue(jArr);
                break;
            case 5:
                Rational[] rationalArr = new Rational[exifTag.getComponentCount()];
                int length3 = rationalArr.length;
                while (i < length3) {
                    rationalArr[i] = readUnsignedRational();
                    i++;
                }
                exifTag.setValue(rationalArr);
                break;
            case 9:
                int[] iArr2 = new int[exifTag.getComponentCount()];
                int length4 = iArr2.length;
                while (i < length4) {
                    iArr2[i] = readLong();
                    i++;
                }
                exifTag.setValue(iArr2);
                break;
            case 10:
                Rational[] rationalArr2 = new Rational[exifTag.getComponentCount()];
                int length5 = rationalArr2.length;
                for (int i2 = 0; i2 < length5; i2++) {
                    rationalArr2[i2] = readRational();
                }
                exifTag.setValue(rationalArr2);
                break;
        }
    }

    private void parseTiffHeader() throws IOException, ExifInvalidFormatException {
        short s = this.mTiffStream.readShort();
        if (18761 == s) {
            this.mTiffStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        } else if (19789 == s) {
            this.mTiffStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        } else {
            throw new ExifInvalidFormatException("Invalid TIFF header");
        }
        if (this.mTiffStream.readShort() != 42) {
            throw new ExifInvalidFormatException("Invalid TIFF header");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0077, code lost:
    
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean seekTiffData(java.io.InputStream r8) throws java.io.IOException, com.mediatek.camera.p005v2.exif.ExifInvalidFormatException {
        /*
            r7 = this;
            r6 = 0
            com.mediatek.camera.v2.exif.CountedDataInputStream r2 = new com.mediatek.camera.v2.exif.CountedDataInputStream
            r2.<init>(r8)
            short r0 = r2.readShort()
            r1 = -40
            if (r0 == r1) goto L17
            com.mediatek.camera.v2.exif.ExifInvalidFormatException r0 = new com.mediatek.camera.v2.exif.ExifInvalidFormatException
            java.lang.String r1 = "Invalid JPEG format"
            r0.<init>(r1)
            throw r0
        L17:
            short r0 = r2.readShort()
            r1 = r0
        L1c:
            r0 = -39
            if (r1 == r0) goto L77
            boolean r0 = com.mediatek.camera.p005v2.exif.JpegHeader.isSofMarker(r1)
            r0 = r0 ^ 1
            if (r0 == 0) goto L77
            int r0 = r2.readUnsignedShort()
            r3 = -31
            if (r1 != r3) goto L56
            r1 = 8
            if (r0 < r1) goto L56
            int r1 = r2.readInt()
            short r3 = r2.readShort()
            int r0 = r0 + (-6)
            r4 = 1165519206(0x45786966, float:3974.5874)
            if (r1 != r4) goto L56
            if (r3 != 0) goto L56
            int r1 = r2.getReadByteCount()
            r7.mTiffStartPosition = r1
            r7.mApp1End = r0
            int r0 = r7.mTiffStartPosition
            int r1 = r7.mApp1End
            int r0 = r0 + r1
            r7.mOffsetToApp1EndFromSOF = r0
            r0 = 1
            return r0
        L56:
            r1 = 2
            if (r0 < r1) goto L67
            int r1 = r0 + (-2)
            long r4 = (long) r1
            int r0 = r0 + (-2)
            long r0 = (long) r0
            long r0 = r2.skip(r0)
            int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r0 == 0) goto L71
        L67:
            java.lang.String r0 = "ExifParser"
            java.lang.String r1 = "Invalid JPEG format."
            android.util.Log.w(r0, r1)
            return r6
        L71:
            short r0 = r2.readShort()
            r1 = r0
            goto L1c
        L77:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.camera.p005v2.exif.ExifParser.seekTiffData(java.io.InputStream):boolean");
    }

    protected int read(byte[] bArr) throws IOException {
        return this.mTiffStream.read(bArr);
    }

    protected String readString(int i) throws IOException {
        return readString(i, US_ASCII);
    }

    protected String readString(int i, Charset charset) throws IOException {
        if (i > 0) {
            return this.mTiffStream.readString(i, charset);
        }
        return "";
    }

    protected int readUnsignedShort() throws IOException {
        return this.mTiffStream.readShort() & 65535;
    }

    protected long readUnsignedLong() throws IOException {
        return readLong() & 4294967295L;
    }

    protected Rational readUnsignedRational() throws IOException {
        return new Rational(readUnsignedLong(), readUnsignedLong());
    }

    protected int readLong() throws IOException {
        return this.mTiffStream.readInt();
    }

    protected Rational readRational() throws IOException {
        return new Rational(readLong(), readLong());
    }

    private static class ImageEvent {
        int stripIndex;
        int type;

        ImageEvent(int i) {
            this.stripIndex = 0;
            this.type = i;
        }

        ImageEvent(int i, int i2) {
            this.type = i;
            this.stripIndex = i2;
        }
    }

    private static class IfdEvent {
        int ifd;
        boolean isRequested;

        IfdEvent(int i, boolean z) {
            this.ifd = i;
            this.isRequested = z;
        }
    }

    private static class ExifTagEvent {
        boolean isRequested;
        ExifTag tag;

        ExifTagEvent(ExifTag exifTag, boolean z) {
            this.tag = exifTag;
            this.isRequested = z;
        }
    }

    protected ByteOrder getByteOrder() {
        return this.mTiffStream.getByteOrder();
    }
}
