package com.coremedia.iso.boxes;

/* loaded from: classes.dex */
public class VideoMediaHeaderBox extends AbstractMediaHeaderBox {
    private int graphicsmode;
    private int[] opcolor;

    public VideoMediaHeaderBox() {
        super("vmhd");
        this.graphicsmode = 0;
        this.opcolor = new int[]{0, 0, 0};
        setFlags(1);
    }

    public int getGraphicsmode() {
        return this.graphicsmode;
    }

    public int[] getOpcolor() {
        return this.opcolor;
    }

    public String toString() {
        return "VideoMediaHeaderBox[graphicsmode=" + getGraphicsmode() + ";opcolor0=" + getOpcolor()[0] + ";opcolor1=" + getOpcolor()[1] + ";opcolor2=" + getOpcolor()[2] + "]";
    }
}
