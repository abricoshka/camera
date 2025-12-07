package com.mediatek.camera;

import com.mediatek.camera.ICameraMode;

/* loaded from: classes.dex */
public interface ICameraAddition {

    interface Listener {
        void onFileSaveing();

        boolean restartPreview(boolean z);
    }

    void close();

    void destory();

    boolean execute(AdditionActionType additionActionType, Object... objArr);

    boolean execute(ICameraMode.ActionType actionType, Object... objArr);

    boolean isOpen();

    boolean isSupport();

    void open();

    void pause();

    void resume();

    void setListener(Listener listener);

    enum AdditionActionType {
        ACTION_TAKEN_PICTURE,
        ACTION_ON_START_PREVIEW,
        ACTION_ON_STOP_PREVIEW,
        ACTION_EFFECT_CLICK,
        ACTION_ON_SWITCH_PIP;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AdditionActionType[] valuesCustom() {
            return values();
        }
    }
}
