package android.support.v4.view;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.compat.R$id;
import android.view.ViewGroup;

/* loaded from: classes.dex */
public final class ViewGroupCompat {
    static final ViewGroupCompatBaseImpl IMPL;

    static class ViewGroupCompatBaseImpl {
        ViewGroupCompatBaseImpl() {
        }

        public boolean isTransitionGroup(ViewGroup viewGroup) {
            Boolean bool = (Boolean) viewGroup.getTag(R$id.tag_transition_group);
            if ((bool == null || !bool.booleanValue()) && viewGroup.getBackground() == null) {
                return ViewCompat.getTransitionName(viewGroup) != null;
            }
            return true;
        }
    }

    @RequiresApi(18)
    static class ViewGroupCompatApi18Impl extends ViewGroupCompatBaseImpl {
        ViewGroupCompatApi18Impl() {
        }
    }

    @RequiresApi(21)
    static class ViewGroupCompatApi21Impl extends ViewGroupCompatApi18Impl {
        ViewGroupCompatApi21Impl() {
        }

        @Override // android.support.v4.view.ViewGroupCompat.ViewGroupCompatBaseImpl
        public boolean isTransitionGroup(ViewGroup viewGroup) {
            return viewGroup.isTransitionGroup();
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ViewGroupCompatApi21Impl();
        } else if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new ViewGroupCompatApi18Impl();
        } else {
            IMPL = new ViewGroupCompatBaseImpl();
        }
    }

    private ViewGroupCompat() {
    }

    public static boolean isTransitionGroup(ViewGroup viewGroup) {
        return IMPL.isTransitionGroup(viewGroup);
    }
}
