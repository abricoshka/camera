package android.support.p000v8.renderscript;

import android.content.res.Resources;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public class ScriptC extends Script {
    private static final String TAG = "ScriptC";

    protected ScriptC(long j, RenderScript renderScript) {
        super(j, renderScript);
    }

    protected ScriptC(RenderScript renderScript, Resources resources, int i) {
        super(0L, renderScript);
        long jInternalCreate = internalCreate(renderScript, resources, i);
        if (jInternalCreate == 0) {
            throw new RSRuntimeException("Loading of ScriptC script failed.");
        }
        setID(jInternalCreate);
    }

    protected ScriptC(RenderScript renderScript, String str, byte[] bArr, byte[] bArr2) {
        long jInternalStringCreate;
        super(0L, renderScript);
        if (RenderScript.sPointerSize == 4) {
            jInternalStringCreate = internalStringCreate(renderScript, str, bArr);
        } else {
            jInternalStringCreate = internalStringCreate(renderScript, str, bArr2);
        }
        if (jInternalStringCreate == 0) {
            throw new RSRuntimeException("Loading of ScriptC script failed.");
        }
        setID(jInternalStringCreate);
    }

    private static synchronized long internalCreate(RenderScript renderScript, Resources resources, int i) {
        int i2;
        byte[] bArr;
        InputStream inputStreamOpenRawResource = resources.openRawResource(i);
        try {
            try {
                byte[] bArr2 = new byte[1024];
                i2 = 0;
                while (true) {
                    int length = bArr2.length - i2;
                    if (length == 0) {
                        bArr = new byte[bArr2.length * 2];
                        System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
                        length = bArr.length - i2;
                    } else {
                        bArr = bArr2;
                    }
                    int i3 = inputStreamOpenRawResource.read(bArr, i2, length);
                    if (i3 > 0) {
                        i2 = i3 + i2;
                        bArr2 = bArr;
                    }
                }
            } finally {
                inputStreamOpenRawResource.close();
            }
        } catch (IOException e) {
            throw new Resources.NotFoundException();
        }
        return renderScript.nScriptCCreate(resources.getResourceEntryName(i), renderScript.getApplicationContext().getCacheDir().toString(), bArr, i2);
    }

    private static synchronized long internalStringCreate(RenderScript renderScript, String str, byte[] bArr) {
        return renderScript.nScriptCCreate(str, renderScript.getApplicationContext().getCacheDir().toString(), bArr, bArr.length);
    }
}
