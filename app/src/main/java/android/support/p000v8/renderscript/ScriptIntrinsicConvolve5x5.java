package android.support.p000v8.renderscript;

import android.os.Build;
import android.support.p000v8.renderscript.Script;

/* loaded from: classes.dex */
public class ScriptIntrinsicConvolve5x5 extends ScriptIntrinsic {
    private static final int INTRINSIC_API_LEVEL = 19;
    private Allocation mInput;
    private final float[] mValues;

    ScriptIntrinsicConvolve5x5(long j, RenderScript renderScript) {
        super(j, renderScript);
        this.mValues = new float[25];
    }

    public static ScriptIntrinsicConvolve5x5 create(RenderScript renderScript, Element element) {
        boolean z = false;
        if (!element.isCompatible(Element.m4U8(renderScript)) && (!element.isCompatible(Element.U8_2(renderScript))) && (!element.isCompatible(Element.U8_3(renderScript))) && (!element.isCompatible(Element.U8_4(renderScript))) && (!element.isCompatible(Element.F32(renderScript))) && (!element.isCompatible(Element.F32_2(renderScript))) && (!element.isCompatible(Element.F32_3(renderScript))) && (!element.isCompatible(Element.F32_4(renderScript)))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        if (renderScript.isUseNative() && Build.VERSION.SDK_INT < INTRINSIC_API_LEVEL) {
            z = true;
        }
        ScriptIntrinsicConvolve5x5 scriptIntrinsicConvolve5x5 = new ScriptIntrinsicConvolve5x5(renderScript.nScriptIntrinsicCreate(4, element.getID(renderScript), z), renderScript);
        scriptIntrinsicConvolve5x5.setIncSupp(z);
        return scriptIntrinsicConvolve5x5;
    }

    public void setInput(Allocation allocation) {
        this.mInput = allocation;
        setVar(1, allocation);
    }

    public void setCoefficients(float[] fArr) {
        FieldPacker fieldPacker = new FieldPacker(100);
        for (int i = 0; i < this.mValues.length; i++) {
            this.mValues[i] = fArr[i];
            fieldPacker.addF32(this.mValues[i]);
        }
        setVar(0, fieldPacker);
    }

    public void forEach(Allocation allocation) {
        forEach(0, (Allocation) null, allocation, (FieldPacker) null);
    }

    public void forEach(Allocation allocation, Script.LaunchOptions launchOptions) {
        forEach(0, (Allocation) null, allocation, (FieldPacker) null, launchOptions);
    }

    public Script.KernelID getKernelID() {
        return createKernelID(0, 2, null, null);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(1, null);
    }
}
