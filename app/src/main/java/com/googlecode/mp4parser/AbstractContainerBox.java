package com.googlecode.mp4parser;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ContainerBox;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/* loaded from: classes.dex */
public abstract class AbstractContainerBox extends AbstractBox implements ContainerBox {
    private static Logger LOG = Logger.getLogger(AbstractContainerBox.class.getName());
    protected List<Box> boxes;

    public AbstractContainerBox(String str) {
        super(str);
        this.boxes = new LinkedList();
    }

    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("[");
        while (true) {
            int i2 = i;
            if (i2 < this.boxes.size()) {
                if (i2 > 0) {
                    sb.append(";");
                }
                sb.append(this.boxes.get(i2).toString());
                i = i2 + 1;
            } else {
                sb.append("]");
                return sb.toString();
            }
        }
    }
}
