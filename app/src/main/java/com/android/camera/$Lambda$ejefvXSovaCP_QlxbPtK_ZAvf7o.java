package com.android.camera;

/* renamed from: com.android.camera.-$Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o, reason: invalid class name */
/* loaded from: classes.dex */
final /* synthetic */ class $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o implements Runnable {
    private final /* synthetic */ byte $id;

    /* renamed from: -$f0, reason: not valid java name */
    private final /* synthetic */ Object f84$f0;

    private final /* synthetic */ void $m$0() {
        ((CameraActivity) this.f84$f0).m161lambda$com_android_camera_CameraActivity_21477();
    }

    private final /* synthetic */ void $m$1() {
        ((CameraActivity) this.f84$f0).m163lambda$com_android_camera_CameraActivity_21605();
    }

    private final /* synthetic */ void $m$2() {
        ((CameraActivity) this.f84$f0).m160lambda$com_android_camera_CameraActivity_21447();
    }

    private final /* synthetic */ void $m$3() {
        ((CameraActivity) this.f84$f0).m162lambda$com_android_camera_CameraActivity_21576();
    }

    public /* synthetic */ $Lambda$ejefvXSovaCP_QlxbPtK_ZAvf7o(byte b, Object obj) {
        this.$id = b;
        this.f84$f0 = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.$id) {
            case 0:
                $m$0();
                return;
            case 1:
                $m$1();
                return;
            case 2:
                $m$2();
                return;
            case 3:
                $m$3();
                return;
            default:
                throw new AssertionError();
        }
    }
}
