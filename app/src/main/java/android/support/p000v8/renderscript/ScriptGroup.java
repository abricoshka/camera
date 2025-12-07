package android.support.p000v8.renderscript;

import android.os.Build;
import android.support.p000v8.renderscript.Allocation;
import android.support.p000v8.renderscript.Script;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public final class ScriptGroup extends BaseObj {
    private static final int MIN_API_VERSION = 23;
    private static final String TAG = "ScriptGroup";
    private List<Closure> mClosures;
    C0062IO[] mInputs;
    private List<Input> mInputs2;
    private String mName;
    private ArrayList<Node> mNodes;
    C0062IO[] mOutputs;
    private Future[] mOutputs2;
    private boolean mUseIncSupp;

    /* renamed from: android.support.v8.renderscript.ScriptGroup$IO */
    static class C0062IO {
        Allocation mAllocation;
        Script.KernelID mKID;

        C0062IO(Script.KernelID kernelID) {
            this.mKID = kernelID;
        }
    }

    static class ConnectLine {
        Allocation mAllocation;
        Type mAllocationType;
        Script.KernelID mFrom;
        Script.FieldID mToF;
        Script.KernelID mToK;

        ConnectLine(Type type, Script.KernelID kernelID, Script.KernelID kernelID2) {
            this.mFrom = kernelID;
            this.mToK = kernelID2;
            this.mAllocationType = type;
        }

        ConnectLine(Type type, Script.KernelID kernelID, Script.FieldID fieldID) {
            this.mFrom = kernelID;
            this.mToF = fieldID;
            this.mAllocationType = type;
        }
    }

    static class Node {
        int dagNumber;
        Node mNext;
        int mOrder;
        Script mScript;
        boolean mSeen;
        ArrayList<Script.KernelID> mKernels = new ArrayList<>();
        ArrayList<ConnectLine> mInputs = new ArrayList<>();
        ArrayList<ConnectLine> mOutputs = new ArrayList<>();

        Node(Script script) {
            this.mScript = script;
        }
    }

    public static final class Closure extends BaseObj {
        private static final String TAG = "Closure";
        private Object[] mArgs;
        private Map<Script.FieldID, Object> mBindings;
        private FieldPacker mFP;
        private Map<Script.FieldID, Future> mGlobalFuture;
        private Future mReturnFuture;
        private Allocation mReturnValue;

        Closure(long j, RenderScript renderScript) {
            super(j, renderScript);
        }

        Closure(RenderScript renderScript, Script.KernelID kernelID, Type type, Object[] objArr, Map<Script.FieldID, Object> map) {
            super(0L, renderScript);
            if (Build.VERSION.SDK_INT < ScriptGroup.MIN_API_VERSION && renderScript.isUseNative()) {
                throw new RSRuntimeException("ScriptGroup2 not supported in this API level");
            }
            this.mArgs = objArr;
            this.mReturnValue = Allocation.createTyped(renderScript, type);
            this.mBindings = map;
            this.mGlobalFuture = new HashMap();
            int length = objArr.length + map.size();
            long[] jArr = new long[length];
            long[] jArr2 = new long[length];
            int[] iArr = new int[length];
            long[] jArr3 = new long[length];
            long[] jArr4 = new long[length];
            int i = 0;
            while (i < objArr.length) {
                jArr[i] = 0;
                retrieveValueAndDependenceInfo(renderScript, i, null, objArr[i], jArr2, iArr, jArr3, jArr4);
                i++;
            }
            Iterator<T> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object value = entry.getValue();
                Script.FieldID fieldID = (Script.FieldID) entry.getKey();
                jArr[i] = fieldID.getID(renderScript);
                retrieveValueAndDependenceInfo(renderScript, i, fieldID, value, jArr2, iArr, jArr3, jArr4);
                i++;
            }
            setID(renderScript.nClosureCreate(kernelID.getID(renderScript), this.mReturnValue.getID(renderScript), jArr, jArr2, iArr, jArr3, jArr4));
        }

        Closure(RenderScript renderScript, Script.InvokeID invokeID, Object[] objArr, Map<Script.FieldID, Object> map) {
            super(0L, renderScript);
            if (Build.VERSION.SDK_INT < ScriptGroup.MIN_API_VERSION && renderScript.isUseNative()) {
                throw new RSRuntimeException("ScriptGroup2 not supported in this API level");
            }
            this.mFP = FieldPacker.createFromArray(objArr);
            this.mArgs = objArr;
            this.mBindings = map;
            this.mGlobalFuture = new HashMap();
            int size = map.size();
            long[] jArr = new long[size];
            long[] jArr2 = new long[size];
            int[] iArr = new int[size];
            long[] jArr3 = new long[size];
            long[] jArr4 = new long[size];
            int i = 0;
            Iterator<T> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object value = entry.getValue();
                Script.FieldID fieldID = (Script.FieldID) entry.getKey();
                jArr[i] = fieldID.getID(renderScript);
                retrieveValueAndDependenceInfo(renderScript, i, fieldID, value, jArr2, iArr, jArr3, jArr4);
                i++;
            }
            setID(renderScript.nInvokeClosureCreate(invokeID.getID(renderScript), this.mFP.getData(), jArr, jArr2, iArr));
        }

        private void retrieveValueAndDependenceInfo(RenderScript renderScript, int i, Script.FieldID fieldID, Object obj, long[] jArr, int[] iArr, long[] jArr2, long[] jArr3) {
            Object obj2;
            if (obj instanceof Future) {
                Future future = (Future) obj;
                Object value = future.getValue();
                jArr2[i] = future.getClosure().getID(renderScript);
                Script.FieldID fieldID2 = future.getFieldID();
                jArr3[i] = fieldID2 != null ? fieldID2.getID(renderScript) : 0L;
                obj2 = value;
            } else {
                jArr2[i] = 0;
                jArr3[i] = 0;
                obj2 = obj;
            }
            if (obj2 instanceof Input) {
                Input input = (Input) obj2;
                if (i < this.mArgs.length) {
                    input.addReference(this, i);
                } else {
                    input.addReference(this, fieldID);
                }
                jArr[i] = 0;
                iArr[i] = 0;
                return;
            }
            ValueAndSize valueAndSize = new ValueAndSize(renderScript, obj2);
            jArr[i] = valueAndSize.value;
            iArr[i] = valueAndSize.size;
        }

        public Future getReturn() {
            if (this.mReturnFuture == null) {
                this.mReturnFuture = new Future(this, null, this.mReturnValue);
            }
            return this.mReturnFuture;
        }

        public Future getGlobal(Script.FieldID fieldID) {
            Future future = this.mGlobalFuture.get(fieldID);
            if (future == null) {
                Object value = this.mBindings.get(fieldID);
                if (value instanceof Future) {
                    value = ((Future) value).getValue();
                }
                Future future2 = new Future(this, fieldID, value);
                this.mGlobalFuture.put(fieldID, future2);
                return future2;
            }
            return future;
        }

        void setArg(int i, Object obj) {
            if (obj instanceof Future) {
                obj = ((Future) obj).getValue();
            }
            this.mArgs[i] = obj;
            ValueAndSize valueAndSize = new ValueAndSize(this.mRS, obj);
            this.mRS.nClosureSetArg(getID(this.mRS), i, valueAndSize.value, valueAndSize.size);
        }

        void setGlobal(Script.FieldID fieldID, Object obj) {
            if (obj instanceof Future) {
                obj = ((Future) obj).getValue();
            }
            this.mBindings.put(fieldID, obj);
            ValueAndSize valueAndSize = new ValueAndSize(this.mRS, obj);
            this.mRS.nClosureSetGlobal(getID(this.mRS), fieldID.getID(this.mRS), valueAndSize.value, valueAndSize.size);
        }

        private static final class ValueAndSize {
            public int size;
            public long value;

            public ValueAndSize(RenderScript renderScript, Object obj) {
                if (obj instanceof Allocation) {
                    this.value = ((Allocation) obj).getID(renderScript);
                    this.size = -1;
                    return;
                }
                if (obj instanceof Boolean) {
                    this.value = ((Boolean) obj).booleanValue() ? 1 : 0;
                    this.size = 4;
                    return;
                }
                if (obj instanceof Integer) {
                    this.value = ((Integer) obj).longValue();
                    this.size = 4;
                    return;
                }
                if (obj instanceof Long) {
                    this.value = ((Long) obj).longValue();
                    this.size = 8;
                } else if (obj instanceof Float) {
                    this.value = Float.floatToRawIntBits(((Float) obj).floatValue());
                    this.size = 4;
                } else if (obj instanceof Double) {
                    this.value = Double.doubleToRawLongBits(((Double) obj).doubleValue());
                    this.size = 8;
                }
            }
        }
    }

    public static final class Future {
        Closure mClosure;
        Script.FieldID mFieldID;
        Object mValue;

        Future(Closure closure, Script.FieldID fieldID, Object obj) {
            this.mClosure = closure;
            this.mFieldID = fieldID;
            this.mValue = obj;
        }

        Closure getClosure() {
            return this.mClosure;
        }

        Script.FieldID getFieldID() {
            return this.mFieldID;
        }

        Object getValue() {
            return this.mValue;
        }
    }

    public static final class Input {
        Object mValue;
        List<Pair<Closure, Script.FieldID>> mFieldID = new ArrayList();
        List<Pair<Closure, Integer>> mArgIndex = new ArrayList();

        Input() {
        }

        void addReference(Closure closure, int i) {
            this.mArgIndex.add(Pair.create(closure, Integer.valueOf(i)));
        }

        void addReference(Closure closure, Script.FieldID fieldID) {
            this.mFieldID.add(Pair.create(closure, fieldID));
        }

        void set(Object obj) {
            this.mValue = obj;
            Iterator<T> it = this.mArgIndex.iterator();
            while (it.hasNext()) {
                Pair pair = (Pair) it.next();
                ((Closure) pair.first).setArg(((Integer) pair.second).intValue(), obj);
            }
            Iterator<T> it2 = this.mFieldID.iterator();
            while (it2.hasNext()) {
                Pair pair2 = (Pair) it2.next();
                ((Closure) pair2.first).setGlobal((Script.FieldID) pair2.second, obj);
            }
        }

        Object get() {
            return this.mValue;
        }
    }

    ScriptGroup(long j, RenderScript renderScript) {
        super(j, renderScript);
        this.mUseIncSupp = false;
        this.mNodes = new ArrayList<>();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ScriptGroup(RenderScript renderScript, String str, List<Closure> list, List<Input> list2, Future[] futureArr) {
        super(0L, renderScript);
        int i = 0;
        this.mUseIncSupp = false;
        this.mNodes = new ArrayList<>();
        if (Build.VERSION.SDK_INT < MIN_API_VERSION && renderScript.isUseNative()) {
            throw new RSRuntimeException("ScriptGroup2 not supported in this API level");
        }
        this.mName = str;
        this.mClosures = list;
        this.mInputs2 = list2;
        this.mOutputs2 = futureArr;
        long[] jArr = new long[list.size()];
        while (true) {
            int i2 = i;
            if (i2 < jArr.length) {
                jArr[i2] = list.get(i2).getID(renderScript);
                i = i2 + 1;
            } else {
                setID(renderScript.nScriptGroup2Create(str, renderScript.getApplicationContext().getCacheDir().toString(), jArr));
                return;
            }
        }
    }

    public Object[] execute(Object... objArr) {
        int i = 0;
        if (objArr.length < this.mInputs2.size()) {
            Log.e(TAG, toString() + " receives " + objArr.length + " inputs, less than expected " + this.mInputs2.size());
            return null;
        }
        if (objArr.length > this.mInputs2.size()) {
            Log.i(TAG, toString() + " receives " + objArr.length + " inputs, more than expected " + this.mInputs2.size());
        }
        for (int i2 = 0; i2 < this.mInputs2.size(); i2++) {
            Object obj = objArr[i2];
            if ((obj instanceof Future) || (obj instanceof Input)) {
                Log.e(TAG, toString() + ": input " + i2 + " is a future or unbound value");
                return null;
            }
            this.mInputs2.get(i2).set(obj);
        }
        this.mRS.nScriptGroup2Execute(getID(this.mRS));
        Object[] objArr2 = new Object[this.mOutputs2.length];
        Future[] futureArr = this.mOutputs2;
        int length = futureArr.length;
        int i3 = 0;
        while (i < length) {
            Object value = futureArr[i].getValue();
            if (value instanceof Input) {
                value = ((Input) value).get();
            }
            objArr2[i3] = value;
            i++;
            i3++;
        }
        return objArr2;
    }

    @Deprecated
    public void setInput(Script.KernelID kernelID, Allocation allocation) {
        for (int i = 0; i < this.mInputs.length; i++) {
            if (this.mInputs[i].mKID == kernelID) {
                this.mInputs[i].mAllocation = allocation;
                if (!this.mUseIncSupp) {
                    this.mRS.nScriptGroupSetInput(getID(this.mRS), kernelID.getID(this.mRS), this.mRS.safeID(allocation));
                    return;
                }
                return;
            }
        }
        throw new RSIllegalArgumentException("Script not found");
    }

    @Deprecated
    public void setOutput(Script.KernelID kernelID, Allocation allocation) {
        for (int i = 0; i < this.mOutputs.length; i++) {
            if (this.mOutputs[i].mKID == kernelID) {
                this.mOutputs[i].mAllocation = allocation;
                if (!this.mUseIncSupp) {
                    this.mRS.nScriptGroupSetOutput(getID(this.mRS), kernelID.getID(this.mRS), this.mRS.safeID(allocation));
                    return;
                }
                return;
            }
        }
        throw new RSIllegalArgumentException("Script not found");
    }

    @Deprecated
    public void execute() {
        if (!this.mUseIncSupp) {
            this.mRS.nScriptGroupExecute(getID(this.mRS));
            return;
        }
        for (int i = 0; i < this.mNodes.size(); i++) {
            Node node = this.mNodes.get(i);
            for (int i2 = 0; i2 < node.mOutputs.size(); i2++) {
                ConnectLine connectLine = node.mOutputs.get(i2);
                if (connectLine.mAllocation == null) {
                    Allocation allocationCreateTyped = Allocation.createTyped(this.mRS, connectLine.mAllocationType, Allocation.MipmapControl.MIPMAP_NONE, 1);
                    connectLine.mAllocation = allocationCreateTyped;
                    int i3 = i2 + 1;
                    while (true) {
                        int i4 = i3;
                        if (i4 < node.mOutputs.size()) {
                            if (node.mOutputs.get(i4).mFrom == connectLine.mFrom) {
                                node.mOutputs.get(i4).mAllocation = allocationCreateTyped;
                            }
                            i3 = i4 + 1;
                        }
                    }
                }
            }
        }
        for (Node node2 : this.mNodes) {
            for (Script.KernelID kernelID : node2.mKernels) {
                Allocation allocation = null;
                for (ConnectLine connectLine2 : node2.mInputs) {
                    allocation = connectLine2.mToK == kernelID ? connectLine2.mAllocation : allocation;
                }
                C0062IO[] c0062ioArr = this.mInputs;
                int length = c0062ioArr.length;
                int i5 = 0;
                while (i5 < length) {
                    C0062IO c0062io = c0062ioArr[i5];
                    i5++;
                    allocation = c0062io.mKID == kernelID ? c0062io.mAllocation : allocation;
                }
                Allocation allocation2 = null;
                for (ConnectLine connectLine3 : node2.mOutputs) {
                    allocation2 = connectLine3.mFrom == kernelID ? connectLine3.mAllocation : allocation2;
                }
                Allocation allocation3 = allocation2;
                for (C0062IO c0062io2 : this.mOutputs) {
                    if (c0062io2.mKID == kernelID) {
                        allocation3 = c0062io2.mAllocation;
                    }
                }
                kernelID.mScript.forEach(kernelID.mSlot, allocation, allocation3, (FieldPacker) null);
            }
        }
    }

    @Deprecated
    public static final class Builder {
        private int mKernelCount;
        private RenderScript mRS;
        private ArrayList<Node> mNodes = new ArrayList<>();
        private ArrayList<ConnectLine> mLines = new ArrayList<>();
        private boolean mUseIncSupp = false;

        public Builder(RenderScript renderScript) {
            this.mRS = renderScript;
        }

        private void validateCycle(Node node, Node node2) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < node.mOutputs.size()) {
                    ConnectLine connectLine = node.mOutputs.get(i2);
                    if (connectLine.mToK != null) {
                        Node nodeFindNode = findNode(connectLine.mToK.mScript);
                        if (nodeFindNode.equals(node2)) {
                            throw new RSInvalidStateException("Loops in group not allowed.");
                        }
                        validateCycle(nodeFindNode, node2);
                    }
                    if (connectLine.mToF != null) {
                        Node nodeFindNode2 = findNode(connectLine.mToF.mScript);
                        if (nodeFindNode2.equals(node2)) {
                            throw new RSInvalidStateException("Loops in group not allowed.");
                        }
                        validateCycle(nodeFindNode2, node2);
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }

        private void mergeDAGs(int i, int i2) {
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 < this.mNodes.size()) {
                    if (this.mNodes.get(i4).dagNumber == i2) {
                        this.mNodes.get(i4).dagNumber = i;
                    }
                    i3 = i4 + 1;
                } else {
                    return;
                }
            }
        }

        private void validateDAGRecurse(Node node, int i) {
            int i2 = 0;
            if (node.dagNumber != 0 && node.dagNumber != i) {
                mergeDAGs(node.dagNumber, i);
                return;
            }
            node.dagNumber = i;
            while (true) {
                int i3 = i2;
                if (i3 < node.mOutputs.size()) {
                    ConnectLine connectLine = node.mOutputs.get(i3);
                    if (connectLine.mToK != null) {
                        validateDAGRecurse(findNode(connectLine.mToK.mScript), i);
                    }
                    if (connectLine.mToF != null) {
                        validateDAGRecurse(findNode(connectLine.mToF.mScript), i);
                    }
                    i2 = i3 + 1;
                } else {
                    return;
                }
            }
        }

        private void validateDAG() {
            for (int i = 0; i < this.mNodes.size(); i++) {
                Node node = this.mNodes.get(i);
                if (node.mInputs.size() == 0) {
                    if (node.mOutputs.size() == 0 && this.mNodes.size() > 1) {
                        throw new RSInvalidStateException("Groups cannot contain unconnected scripts");
                    }
                    validateDAGRecurse(node, i + 1);
                }
            }
            int i2 = this.mNodes.get(0).dagNumber;
            for (int i3 = 0; i3 < this.mNodes.size(); i3++) {
                if (this.mNodes.get(i3).dagNumber != i2) {
                    throw new RSInvalidStateException("Multiple DAGs in group not allowed.");
                }
            }
        }

        private Node findNode(Script script) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < this.mNodes.size()) {
                    if (script != this.mNodes.get(i2).mScript) {
                        i = i2 + 1;
                    } else {
                        return this.mNodes.get(i2);
                    }
                } else {
                    return null;
                }
            }
        }

        private Node findNode(Script.KernelID kernelID) {
            for (int i = 0; i < this.mNodes.size(); i++) {
                Node node = this.mNodes.get(i);
                for (int i2 = 0; i2 < node.mKernels.size(); i2++) {
                    if (kernelID == node.mKernels.get(i2)) {
                        return node;
                    }
                }
            }
            return null;
        }

        public Builder addKernel(Script.KernelID kernelID) {
            if (this.mLines.size() != 0) {
                throw new RSInvalidStateException("Kernels may not be added once connections exist.");
            }
            if (kernelID.mScript.isIncSupp()) {
                this.mUseIncSupp = true;
            }
            if (findNode(kernelID) != null) {
                return this;
            }
            this.mKernelCount++;
            Node nodeFindNode = findNode(kernelID.mScript);
            if (nodeFindNode == null) {
                nodeFindNode = new Node(kernelID.mScript);
                this.mNodes.add(nodeFindNode);
            }
            nodeFindNode.mKernels.add(kernelID);
            return this;
        }

        public Builder addConnection(Type type, Script.KernelID kernelID, Script.FieldID fieldID) {
            Node nodeFindNode = findNode(kernelID);
            if (nodeFindNode == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nodeFindNode2 = findNode(fieldID.mScript);
            if (nodeFindNode2 == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine connectLine = new ConnectLine(type, kernelID, fieldID);
            this.mLines.add(new ConnectLine(type, kernelID, fieldID));
            nodeFindNode.mOutputs.add(connectLine);
            nodeFindNode2.mInputs.add(connectLine);
            validateCycle(nodeFindNode, nodeFindNode);
            return this;
        }

        public Builder addConnection(Type type, Script.KernelID kernelID, Script.KernelID kernelID2) {
            Node nodeFindNode = findNode(kernelID);
            if (nodeFindNode == null) {
                throw new RSInvalidStateException("From script not found.");
            }
            Node nodeFindNode2 = findNode(kernelID2);
            if (nodeFindNode2 == null) {
                throw new RSInvalidStateException("To script not found.");
            }
            ConnectLine connectLine = new ConnectLine(type, kernelID, kernelID2);
            this.mLines.add(new ConnectLine(type, kernelID, kernelID2));
            nodeFindNode.mOutputs.add(connectLine);
            nodeFindNode2.mInputs.add(connectLine);
            validateCycle(nodeFindNode, nodeFindNode);
            return this;
        }

        private boolean calcOrderRecurse(Node node, int i) {
            Node nodeFindNode;
            boolean zCalcOrderRecurse = true;
            node.mSeen = true;
            if (node.mOrder < i) {
                node.mOrder = i;
            }
            Iterator<T> it = node.mOutputs.iterator();
            while (true) {
                boolean z = zCalcOrderRecurse;
                if (it.hasNext()) {
                    ConnectLine connectLine = (ConnectLine) it.next();
                    if (connectLine.mToF != null) {
                        nodeFindNode = findNode(connectLine.mToF.mScript);
                    } else {
                        nodeFindNode = findNode(connectLine.mToK.mScript);
                    }
                    if (nodeFindNode.mSeen) {
                        return false;
                    }
                    zCalcOrderRecurse = calcOrderRecurse(nodeFindNode, node.mOrder + 1) & z;
                } else {
                    return z;
                }
            }
        }

        private boolean calcOrder() {
            boolean zCalcOrderRecurse;
            boolean z = true;
            for (Node node : this.mNodes) {
                if (node.mInputs.size() == 0) {
                    Iterator<T> it = this.mNodes.iterator();
                    while (it.hasNext()) {
                        ((Node) it.next()).mSeen = false;
                    }
                    zCalcOrderRecurse = calcOrderRecurse(node, 1) & z;
                } else {
                    zCalcOrderRecurse = z;
                }
                z = zCalcOrderRecurse;
            }
            Collections.sort(this.mNodes, new Comparator<Node>() { // from class: android.support.v8.renderscript.ScriptGroup.Builder.1
                @Override // java.util.Comparator
                public int compare(Node node2, Node node3) {
                    return node2.mOrder - node3.mOrder;
                }
            });
            return z;
        }

        public ScriptGroup create() {
            long jNScriptGroupCreate;
            if (this.mNodes.size() == 0) {
                throw new RSInvalidStateException("Empty script groups are not allowed");
            }
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.mNodes.size()) {
                    break;
                }
                this.mNodes.get(i2).dagNumber = 0;
                i = i2 + 1;
            }
            validateDAG();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            long[] jArr = new long[this.mKernelCount];
            int i3 = 0;
            int i4 = 0;
            while (i4 < this.mNodes.size()) {
                Node node = this.mNodes.get(i4);
                int i5 = 0;
                int i6 = i3;
                while (i5 < node.mKernels.size()) {
                    Script.KernelID kernelID = node.mKernels.get(i5);
                    int i7 = i6 + 1;
                    jArr[i6] = kernelID.getID(this.mRS);
                    boolean z = false;
                    int i8 = 0;
                    boolean z2 = false;
                    while (true) {
                        int i9 = i8;
                        if (i9 >= node.mInputs.size()) {
                            break;
                        }
                        if (node.mInputs.get(i9).mToK == kernelID) {
                            z2 = true;
                        }
                        i8 = i9 + 1;
                    }
                    int i10 = 0;
                    while (true) {
                        int i11 = i10;
                        if (i11 >= node.mOutputs.size()) {
                            break;
                        }
                        if (node.mOutputs.get(i11).mFrom == kernelID) {
                            z = true;
                        }
                        i10 = i11 + 1;
                    }
                    if (!z2) {
                        arrayList.add(new C0062IO(kernelID));
                    }
                    if (!z) {
                        arrayList2.add(new C0062IO(kernelID));
                    }
                    i5++;
                    i6 = i7;
                }
                i4++;
                i3 = i6;
            }
            if (i3 != this.mKernelCount) {
                throw new RSRuntimeException("Count mismatch, should not happen.");
            }
            if (!this.mUseIncSupp) {
                long[] jArr2 = new long[this.mLines.size()];
                long[] jArr3 = new long[this.mLines.size()];
                long[] jArr4 = new long[this.mLines.size()];
                long[] jArr5 = new long[this.mLines.size()];
                int i12 = 0;
                while (true) {
                    int i13 = i12;
                    if (i13 >= this.mLines.size()) {
                        break;
                    }
                    ConnectLine connectLine = this.mLines.get(i13);
                    jArr2[i13] = connectLine.mFrom.getID(this.mRS);
                    if (connectLine.mToK != null) {
                        jArr3[i13] = connectLine.mToK.getID(this.mRS);
                    }
                    if (connectLine.mToF != null) {
                        jArr4[i13] = connectLine.mToF.getID(this.mRS);
                    }
                    jArr5[i13] = connectLine.mAllocationType.getID(this.mRS);
                    i12 = i13 + 1;
                }
                jNScriptGroupCreate = this.mRS.nScriptGroupCreate(jArr, jArr2, jArr3, jArr4, jArr5);
                if (jNScriptGroupCreate == 0) {
                    throw new RSRuntimeException("Object creation error, should not happen.");
                }
            } else {
                calcOrder();
                jNScriptGroupCreate = 0;
            }
            ScriptGroup scriptGroup = new ScriptGroup(jNScriptGroupCreate, this.mRS);
            scriptGroup.mOutputs = new C0062IO[arrayList2.size()];
            int i14 = 0;
            while (true) {
                int i15 = i14;
                if (i15 >= arrayList2.size()) {
                    break;
                }
                scriptGroup.mOutputs[i15] = (C0062IO) arrayList2.get(i15);
                i14 = i15 + 1;
            }
            scriptGroup.mInputs = new C0062IO[arrayList.size()];
            int i16 = 0;
            while (true) {
                int i17 = i16;
                if (i17 < arrayList.size()) {
                    scriptGroup.mInputs[i17] = (C0062IO) arrayList.get(i17);
                    i16 = i17 + 1;
                } else {
                    scriptGroup.mNodes = this.mNodes;
                    scriptGroup.mUseIncSupp = this.mUseIncSupp;
                    return scriptGroup;
                }
            }
        }
    }

    public static final class Binding {
        private final Script.FieldID mField;
        private final Object mValue;

        public Binding(Script.FieldID fieldID, Object obj) {
            this.mField = fieldID;
            this.mValue = obj;
        }

        public Script.FieldID getField() {
            return this.mField;
        }

        public Object getValue() {
            return this.mValue;
        }
    }

    public static final class Builder2 {
        private static final String TAG = "ScriptGroup.Builder2";
        List<Closure> mClosures = new ArrayList();
        List<Input> mInputs = new ArrayList();
        RenderScript mRS;

        public Builder2(RenderScript renderScript) {
            this.mRS = renderScript;
        }

        private Closure addKernelInternal(Script.KernelID kernelID, Type type, Object[] objArr, Map<Script.FieldID, Object> map) {
            Closure closure = new Closure(this.mRS, kernelID, type, objArr, map);
            this.mClosures.add(closure);
            return closure;
        }

        private Closure addInvokeInternal(Script.InvokeID invokeID, Object[] objArr, Map<Script.FieldID, Object> map) {
            Closure closure = new Closure(this.mRS, invokeID, objArr, map);
            this.mClosures.add(closure);
            return closure;
        }

        public Input addInput() {
            Input input = new Input();
            this.mInputs.add(input);
            return input;
        }

        public Closure addKernel(Script.KernelID kernelID, Type type, Object... objArr) {
            ArrayList<Object> arrayList = new ArrayList<>();
            HashMap map = new HashMap();
            if (!seperateArgsAndBindings(objArr, arrayList, map)) {
                return null;
            }
            return addKernelInternal(kernelID, type, arrayList.toArray(), map);
        }

        public Closure addInvoke(Script.InvokeID invokeID, Object... objArr) {
            ArrayList<Object> arrayList = new ArrayList<>();
            HashMap map = new HashMap();
            if (!seperateArgsAndBindings(objArr, arrayList, map)) {
                return null;
            }
            return addInvokeInternal(invokeID, arrayList.toArray(), map);
        }

        public ScriptGroup create(String str, Future... futureArr) {
            if (str == null || str.isEmpty() || str.length() > 100 || (!str.equals(str.replaceAll("[^a-zA-Z0-9-]", "_")))) {
                throw new RSIllegalArgumentException("invalid script group name");
            }
            return new ScriptGroup(this.mRS, str, this.mClosures, this.mInputs, futureArr);
        }

        private boolean seperateArgsAndBindings(Object[] objArr, ArrayList<Object> arrayList, Map<Script.FieldID, Object> map) {
            int i = 0;
            while (i < objArr.length && !(objArr[i] instanceof Binding)) {
                arrayList.add(objArr[i]);
                i++;
            }
            while (true) {
                int i2 = i;
                if (i2 < objArr.length) {
                    if (!(objArr[i2] instanceof Binding)) {
                        return false;
                    }
                    Binding binding = (Binding) objArr[i2];
                    map.put(binding.getField(), binding.getValue());
                    i = i2 + 1;
                } else {
                    return true;
                }
            }
        }
    }
}
