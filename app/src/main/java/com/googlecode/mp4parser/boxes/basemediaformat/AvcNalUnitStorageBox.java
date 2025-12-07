package com.googlecode.mp4parser.boxes.basemediaformat;

import com.coremedia.iso.boxes.h264.AvcConfigurationBox;
import com.googlecode.mp4parser.AbstractBox;

/* loaded from: classes.dex */
public class AvcNalUnitStorageBox extends AbstractBox {
    AvcConfigurationBox.AVCDecoderConfigurationRecord avcDecoderConfigurationRecord;

    public AvcNalUnitStorageBox() {
        super("avcn");
    }

    public String toString() {
        return "AvcNalUnitStorageBox{SPS=" + this.avcDecoderConfigurationRecord.getSequenceParameterSetsAsStrings() + ",PPS=" + this.avcDecoderConfigurationRecord.getPictureParameterSetsAsStrings() + ",lengthSize=" + (this.avcDecoderConfigurationRecord.lengthSizeMinusOne + 1) + '}';
    }
}
