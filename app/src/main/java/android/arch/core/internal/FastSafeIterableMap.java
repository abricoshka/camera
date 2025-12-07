package android.arch.core.internal;

import android.arch.core.internal.SafeIterableMap;
import android.support.annotation.RestrictTo;
import java.util.HashMap;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class FastSafeIterableMap<K, V> extends SafeIterableMap<K, V> {
    private HashMap<K, SafeIterableMap.Entry<K, V>> mHashMap = new HashMap<>();

    public boolean contains(K k) {
        return this.mHashMap.containsKey(k);
    }
}
