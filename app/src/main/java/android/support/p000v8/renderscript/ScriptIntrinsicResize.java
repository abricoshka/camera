package android.support.p000v8.renderscript;

import android.os.Build;
import android.support.p000v8.renderscript.Script;

/* loaded from: classes.dex */
public class ScriptIntrinsicResize extends ScriptIntrinsic {
    private static final int INTRINSIC_API_LEVEL = 21;
    private Allocation mInput;

    protected ScriptIntrinsicResize(long j, RenderScript renderScript) {
        super(j, renderScript);
    }

    public static ScriptIntrinsicResize create(RenderScript renderScript) {
        boolean z = false;
        if (renderScript.isUseNative() && Build.VERSION.SDK_INT < INTRINSIC_API_LEVEL) {
            z = true;
        }
        ScriptIntrinsicResize scriptIntrinsicResize = new ScriptIntrinsicResize(renderScript.nScriptIntrinsicCreate(12, 0L, z), renderScript);
        scriptIntrinsicResize.setIncSupp(z);
        return scriptIntrinsicResize;
    }

    public void setInput(Allocation allocation) {
        if (!allocation.getElement().isCompatible(Element.m4U8(this.mRS)) && (!r0.isCompatible(Element.U8_2(this.mRS))) && (!r0.isCompatible(Element.U8_3(this.mRS))) && (!r0.isCompatible(Element.U8_4(this.mRS))) && (!r0.isCompatible(Element.F32(this.mRS))) && (!r0.isCompatible(Element.F32_2(this.mRS))) && (!r0.isCompatible(Element.F32_3(this.mRS))) && (!r0.isCompatible(Element.F32_4(this.mRS)))) {
            throw new RSIllegalArgumentException("Unsupported element type.");
        }
        this.mInput = allocation;
        setVar(0, allocation);
    }

    public Script.FieldID getFieldID_Input() {
        return createFieldID(0, null);
    }

    public void forEach_bicubic(Allocation allocation) {
        if (allocation == this.mInput) {
            throw new RSIllegalArgumentException("Output cannot be same as Input.");
        }
        forEach_bicubic(allocation, null);
    }

    public void forEach_bicubic(Allocation allocation, Script.LaunchOptions launchOptions) {
        forEach(0, (Allocation) null, allocation, (FieldPacker) null, launchOptions);
    }

    public Script.KernelID getKernelID_bicubic() {
        return createKernelID(0, 2, null, null);
    }
}
