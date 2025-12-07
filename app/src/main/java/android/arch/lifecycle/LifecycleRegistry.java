package android.arch.lifecycle;

import android.arch.core.internal.FastSafeIterableMap;
import android.arch.core.internal.SafeIterableMap;
import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FrameMetricsAggregator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class LifecycleRegistry extends Lifecycle {
    private final LifecycleOwner mLifecycleOwner;
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap = new FastSafeIterableMap<>();
    private int mAddingObserverCounter = 0;
    private boolean mHandlingEvent = false;
    private boolean mNewEventOccurred = false;
    private ArrayList<Lifecycle.State> mParentStates = new ArrayList<>();
    private Lifecycle.State mState = Lifecycle.State.INITIALIZED;

    public LifecycleRegistry(@NonNull LifecycleOwner lifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner;
    }

    public void markState(Lifecycle.State state) {
        this.mState = state;
    }

    public void handleLifecycleEvent(Lifecycle.Event event) {
        this.mState = getStateAfter(event);
        if (this.mHandlingEvent || this.mAddingObserverCounter != 0) {
            this.mNewEventOccurred = true;
            return;
        }
        this.mHandlingEvent = true;
        sync();
        this.mHandlingEvent = false;
    }

    private boolean isSynced() {
        if (this.mObserverMap.size() == 0) {
            return true;
        }
        Lifecycle.State state = this.mObserverMap.eldest().getValue().mState;
        Lifecycle.State state2 = this.mObserverMap.newest().getValue().mState;
        return state == state2 && this.mState == state2;
    }

    private void popParentState() {
        this.mParentStates.remove(this.mParentStates.size() - 1);
    }

    private void pushParentState(Lifecycle.State state) {
        this.mParentStates.add(state);
    }

    static Lifecycle.State getStateAfter(Lifecycle.Event event) {
        switch (C00011.$SwitchMap$android$arch$lifecycle$Lifecycle$Event[event.ordinal()]) {
            case 1:
            case 2:
                return Lifecycle.State.CREATED;
            case 3:
            case 4:
                return Lifecycle.State.STARTED;
            case 5:
                return Lifecycle.State.RESUMED;
            case 6:
                return Lifecycle.State.DESTROYED;
            case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
            default:
                throw new IllegalArgumentException("Unexpected event value " + event);
        }
    }

    /* renamed from: android.arch.lifecycle.LifecycleRegistry$1 */
    static /* synthetic */ class C00011 {
        static final /* synthetic */ int[] $SwitchMap$android$arch$lifecycle$Lifecycle$Event;

        static {
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[Lifecycle.State.INITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[Lifecycle.State.CREATED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[Lifecycle.State.STARTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[Lifecycle.State.RESUMED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$State[Lifecycle.State.DESTROYED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            $SwitchMap$android$arch$lifecycle$Lifecycle$Event = new int[Lifecycle.Event.values().length];
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_STOP.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_START.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_PAUSE.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_RESUME.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_DESTROY.ordinal()] = 6;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$arch$lifecycle$Lifecycle$Event[Lifecycle.Event.ON_ANY.ordinal()] = 7;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    private static Lifecycle.Event downEvent(Lifecycle.State state) {
        switch (state) {
            case INITIALIZED:
                throw new IllegalArgumentException();
            case CREATED:
                return Lifecycle.Event.ON_DESTROY;
            case STARTED:
                return Lifecycle.Event.ON_STOP;
            case RESUMED:
                return Lifecycle.Event.ON_PAUSE;
            case DESTROYED:
                throw new IllegalArgumentException();
            default:
                throw new IllegalArgumentException("Unexpected state value " + state);
        }
    }

    private static Lifecycle.Event upEvent(Lifecycle.State state) {
        switch (state) {
            case INITIALIZED:
            case DESTROYED:
                return Lifecycle.Event.ON_CREATE;
            case CREATED:
                return Lifecycle.Event.ON_START;
            case STARTED:
                return Lifecycle.Event.ON_RESUME;
            case RESUMED:
                throw new IllegalArgumentException();
            default:
                throw new IllegalArgumentException("Unexpected state value " + state);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void forwardPass() {
        SafeIterableMap<LifecycleObserver, ObserverWithState>.IteratorWithAdditions iteratorWithAdditions = this.mObserverMap.iteratorWithAdditions();
        while (iteratorWithAdditions.hasNext() && !this.mNewEventOccurred) {
            Map.Entry next = iteratorWithAdditions.next();
            ObserverWithState observerWithState = (ObserverWithState) next.getValue();
            while (observerWithState.mState.compareTo(this.mState) < 0 && !this.mNewEventOccurred && this.mObserverMap.contains(next.getKey())) {
                pushParentState(observerWithState.mState);
                observerWithState.dispatchEvent(this.mLifecycleOwner, upEvent(observerWithState.mState));
                popParentState();
            }
        }
    }

    private void backwardPass() {
        Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> itDescendingIterator = this.mObserverMap.descendingIterator();
        while (itDescendingIterator.hasNext() && !this.mNewEventOccurred) {
            Map.Entry<LifecycleObserver, ObserverWithState> next = itDescendingIterator.next();
            ObserverWithState value = next.getValue();
            while (value.mState.compareTo(this.mState) > 0 && !this.mNewEventOccurred && this.mObserverMap.contains(next.getKey())) {
                Lifecycle.Event eventDownEvent = downEvent(value.mState);
                pushParentState(getStateAfter(eventDownEvent));
                value.dispatchEvent(this.mLifecycleOwner, eventDownEvent);
                popParentState();
            }
        }
    }

    private void sync() {
        while (!isSynced()) {
            this.mNewEventOccurred = false;
            if (this.mState.compareTo(this.mObserverMap.eldest().getValue().mState) < 0) {
                backwardPass();
            }
            Map.Entry<LifecycleObserver, ObserverWithState> entryNewest = this.mObserverMap.newest();
            if (!this.mNewEventOccurred && entryNewest != null && this.mState.compareTo(entryNewest.getValue().mState) > 0) {
                forwardPass();
            }
        }
        this.mNewEventOccurred = false;
    }

    static Lifecycle.State min(@NonNull Lifecycle.State state, @Nullable Lifecycle.State state2) {
        return (state2 != null && state2.compareTo(state) < 0) ? state2 : state;
    }

    static class ObserverWithState {
        GenericLifecycleObserver mLifecycleObserver;
        Lifecycle.State mState;

        void dispatchEvent(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
            Lifecycle.State stateAfter = LifecycleRegistry.getStateAfter(event);
            this.mState = LifecycleRegistry.min(this.mState, stateAfter);
            this.mLifecycleObserver.onStateChanged(lifecycleOwner, event);
            this.mState = stateAfter;
        }
    }
}
