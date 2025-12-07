package android.arch.lifecycle;

import android.arch.lifecycle.Lifecycle;

/* loaded from: classes.dex */
public interface GenericLifecycleObserver {
    void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event);
}
