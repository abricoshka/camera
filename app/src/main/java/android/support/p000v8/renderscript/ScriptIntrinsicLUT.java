package android.support.p000v8.renderscript;

import android.os.Build;
import android.support.p000v8.renderscript.Script;
import android.support.v4.app.NotificationCompat;

/* loaded from: classes.dex */
public class ScriptIntrinsicLUT extends ScriptIntrinsic {
    private static final int INTRINSIC_API_LEVEL = 19;
    private final byte[] mCache;
    private boolean mDirty;
    private final Matrix4f mMatrix;
    private Allocation mTables;

    protected ScriptIntrinsicLUT(long j, RenderScript renderScript) {
        super(j, renderScript);
        this.mMatrix = new Matrix4f();
        this.mCache = new byte[1024];
        this.mDirty = true;
    }

    public static ScriptIntrinsicLUT create(RenderScript renderScript, Element element) {
        boolean z = renderScript.isUseNative() && Build.VERSION.SDK_INT < INTRINSIC_API_LEVEL;
        ScriptIntrinsicLUT scriptIntrinsicLUT = new ScriptIntrinsicLUT(renderScript.nScriptIntrinsicCreate(3, element.getID(renderScript), z), renderScript);
        scriptIntrinsicLUT.setIncSupp(z);
        scriptIntrinsicLUT.mTables = Allocation.createSized(renderScript, Element.m4U8(renderScript), 1024);
        for (int i = 0; i < 256; i++) {
            scriptIntrinsicLUT.mCache[i] = (byte) i;
            scriptIntrinsicLUT.mCache[i + 256] = (byte) i;
            scriptIntrinsicLUT.mCache[i + NotificationCompat.FLAG_GROUP_SUMMARY] = (byte) i;
            scriptIntrinsicLUT.mCache[i + 768] = (byte) i;
        }
        scriptIntrinsicLUT.setVar(0, scriptIntrinsicLUT.mTables);
        return scriptIntrinsicLUT;
    }

    private void validate(int i, int i2) {
        if (i < 0 || i > 255) {
            throw new RSIllegalArgumentException("Index out of range (0-255).");
        }
        if (i2 < 0 || i2 > 255) {
            throw new RSIllegalArgumentException("Value out of range (0-255).");
        }
    }

    public void setRed(int i, int i2) {
        validate(i, i2);
        this.mCache[i] = (byte) i2;
        this.mDirty = true;
    }

    public void setGreen(int i, int i2) {
        validate(i, i2);
        this.mCache[i + 256] = (byte) i2;
        this.mDirty = true;
    }

    public void setBlue(int i, int i2) {
        validate(i, i2);
        this.mCache[i + NotificationCompat.FLAG_GROUP_SUMMARY] = (byte) i2;
        this.mDirty = true;
    }

    public void setAlpha(int i, int i2) {
        validate(i, i2);
        this.mCache[i + 768] = (byte) i2;
        this.mDirty = true;
    }

    public void forEach(Allocation allocation, Allocation allocation2) {
        if (this.mDirty) {
            this.mDirty = false;
            this.mTables.copyFromUnchecked(this.mCache);
        }
        forEach(0, allocation, allocation2, (FieldPacker) null);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 3, null, null);
    }
}
