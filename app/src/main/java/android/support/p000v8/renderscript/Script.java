package android.support.p000v8.renderscript;

import android.renderscript.Script;
import android.util.SparseArray;
import java.io.UnsupportedEncodingException;

/* loaded from: classes.dex */
public class Script extends BaseObj {
    private final SparseArray<FieldID> mFIDs;
    private final SparseArray<InvokeID> mIIDs;
    private final SparseArray<KernelID> mKIDs;
    private boolean mUseIncSupp;

    protected void setIncSupp(boolean z) {
        this.mUseIncSupp = z;
    }

    protected boolean isIncSupp() {
        return this.mUseIncSupp;
    }

    long getDummyAlloc(Allocation allocation) {
        if (allocation == null) {
            return 0L;
        }
        Type type = allocation.getType();
        long jNIncAllocationCreateTyped = this.mRS.nIncAllocationCreateTyped(allocation.getID(this.mRS), type.getDummyType(this.mRS, type.getElement().getDummyElement(this.mRS)), type.getX() * type.getElement().getBytesSize());
        allocation.setIncAllocID(jNIncAllocationCreateTyped);
        return jNIncAllocationCreateTyped;
    }

    public static final class KernelID extends BaseObj {

        /* renamed from: mN */
        Script.KernelID f48mN;
        Script mScript;
        int mSig;
        int mSlot;

        KernelID(long j, RenderScript renderScript, Script script, int i, int i2) {
            super(j, renderScript);
            this.mScript = script;
            this.mSlot = i;
            this.mSig = i2;
        }
    }

    protected KernelID createKernelID(int i, int i2, Element element, Element element2) {
        KernelID kernelID = this.mKIDs.get(i);
        if (kernelID != null) {
            return kernelID;
        }
        long jNScriptKernelIDCreate = this.mRS.nScriptKernelIDCreate(getID(this.mRS), i, i2, this.mUseIncSupp);
        if (jNScriptKernelIDCreate == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        KernelID kernelID2 = new KernelID(jNScriptKernelIDCreate, this.mRS, this, i, i2);
        this.mKIDs.put(i, kernelID2);
        return kernelID2;
    }

    public static final class InvokeID extends BaseObj {
        Script mScript;
        int mSlot;

        InvokeID(long j, RenderScript renderScript, Script script, int i) {
            super(j, renderScript);
            this.mScript = script;
            this.mSlot = i;
        }
    }

    protected InvokeID createInvokeID(int i) {
        InvokeID invokeID = this.mIIDs.get(i);
        if (invokeID != null) {
            return invokeID;
        }
        long jNScriptInvokeIDCreate = this.mRS.nScriptInvokeIDCreate(getID(this.mRS), i);
        if (jNScriptInvokeIDCreate == 0) {
            throw new RSDriverException("Failed to create KernelID");
        }
        InvokeID invokeID2 = new InvokeID(jNScriptInvokeIDCreate, this.mRS, this, i);
        this.mIIDs.put(i, invokeID2);
        return invokeID2;
    }

    public static final class FieldID extends BaseObj {

        /* renamed from: mN */
        Script.FieldID f47mN;
        Script mScript;
        int mSlot;

        FieldID(long j, RenderScript renderScript, Script script, int i) {
            super(j, renderScript);
            this.mScript = script;
            this.mSlot = i;
        }
    }

    protected FieldID createFieldID(int i, Element element) {
        FieldID fieldID = this.mFIDs.get(i);
        if (fieldID != null) {
            return fieldID;
        }
        long jNScriptFieldIDCreate = this.mRS.nScriptFieldIDCreate(getID(this.mRS), i, this.mUseIncSupp);
        if (jNScriptFieldIDCreate == 0) {
            throw new RSDriverException("Failed to create FieldID");
        }
        FieldID fieldID2 = new FieldID(jNScriptFieldIDCreate, this.mRS, this, i);
        this.mFIDs.put(i, fieldID2);
        return fieldID2;
    }

    protected void invoke(int i) {
        this.mRS.nScriptInvoke(getID(this.mRS), i, this.mUseIncSupp);
    }

    protected void invoke(int i, FieldPacker fieldPacker) {
        if (fieldPacker != null) {
            this.mRS.nScriptInvokeV(getID(this.mRS), i, fieldPacker.getData(), this.mUseIncSupp);
        } else {
            this.mRS.nScriptInvoke(getID(this.mRS), i, this.mUseIncSupp);
        }
    }

    public void bindAllocation(Allocation allocation, int i) {
        this.mRS.validate();
        if (allocation != null) {
            this.mRS.nScriptBindAllocation(getID(this.mRS), allocation.getID(this.mRS), i, this.mUseIncSupp);
        } else {
            this.mRS.nScriptBindAllocation(getID(this.mRS), 0L, i, this.mUseIncSupp);
        }
    }

    public void setTimeZone(String str) {
        this.mRS.validate();
        try {
            this.mRS.nScriptSetTimeZone(getID(this.mRS), str.getBytes("UTF-8"), this.mUseIncSupp);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void forEach(int i, Allocation allocation, Allocation allocation2, FieldPacker fieldPacker) {
        if (allocation == null && allocation2 == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        long id = allocation != null ? allocation.getID(this.mRS) : 0L;
        long id2 = allocation2 != null ? allocation2.getID(this.mRS) : 0L;
        byte[] data = fieldPacker != null ? fieldPacker.getData() : null;
        if (this.mUseIncSupp) {
            this.mRS.nScriptForEach(getID(this.mRS), i, getDummyAlloc(allocation), getDummyAlloc(allocation2), data, this.mUseIncSupp);
        } else {
            this.mRS.nScriptForEach(getID(this.mRS), i, id, id2, data, this.mUseIncSupp);
        }
    }

    protected void forEach(int i, Allocation allocation, Allocation allocation2, FieldPacker fieldPacker, LaunchOptions launchOptions) {
        if (allocation == null && allocation2 == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        if (launchOptions == null) {
            forEach(i, allocation, allocation2, fieldPacker);
            return;
        }
        long id = 0;
        long id2 = 0;
        if (allocation != null) {
            id = allocation.getID(this.mRS);
        }
        if (allocation2 != null) {
            id2 = allocation2.getID(this.mRS);
        }
        byte[] data = null;
        if (fieldPacker != null) {
            data = fieldPacker.getData();
        }
        if (this.mUseIncSupp) {
            this.mRS.nScriptForEachClipped(getID(this.mRS), i, getDummyAlloc(allocation), getDummyAlloc(allocation2), data, launchOptions.xstart, launchOptions.xend, launchOptions.ystart, launchOptions.yend, launchOptions.zstart, launchOptions.zend, this.mUseIncSupp);
            return;
        }
        this.mRS.nScriptForEachClipped(getID(this.mRS), i, id, id2, data, launchOptions.xstart, launchOptions.xend, launchOptions.ystart, launchOptions.yend, launchOptions.zstart, launchOptions.zend, this.mUseIncSupp);
    }

    Script(long j, RenderScript renderScript) {
        super(j, renderScript);
        this.mKIDs = new SparseArray<>();
        this.mIIDs = new SparseArray<>();
        this.mFIDs = new SparseArray<>();
        this.mUseIncSupp = false;
    }

    protected void forEach(int i, Allocation[] allocationArr, Allocation allocation, FieldPacker fieldPacker) {
        forEach(i, allocationArr, allocation, fieldPacker, (LaunchOptions) null);
    }

    protected void forEach(int i, Allocation[] allocationArr, Allocation allocation, FieldPacker fieldPacker, LaunchOptions launchOptions) {
        long[] jArr;
        this.mRS.validate();
        if (allocationArr != null) {
            for (Allocation allocation2 : allocationArr) {
                this.mRS.validateObject(allocation2);
            }
        }
        this.mRS.validateObject(allocation);
        if (allocationArr == null && allocation == null) {
            throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
        }
        if (allocationArr != null) {
            jArr = new long[allocationArr.length];
            for (int i2 = 0; i2 < allocationArr.length; i2++) {
                jArr[i2] = allocationArr[i2].getID(this.mRS);
            }
        } else {
            jArr = null;
        }
        long id = 0;
        if (allocation != null) {
            id = allocation.getID(this.mRS);
        }
        this.mRS.nScriptForEach(getID(this.mRS), i, jArr, id, fieldPacker != null ? fieldPacker.getData() : null, launchOptions != null ? new int[]{launchOptions.xstart, launchOptions.xend, launchOptions.ystart, launchOptions.yend, launchOptions.zstart, launchOptions.zend} : null);
    }

    protected void reduce(int i, Allocation[] allocationArr, Allocation allocation, LaunchOptions launchOptions) {
        this.mRS.validate();
        if (allocationArr == null || allocationArr.length < 1) {
            throw new RSIllegalArgumentException("At least one input is required.");
        }
        if (allocation == null) {
            throw new RSIllegalArgumentException("aout is required to be non-null.");
        }
        for (Allocation allocation2 : allocationArr) {
            this.mRS.validateObject(allocation2);
        }
        long[] jArr = new long[allocationArr.length];
        for (int i2 = 0; i2 < allocationArr.length; i2++) {
            jArr[i2] = allocationArr[i2].getID(this.mRS);
        }
        this.mRS.nScriptReduce(getID(this.mRS), i, jArr, allocation.getID(this.mRS), launchOptions != null ? new int[]{launchOptions.xstart, launchOptions.xend, launchOptions.ystart, launchOptions.yend, launchOptions.zstart, launchOptions.zend} : null);
    }

    public void setVar(int i, float f) {
        this.mRS.nScriptSetVarF(getID(this.mRS), i, f, this.mUseIncSupp);
    }

    public void setVar(int i, double d) {
        this.mRS.nScriptSetVarD(getID(this.mRS), i, d, this.mUseIncSupp);
    }

    public void setVar(int i, int i2) {
        this.mRS.nScriptSetVarI(getID(this.mRS), i, i2, this.mUseIncSupp);
    }

    public void setVar(int i, long j) {
        this.mRS.nScriptSetVarJ(getID(this.mRS), i, j, this.mUseIncSupp);
    }

    public void setVar(int i, boolean z) {
        this.mRS.nScriptSetVarI(getID(this.mRS), i, z ? 1 : 0, this.mUseIncSupp);
    }

    public void setVar(int i, BaseObj baseObj) {
        if (this.mUseIncSupp) {
            this.mRS.nScriptSetVarObj(getID(this.mRS), i, baseObj != null ? getDummyAlloc((Allocation) baseObj) : 0L, this.mUseIncSupp);
        } else {
            this.mRS.nScriptSetVarObj(getID(this.mRS), i, baseObj != null ? baseObj.getID(this.mRS) : 0L, this.mUseIncSupp);
        }
    }

    public void setVar(int i, FieldPacker fieldPacker) {
        this.mRS.nScriptSetVarV(getID(this.mRS), i, fieldPacker.getData(), this.mUseIncSupp);
    }

    public void setVar(int i, FieldPacker fieldPacker, Element element, int[] iArr) {
        if (this.mUseIncSupp) {
            this.mRS.nScriptSetVarVE(getID(this.mRS), i, fieldPacker.getData(), element.getDummyElement(this.mRS), iArr, this.mUseIncSupp);
        } else {
            this.mRS.nScriptSetVarVE(getID(this.mRS), i, fieldPacker.getData(), element.getID(this.mRS), iArr, this.mUseIncSupp);
        }
    }

    public static class Builder {
        RenderScript mRS;

        Builder(RenderScript renderScript) {
            this.mRS = renderScript;
        }
    }

    public static class FieldBase {
        protected Allocation mAllocation;
        protected Element mElement;

        protected void init(RenderScript renderScript, int i) {
            this.mAllocation = Allocation.createSized(renderScript, this.mElement, i, 1);
        }

        protected void init(RenderScript renderScript, int i, int i2) {
            this.mAllocation = Allocation.createSized(renderScript, this.mElement, i, i2 | 1);
        }

        protected FieldBase() {
        }

        public Element getElement() {
            return this.mElement;
        }

        public Type getType() {
            return this.mAllocation.getType();
        }

        public Allocation getAllocation() {
            return this.mAllocation;
        }

        public void updateAllocation() {
        }
    }

    public static final class LaunchOptions {
        private int strategy;
        private int xstart = 0;
        private int ystart = 0;
        private int xend = 0;
        private int yend = 0;
        private int zstart = 0;
        private int zend = 0;

        public LaunchOptions setX(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.xstart = i;
            this.xend = i2;
            return this;
        }

        public LaunchOptions setY(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.ystart = i;
            this.yend = i2;
            return this;
        }

        public LaunchOptions setZ(int i, int i2) {
            if (i < 0 || i2 <= i) {
                throw new RSIllegalArgumentException("Invalid dimensions");
            }
            this.zstart = i;
            this.zend = i2;
            return this;
        }

        public int getXStart() {
            return this.xstart;
        }

        public int getXEnd() {
            return this.xend;
        }

        public int getYStart() {
            return this.ystart;
        }

        public int getYEnd() {
            return this.yend;
        }

        public int getZStart() {
            return this.zstart;
        }

        public int getZEnd() {
            return this.zend;
        }
    }
}
