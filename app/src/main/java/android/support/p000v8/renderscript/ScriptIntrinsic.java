package android.support.p000v8.renderscript;

/* loaded from: classes.dex */
public abstract class ScriptIntrinsic extends Script {
    ScriptIntrinsic(long j, RenderScript renderScript) {
        super(j, renderScript);
        if (j == 0) {
            throw new RSRuntimeException("Loading of ScriptIntrinsic failed.");
        }
    }
}
