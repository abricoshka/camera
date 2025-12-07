package android.arch.core.internal;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class SafeIterableMap<K, V> implements Iterable<Map.Entry<K, V>> {
    private Entry<K, V> mEnd;
    private WeakHashMap<SupportRemove<K, V>, Boolean> mIterators = new WeakHashMap<>();
    private int mSize = 0;
    private Entry<K, V> mStart;

    public int size() {
        return this.mSize;
    }

    @Override // java.lang.Iterable
    @NonNull
    public Iterator<Map.Entry<K, V>> iterator() {
        SupportRemove<K, V> ascendingIterator = new AscendingIterator<>(this.mStart, this.mEnd);
        this.mIterators.put(ascendingIterator, false);
        return ascendingIterator;
    }

    public Iterator<Map.Entry<K, V>> descendingIterator() {
        SupportRemove<K, V> descendingIterator = new DescendingIterator<>(this.mEnd, this.mStart);
        this.mIterators.put(descendingIterator, false);
        return descendingIterator;
    }

    public SafeIterableMap<K, V>.IteratorWithAdditions iteratorWithAdditions() {
        SupportRemove<K, V> iteratorWithAdditions = new IteratorWithAdditions();
        this.mIterators.put(iteratorWithAdditions, false);
        return iteratorWithAdditions;
    }

    public Map.Entry<K, V> eldest() {
        return this.mStart;
    }

    public Map.Entry<K, V> newest() {
        return this.mEnd;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SafeIterableMap)) {
            return false;
        }
        SafeIterableMap safeIterableMap = (SafeIterableMap) obj;
        if (size() != safeIterableMap.size()) {
            return false;
        }
        Iterator<Map.Entry<K, V>> it = iterator();
        Iterator<Map.Entry<K, V>> it2 = safeIterableMap.iterator();
        while (it.hasNext() && it2.hasNext()) {
            Map.Entry<K, V> next = it.next();
            Map.Entry<K, V> next2 = it2.next();
            if ((next == null && next2 != null) || (next != null && !next.equals(next2))) {
                return false;
            }
        }
        return (it.hasNext() || it2.hasNext()) ? false : true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Map.Entry<K, V>> it = iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /* JADX WARN: Unexpected interfaces in signature: [android.arch.core.internal.SafeIterableMap$SupportRemove<K, V>] */
    private static abstract class ListIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        Entry<K, V> mExpectedEnd;
        Entry<K, V> mNext;

        abstract Entry<K, V> forward(Entry<K, V> entry);

        ListIterator(Entry<K, V> entry, Entry<K, V> entry2) {
            this.mExpectedEnd = entry2;
            this.mNext = entry;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mNext != null;
        }

        private Entry<K, V> nextNode() {
            if (this.mNext == this.mExpectedEnd || this.mExpectedEnd == null) {
                return null;
            }
            return forward(this.mNext);
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            Entry<K, V> entry = this.mNext;
            this.mNext = nextNode();
            return entry;
        }
    }

    static class AscendingIterator<K, V> extends ListIterator<K, V> {
        AscendingIterator(Entry<K, V> entry, Entry<K, V> entry2) {
            super(entry, entry2);
        }

        @Override // android.arch.core.internal.SafeIterableMap.ListIterator
        Entry<K, V> forward(Entry<K, V> entry) {
            return entry.mNext;
        }
    }

    private static class DescendingIterator<K, V> extends ListIterator<K, V> {
        DescendingIterator(Entry<K, V> entry, Entry<K, V> entry2) {
            super(entry, entry2);
        }

        @Override // android.arch.core.internal.SafeIterableMap.ListIterator
        Entry<K, V> forward(Entry<K, V> entry) {
            return entry.mPrevious;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unexpected interfaces in signature: [android.arch.core.internal.SafeIterableMap$SupportRemove<K, V>] */
    public class IteratorWithAdditions implements Iterator<Map.Entry<K, V>> {
        private boolean mBeforeStart;
        private Entry<K, V> mCurrent;

        private IteratorWithAdditions() {
            this.mBeforeStart = true;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.mBeforeStart ? (this.mCurrent == null || this.mCurrent.mNext == null) ? false : true : SafeIterableMap.this.mStart != null;
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            if (this.mBeforeStart) {
                this.mBeforeStart = false;
                this.mCurrent = SafeIterableMap.this.mStart;
            } else {
                this.mCurrent = this.mCurrent != null ? this.mCurrent.mNext : null;
            }
            return this.mCurrent;
        }
    }

    static class Entry<K, V> implements Map.Entry<K, V> {

        @NonNull
        final K mKey;
        Entry<K, V> mNext;
        Entry<K, V> mPrevious;

        @NonNull
        final V mValue;

        @Override // java.util.Map.Entry
        @NonNull
        public K getKey() {
            return this.mKey;
        }

        @Override // java.util.Map.Entry
        @NonNull
        public V getValue() {
            return this.mValue;
        }

        @Override // java.util.Map.Entry
        public V setValue(V v) {
            throw new UnsupportedOperationException("An entry modification is not supported");
        }

        public String toString() {
            return this.mKey + "=" + this.mValue;
        }

        @Override // java.util.Map.Entry
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return this.mKey.equals(entry.mKey) && this.mValue.equals(entry.mValue);
        }
    }
}
