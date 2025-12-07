package android.support.v4.app;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManagerImpl;
import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/* loaded from: classes.dex */
final class BackStackRecord extends FragmentTransaction implements FragmentManager.BackStackEntry, FragmentManagerImpl.OpGenerator {
    static final int OP_ADD = 1;
    static final int OP_ATTACH = 7;
    static final int OP_DETACH = 6;
    static final int OP_HIDE = 4;
    static final int OP_NULL = 0;
    static final int OP_REMOVE = 3;
    static final int OP_REPLACE = 2;
    static final int OP_SET_PRIMARY_NAV = 8;
    static final int OP_SHOW = 5;
    static final int OP_UNSET_PRIMARY_NAV = 9;
    static final String TAG = "FragmentManager";
    boolean mAddToBackStack;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    ArrayList<Runnable> mCommitRunnables;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    final FragmentManagerImpl mManager;
    String mName;
    int mPopEnterAnim;
    int mPopExitAnim;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    int mTransition;
    int mTransitionStyle;
    ArrayList<C0005Op> mOps = new ArrayList<>();
    boolean mAllowAddToBackStack = true;
    int mIndex = -1;
    boolean mReorderingAllowed = false;

    /* renamed from: android.support.v4.app.BackStackRecord$Op */
    static final class C0005Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        int popEnterAnim;
        int popExitAnim;

        C0005Op() {
        }

        C0005Op(int i, Fragment fragment) {
            this.cmd = i;
            this.fragment = fragment;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mName != null) {
            sb.append(" ");
            sb.append(this.mName);
        }
        sb.append("}");
        return sb.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        dump(str, printWriter, true);
    }

    public void dump(String str, PrintWriter printWriter, boolean z) {
        String str2;
        if (z) {
            printWriter.print(str);
            printWriter.print("mName=");
            printWriter.print(this.mName);
            printWriter.print(" mIndex=");
            printWriter.print(this.mIndex);
            printWriter.print(" mCommitted=");
            printWriter.println(this.mCommitted);
            if (this.mTransition != 0) {
                printWriter.print(str);
                printWriter.print("mTransition=#");
                printWriter.print(Integer.toHexString(this.mTransition));
                printWriter.print(" mTransitionStyle=#");
                printWriter.println(Integer.toHexString(this.mTransitionStyle));
            }
            if (this.mEnterAnim != 0 || this.mExitAnim != 0) {
                printWriter.print(str);
                printWriter.print("mEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mEnterAnim));
                printWriter.print(" mExitAnim=#");
                printWriter.println(Integer.toHexString(this.mExitAnim));
            }
            if (this.mPopEnterAnim != 0 || this.mPopExitAnim != 0) {
                printWriter.print(str);
                printWriter.print("mPopEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mPopEnterAnim));
                printWriter.print(" mPopExitAnim=#");
                printWriter.println(Integer.toHexString(this.mPopExitAnim));
            }
            if (this.mBreadCrumbTitleRes != 0 || this.mBreadCrumbTitleText != null) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
                printWriter.print(" mBreadCrumbTitleText=");
                printWriter.println(this.mBreadCrumbTitleText);
            }
            if (this.mBreadCrumbShortTitleRes != 0 || this.mBreadCrumbShortTitleText != null) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbShortTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
                printWriter.print(" mBreadCrumbShortTitleText=");
                printWriter.println(this.mBreadCrumbShortTitleText);
            }
        }
        if (!this.mOps.isEmpty()) {
            printWriter.print(str);
            printWriter.println("Operations:");
            String str3 = str + "    ";
            int size = this.mOps.size();
            for (int i = 0; i < size; i++) {
                C0005Op c0005Op = this.mOps.get(i);
                switch (c0005Op.cmd) {
                    case 0:
                        str2 = "NULL";
                        break;
                    case 1:
                        str2 = "ADD";
                        break;
                    case 2:
                        str2 = "REPLACE";
                        break;
                    case 3:
                        str2 = "REMOVE";
                        break;
                    case 4:
                        str2 = "HIDE";
                        break;
                    case 5:
                        str2 = "SHOW";
                        break;
                    case 6:
                        str2 = "DETACH";
                        break;
                    case 7:
                        str2 = "ATTACH";
                        break;
                    case 8:
                        str2 = "SET_PRIMARY_NAV";
                        break;
                    case OP_UNSET_PRIMARY_NAV /* 9 */:
                        str2 = "UNSET_PRIMARY_NAV";
                        break;
                    default:
                        str2 = "cmd=" + c0005Op.cmd;
                        break;
                }
                printWriter.print(str);
                printWriter.print("  Op #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.print(str2);
                printWriter.print(" ");
                printWriter.println(c0005Op.fragment);
                if (z) {
                    if (c0005Op.enterAnim != 0 || c0005Op.exitAnim != 0) {
                        printWriter.print(str);
                        printWriter.print("enterAnim=#");
                        printWriter.print(Integer.toHexString(c0005Op.enterAnim));
                        printWriter.print(" exitAnim=#");
                        printWriter.println(Integer.toHexString(c0005Op.exitAnim));
                    }
                    if (c0005Op.popEnterAnim != 0 || c0005Op.popExitAnim != 0) {
                        printWriter.print(str);
                        printWriter.print("popEnterAnim=#");
                        printWriter.print(Integer.toHexString(c0005Op.popEnterAnim));
                        printWriter.print(" popExitAnim=#");
                        printWriter.println(Integer.toHexString(c0005Op.popExitAnim));
                    }
                }
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl fragmentManagerImpl) {
        this.mManager = fragmentManagerImpl;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public int getId() {
        return this.mIndex;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public int getBreadCrumbTitleRes() {
        return this.mBreadCrumbTitleRes;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public int getBreadCrumbShortTitleRes() {
        return this.mBreadCrumbShortTitleRes;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public CharSequence getBreadCrumbTitle() {
        if (this.mBreadCrumbTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes);
        }
        return this.mBreadCrumbTitleText;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public CharSequence getBreadCrumbShortTitle() {
        if (this.mBreadCrumbShortTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes);
        }
        return this.mBreadCrumbShortTitleText;
    }

    void addOp(C0005Op c0005Op) {
        this.mOps.add(c0005Op);
        c0005Op.enterAnim = this.mEnterAnim;
        c0005Op.exitAnim = this.mExitAnim;
        c0005Op.popEnterAnim = this.mPopEnterAnim;
        c0005Op.popExitAnim = this.mPopExitAnim;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction add(Fragment fragment, String str) {
        doAddOp(0, fragment, str, 1);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction add(int i, Fragment fragment) {
        doAddOp(i, fragment, null, 1);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction add(int i, Fragment fragment, String str) {
        doAddOp(i, fragment, str, 1);
        return this;
    }

    private void doAddOp(int i, Fragment fragment, String str, int i2) {
        Class<?> cls = fragment.getClass();
        int modifiers = cls.getModifiers();
        if (cls.isAnonymousClass() || (!Modifier.isPublic(modifiers)) || (cls.isMemberClass() && (!Modifier.isStatic(modifiers)))) {
            throw new IllegalStateException("Fragment " + cls.getCanonicalName() + " must be a public static class to be  properly recreated from instance state.");
        }
        fragment.mFragmentManager = this.mManager;
        if (str != null) {
            if (fragment.mTag != null && (!str.equals(fragment.mTag))) {
                throw new IllegalStateException("Can't change tag of fragment " + fragment + ": was " + fragment.mTag + " now " + str);
            }
            fragment.mTag = str;
        }
        if (i != 0) {
            if (i == -1) {
                throw new IllegalArgumentException("Can't add fragment " + fragment + " with tag " + str + " to container view with no id");
            }
            if (fragment.mFragmentId != 0 && fragment.mFragmentId != i) {
                throw new IllegalStateException("Can't change container ID of fragment " + fragment + ": was " + fragment.mFragmentId + " now " + i);
            }
            fragment.mFragmentId = i;
            fragment.mContainerId = i;
        }
        addOp(new C0005Op(i2, fragment));
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction replace(int i, Fragment fragment) {
        return replace(i, fragment, null);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction replace(int i, Fragment fragment, String str) {
        if (i == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }
        doAddOp(i, fragment, str, 2);
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction remove(Fragment fragment) {
        addOp(new C0005Op(3, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction hide(Fragment fragment) {
        addOp(new C0005Op(4, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction show(Fragment fragment) {
        addOp(new C0005Op(5, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction detach(Fragment fragment) {
        addOp(new C0005Op(6, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction attach(Fragment fragment) {
        addOp(new C0005Op(7, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) {
        addOp(new C0005Op(8, fragment));
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setCustomAnimations(int i, int i2) {
        return setCustomAnimations(i, i2, 0, 0);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setCustomAnimations(int i, int i2, int i3, int i4) {
        this.mEnterAnim = i;
        this.mExitAnim = i2;
        this.mPopEnterAnim = i3;
        this.mPopExitAnim = i4;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setTransition(int i) {
        this.mTransition = i;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction addSharedElement(View view, String str) {
        if (FragmentTransition.supportsTransition()) {
            String transitionName = ViewCompat.getTransitionName(view);
            if (transitionName == null) {
                throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
            }
            if (this.mSharedElementSourceNames == null) {
                this.mSharedElementSourceNames = new ArrayList<>();
                this.mSharedElementTargetNames = new ArrayList<>();
            } else {
                if (this.mSharedElementTargetNames.contains(str)) {
                    throw new IllegalArgumentException("A shared element with the target name '" + str + "' has already been added to the transaction.");
                }
                if (this.mSharedElementSourceNames.contains(transitionName)) {
                    throw new IllegalArgumentException("A shared element with the source name '" + transitionName + " has already been added to the transaction.");
                }
            }
            this.mSharedElementSourceNames.add(transitionName);
            this.mSharedElementTargetNames.add(str);
        }
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setTransitionStyle(int i) {
        this.mTransitionStyle = i;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction addToBackStack(String str) {
        if (!this.mAllowAddToBackStack) {
            throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
        }
        this.mAddToBackStack = true;
        this.mName = str;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public boolean isAddToBackStackAllowed() {
        return this.mAllowAddToBackStack;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction disallowAddToBackStack() {
        if (this.mAddToBackStack) {
            throw new IllegalStateException("This transaction is already being added to the back stack");
        }
        this.mAllowAddToBackStack = false;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setBreadCrumbTitle(int i) {
        this.mBreadCrumbTitleRes = i;
        this.mBreadCrumbTitleText = null;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setBreadCrumbTitle(CharSequence charSequence) {
        this.mBreadCrumbTitleRes = 0;
        this.mBreadCrumbTitleText = charSequence;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setBreadCrumbShortTitle(int i) {
        this.mBreadCrumbShortTitleRes = i;
        this.mBreadCrumbShortTitleText = null;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setBreadCrumbShortTitle(CharSequence charSequence) {
        this.mBreadCrumbShortTitleRes = 0;
        this.mBreadCrumbShortTitleText = charSequence;
        return this;
    }

    void bumpBackStackNesting(int i) {
        if (this.mAddToBackStack) {
            if (FragmentManagerImpl.DEBUG) {
                Log.v(TAG, "Bump nesting in " + this + " by " + i);
            }
            int size = this.mOps.size();
            for (int i2 = 0; i2 < size; i2++) {
                C0005Op c0005Op = this.mOps.get(i2);
                if (c0005Op.fragment != null) {
                    c0005Op.fragment.mBackStackNesting += i;
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v(TAG, "Bump nesting of " + c0005Op.fragment + " to " + c0005Op.fragment.mBackStackNesting);
                    }
                }
            }
        }
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction runOnCommit(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable cannot be null");
        }
        disallowAddToBackStack();
        if (this.mCommitRunnables == null) {
            this.mCommitRunnables = new ArrayList<>();
        }
        this.mCommitRunnables.add(runnable);
        return this;
    }

    public void runOnCommitRunnables() {
        if (this.mCommitRunnables != null) {
            int size = this.mCommitRunnables.size();
            for (int i = 0; i < size; i++) {
                this.mCommitRunnables.get(i).run();
            }
            this.mCommitRunnables = null;
        }
    }

    @Override // android.support.v4.app.FragmentTransaction
    public int commit() {
        return commitInternal(false);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public void commitNow() throws Resources.NotFoundException {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, false);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public void commitNowAllowingStateLoss() throws Resources.NotFoundException {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, true);
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setReorderingAllowed(boolean z) {
        this.mReorderingAllowed = z;
        return this;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public FragmentTransaction setAllowOptimization(boolean z) {
        return setReorderingAllowed(z);
    }

    int commitInternal(boolean z) {
        if (this.mCommitted) {
            throw new IllegalStateException("commit already called");
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "Commit: " + this);
            PrintWriter printWriter = new PrintWriter(new LogWriter(TAG));
            dump("  ", null, printWriter, null);
            printWriter.close();
        }
        this.mCommitted = true;
        if (this.mAddToBackStack) {
            this.mIndex = this.mManager.allocBackStackIndex(this);
        } else {
            this.mIndex = -1;
        }
        this.mManager.enqueueAction(this, z);
        return this.mIndex;
    }

    @Override // android.support.v4.app.FragmentManagerImpl.OpGenerator
    public boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "Run: " + this);
        }
        arrayList.add(this);
        arrayList2.add(false);
        if (this.mAddToBackStack) {
            this.mManager.addBackStackState(this);
            return true;
        }
        return true;
    }

    boolean interactsWith(int i) {
        int size = this.mOps.size();
        for (int i2 = 0; i2 < size; i2++) {
            C0005Op c0005Op = this.mOps.get(i2);
            int i3 = c0005Op.fragment != null ? c0005Op.fragment.mContainerId : 0;
            if (i3 != 0 && i3 == i) {
                return true;
            }
        }
        return false;
    }

    boolean interactsWith(ArrayList<BackStackRecord> arrayList, int i, int i2) {
        int i3;
        if (i2 == i) {
            return false;
        }
        int size = this.mOps.size();
        int i4 = -1;
        int i5 = 0;
        while (i5 < size) {
            C0005Op c0005Op = this.mOps.get(i5);
            int i6 = c0005Op.fragment != null ? c0005Op.fragment.mContainerId : 0;
            if (i6 == 0 || i6 == i4) {
                i3 = i4;
            } else {
                for (int i7 = i; i7 < i2; i7++) {
                    BackStackRecord backStackRecord = arrayList.get(i7);
                    int size2 = backStackRecord.mOps.size();
                    for (int i8 = 0; i8 < size2; i8++) {
                        C0005Op c0005Op2 = backStackRecord.mOps.get(i8);
                        if ((c0005Op2.fragment != null ? c0005Op2.fragment.mContainerId : 0) == i6) {
                            return true;
                        }
                    }
                }
                i3 = i6;
            }
            i5++;
            i4 = i3;
        }
        return false;
    }

    void executeOps() throws Resources.NotFoundException {
        int size = this.mOps.size();
        for (int i = 0; i < size; i++) {
            C0005Op c0005Op = this.mOps.get(i);
            Fragment fragment = c0005Op.fragment;
            if (fragment != null) {
                fragment.setNextTransition(this.mTransition, this.mTransitionStyle);
            }
            switch (c0005Op.cmd) {
                case 1:
                    fragment.setNextAnim(c0005Op.enterAnim);
                    this.mManager.addFragment(fragment, false);
                    break;
                case 2:
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + c0005Op.cmd);
                case 3:
                    fragment.setNextAnim(c0005Op.exitAnim);
                    this.mManager.removeFragment(fragment);
                    break;
                case 4:
                    fragment.setNextAnim(c0005Op.exitAnim);
                    this.mManager.hideFragment(fragment);
                    break;
                case 5:
                    fragment.setNextAnim(c0005Op.enterAnim);
                    this.mManager.showFragment(fragment);
                    break;
                case 6:
                    fragment.setNextAnim(c0005Op.exitAnim);
                    this.mManager.detachFragment(fragment);
                    break;
                case 7:
                    fragment.setNextAnim(c0005Op.enterAnim);
                    this.mManager.attachFragment(fragment);
                    break;
                case 8:
                    this.mManager.setPrimaryNavigationFragment(fragment);
                    break;
                case OP_UNSET_PRIMARY_NAV /* 9 */:
                    this.mManager.setPrimaryNavigationFragment(null);
                    break;
            }
            if (!this.mReorderingAllowed && c0005Op.cmd != 1 && fragment != null) {
                this.mManager.moveFragmentToExpectedState(fragment);
            }
        }
        if (!this.mReorderingAllowed) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    void executePopOps(boolean z) throws Resources.NotFoundException {
        for (int size = this.mOps.size() - 1; size >= 0; size--) {
            C0005Op c0005Op = this.mOps.get(size);
            Fragment fragment = c0005Op.fragment;
            if (fragment != null) {
                fragment.setNextTransition(FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
            }
            switch (c0005Op.cmd) {
                case 1:
                    fragment.setNextAnim(c0005Op.popExitAnim);
                    this.mManager.removeFragment(fragment);
                    break;
                case 2:
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + c0005Op.cmd);
                case 3:
                    fragment.setNextAnim(c0005Op.popEnterAnim);
                    this.mManager.addFragment(fragment, false);
                    break;
                case 4:
                    fragment.setNextAnim(c0005Op.popEnterAnim);
                    this.mManager.showFragment(fragment);
                    break;
                case 5:
                    fragment.setNextAnim(c0005Op.popExitAnim);
                    this.mManager.hideFragment(fragment);
                    break;
                case 6:
                    fragment.setNextAnim(c0005Op.popEnterAnim);
                    this.mManager.attachFragment(fragment);
                    break;
                case 7:
                    fragment.setNextAnim(c0005Op.popExitAnim);
                    this.mManager.detachFragment(fragment);
                    break;
                case 8:
                    this.mManager.setPrimaryNavigationFragment(null);
                    break;
                case OP_UNSET_PRIMARY_NAV /* 9 */:
                    this.mManager.setPrimaryNavigationFragment(fragment);
                    break;
            }
            if (!this.mReorderingAllowed && c0005Op.cmd != 3 && fragment != null) {
                this.mManager.moveFragmentToExpectedState(fragment);
            }
        }
        if (!this.mReorderingAllowed && z) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    Fragment expandOps(ArrayList<Fragment> arrayList, Fragment fragment) {
        boolean z;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mOps.size()) {
                C0005Op c0005Op = this.mOps.get(i2);
                switch (c0005Op.cmd) {
                    case 1:
                    case 7:
                        arrayList.add(c0005Op.fragment);
                        break;
                    case 2:
                        Fragment fragment2 = c0005Op.fragment;
                        int i3 = fragment2.mContainerId;
                        boolean z2 = false;
                        int size = arrayList.size() - 1;
                        Fragment fragment3 = fragment;
                        int i4 = i2;
                        while (size >= 0) {
                            Fragment fragment4 = arrayList.get(size);
                            if (fragment4.mContainerId != i3) {
                                z = z2;
                            } else if (fragment4 == fragment2) {
                                z = true;
                            } else {
                                if (fragment4 == fragment3) {
                                    this.mOps.add(i4, new C0005Op(OP_UNSET_PRIMARY_NAV, fragment4));
                                    i4++;
                                    fragment3 = null;
                                }
                                C0005Op c0005Op2 = new C0005Op(3, fragment4);
                                c0005Op2.enterAnim = c0005Op.enterAnim;
                                c0005Op2.popEnterAnim = c0005Op.popEnterAnim;
                                c0005Op2.exitAnim = c0005Op.exitAnim;
                                c0005Op2.popExitAnim = c0005Op.popExitAnim;
                                this.mOps.add(i4, c0005Op2);
                                arrayList.remove(fragment4);
                                i4++;
                                z = z2;
                            }
                            size--;
                            z2 = z;
                        }
                        if (z2) {
                            this.mOps.remove(i4);
                            i4--;
                        } else {
                            c0005Op.cmd = 1;
                            arrayList.add(fragment2);
                        }
                        i2 = i4;
                        fragment = fragment3;
                        break;
                    case 3:
                    case 6:
                        arrayList.remove(c0005Op.fragment);
                        if (c0005Op.fragment != fragment) {
                            break;
                        } else {
                            this.mOps.add(i2, new C0005Op(OP_UNSET_PRIMARY_NAV, c0005Op.fragment));
                            i2++;
                            fragment = null;
                            break;
                        }
                    case 8:
                        this.mOps.add(i2, new C0005Op(OP_UNSET_PRIMARY_NAV, fragment));
                        i2++;
                        fragment = c0005Op.fragment;
                        break;
                }
                i = i2 + 1;
            } else {
                return fragment;
            }
        }
    }

    Fragment trackAddedFragmentsInPop(ArrayList<Fragment> arrayList, Fragment fragment) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mOps.size()) {
                C0005Op c0005Op = this.mOps.get(i2);
                switch (c0005Op.cmd) {
                    case 1:
                    case 7:
                        arrayList.remove(c0005Op.fragment);
                        break;
                    case 3:
                    case 6:
                        arrayList.add(c0005Op.fragment);
                        break;
                    case 8:
                        fragment = null;
                        break;
                    case OP_UNSET_PRIMARY_NAV /* 9 */:
                        fragment = c0005Op.fragment;
                        break;
                }
                i = i2 + 1;
            } else {
                return fragment;
            }
        }
    }

    boolean isPostponed() {
        for (int i = 0; i < this.mOps.size(); i++) {
            if (isFragmentPostponed(this.mOps.get(i))) {
                return true;
            }
        }
        return false;
    }

    void setOnStartPostponedListener(Fragment.OnStartEnterTransitionListener onStartEnterTransitionListener) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mOps.size()) {
                C0005Op c0005Op = this.mOps.get(i2);
                if (isFragmentPostponed(c0005Op)) {
                    c0005Op.fragment.setOnStartEnterTransitionListener(onStartEnterTransitionListener);
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private static boolean isFragmentPostponed(C0005Op c0005Op) {
        Fragment fragment = c0005Op.fragment;
        if (fragment != null && fragment.mAdded && fragment.mView != null && (!fragment.mDetached) && (!fragment.mHidden)) {
            return fragment.isPostponed();
        }
        return false;
    }

    @Override // android.support.v4.app.FragmentManager.BackStackEntry
    public String getName() {
        return this.mName;
    }

    public int getTransition() {
        return this.mTransition;
    }

    public int getTransitionStyle() {
        return this.mTransitionStyle;
    }

    @Override // android.support.v4.app.FragmentTransaction
    public boolean isEmpty() {
        return this.mOps.isEmpty();
    }
}
