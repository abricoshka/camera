package com.coremedia.iso.boxes.h264;

import com.coremedia.iso.Hex;
import com.googlecode.mp4parser.AbstractBox;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class AvcConfigurationBox extends AbstractBox {
    public AVCDecoderConfigurationRecord avcDecoderConfigurationRecord;

    public AvcConfigurationBox() {
        super("avcC");
        this.avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord();
    }

    public static class AVCDecoderConfigurationRecord {
        public int lengthSizeMinusOne;
        public List<byte[]> sequenceParameterSets = new ArrayList();
        public List<byte[]> pictureParameterSets = new ArrayList();
        public boolean hasExts = true;
        public int chromaFormat = 1;
        public int bitDepthLumaMinus8 = 0;
        public int bitDepthChromaMinus8 = 0;
        public List<byte[]> sequenceParameterSetExts = new ArrayList();
        public int lengthSizeMinusOnePaddingBits = 60;
        public int numberOfSequenceParameterSetsPaddingBits = 7;
        public int chromaFormatPaddingBits = 31;
        public int bitDepthLumaMinus8PaddingBits = 31;
        public int bitDepthChromaMinus8PaddingBits = 31;

        public List<String> getSequenceParameterSetsAsStrings() {
            ArrayList arrayList = new ArrayList(this.sequenceParameterSets.size());
            Iterator<T> it = this.sequenceParameterSets.iterator();
            while (it.hasNext()) {
                arrayList.add(Hex.encodeHex((byte[]) it.next()));
            }
            return arrayList;
        }

        public List<String> getPictureParameterSetsAsStrings() {
            ArrayList arrayList = new ArrayList(this.pictureParameterSets.size());
            Iterator<T> it = this.pictureParameterSets.iterator();
            while (it.hasNext()) {
                arrayList.add(Hex.encodeHex((byte[]) it.next()));
            }
            return arrayList;
        }
    }
}
