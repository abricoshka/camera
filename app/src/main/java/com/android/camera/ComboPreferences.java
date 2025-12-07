package com.android.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: classes.dex */
public class ComboPreferences implements SharedPreferences, SharedPreferences.OnSharedPreferenceChangeListener {
    private static WeakHashMap<Context, ComboPreferences> sMap = new WeakHashMap<>();
    private boolean mIsSecureCamera;
    private CopyOnWriteArrayList<SharedPreferences.OnSharedPreferenceChangeListener> mListeners;
    private SharedPreferences mPrefGlobal;
    private SharedPreferences mPrefLocal;

    public ComboPreferences(Context context) {
        this.mIsSecureCamera = false;
        new ComboPreferences(context, false);
    }

    public ComboPreferences(Context context, boolean z) {
        this.mIsSecureCamera = false;
        this.mPrefGlobal = PreferenceManager.getDefaultSharedPreferences(context);
        this.mPrefGlobal.registerOnSharedPreferenceChangeListener(this);
        synchronized (sMap) {
            sMap.put(context, this);
        }
        this.mListeners = new CopyOnWriteArrayList<>();
        this.mIsSecureCamera = z;
    }

    public void setLocalId(Context context, int i) {
        String str = context.getPackageName() + "_preferences_" + i;
        if (this.mIsSecureCamera) {
            str = context.getPackageName() + "_preferences_secure" + i;
        }
        if (this.mPrefLocal != null) {
            this.mPrefLocal.unregisterOnSharedPreferenceChangeListener(this);
        }
        this.mPrefLocal = context.getSharedPreferences(str, 0);
        this.mPrefLocal.registerOnSharedPreferenceChangeListener(this);
    }

    public SharedPreferences getSharedPreference(Context context, int i) {
        String str = context.getPackageName() + "_preferences_" + i;
        if (this.mIsSecureCamera) {
            str = context.getPackageName() + "_preferences_secure" + i;
        }
        return context.getSharedPreferences(str, 0);
    }

    public SharedPreferences getGlobal() {
        return this.mPrefGlobal;
    }

    public SharedPreferences getLocal() {
        return this.mPrefLocal;
    }

    @Override // android.content.SharedPreferences
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isGlobal(String str) {
        if (str.equals("pref_camera_id_key") || str.equals("pref_camera_recordlocation_key")) {
            return true;
        }
        return str.equals("pref_face_beauty_multi_mode_key");
    }

    @Override // android.content.SharedPreferences
    public String getString(String str, String str2) {
        if (isGlobal(str) || (!this.mPrefLocal.contains(str))) {
            return this.mPrefGlobal.getString(str, str2);
        }
        return this.mPrefLocal.getString(str, str2);
    }

    @Override // android.content.SharedPreferences
    public int getInt(String str, int i) {
        if (isGlobal(str) || (!this.mPrefLocal.contains(str))) {
            return this.mPrefGlobal.getInt(str, i);
        }
        return this.mPrefLocal.getInt(str, i);
    }

    @Override // android.content.SharedPreferences
    public long getLong(String str, long j) {
        if (isGlobal(str) || (!this.mPrefLocal.contains(str))) {
            return this.mPrefGlobal.getLong(str, j);
        }
        return this.mPrefLocal.getLong(str, j);
    }

    @Override // android.content.SharedPreferences
    public float getFloat(String str, float f) {
        if (isGlobal(str) || (!this.mPrefLocal.contains(str))) {
            return this.mPrefGlobal.getFloat(str, f);
        }
        return this.mPrefLocal.getFloat(str, f);
    }

    @Override // android.content.SharedPreferences
    public boolean getBoolean(String str, boolean z) {
        if (isGlobal(str) || (!this.mPrefLocal.contains(str))) {
            return this.mPrefGlobal.getBoolean(str, z);
        }
        return this.mPrefLocal.getBoolean(str, z);
    }

    @Override // android.content.SharedPreferences
    public Set<String> getStringSet(String str, Set<String> set) {
        throw new UnsupportedOperationException();
    }

    @Override // android.content.SharedPreferences
    public boolean contains(String str) {
        if (this.mPrefLocal.contains(str)) {
            return true;
        }
        return this.mPrefGlobal.contains(str);
    }

    private class MyEditor implements SharedPreferences.Editor {
        private SharedPreferences.Editor mEditorGlobal;
        private SharedPreferences.Editor mEditorLocal;

        MyEditor() {
            this.mEditorGlobal = ComboPreferences.this.mPrefGlobal.edit();
            this.mEditorLocal = ComboPreferences.this.mPrefLocal.edit();
        }

        @Override // android.content.SharedPreferences.Editor
        public boolean commit() {
            boolean zCommit = this.mEditorGlobal.commit();
            boolean zCommit2 = this.mEditorLocal.commit();
            if (zCommit) {
                return zCommit2;
            }
            return false;
        }

        @Override // android.content.SharedPreferences.Editor
        public void apply() {
            this.mEditorGlobal.apply();
            this.mEditorLocal.apply();
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor clear() {
            this.mEditorGlobal.clear();
            this.mEditorLocal.clear();
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor remove(String str) {
            this.mEditorGlobal.remove(str);
            this.mEditorLocal.remove(str);
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putString(String str, String str2) {
            if (ComboPreferences.isGlobal(str)) {
                this.mEditorGlobal.putString(str, str2);
            } else {
                this.mEditorLocal.putString(str, str2);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putInt(String str, int i) {
            if (ComboPreferences.isGlobal(str)) {
                this.mEditorGlobal.putInt(str, i);
            } else {
                this.mEditorLocal.putInt(str, i);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putLong(String str, long j) {
            if (ComboPreferences.isGlobal(str)) {
                this.mEditorGlobal.putLong(str, j);
            } else {
                this.mEditorLocal.putLong(str, j);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putFloat(String str, float f) {
            if (ComboPreferences.isGlobal(str)) {
                this.mEditorGlobal.putFloat(str, f);
            } else {
                this.mEditorLocal.putFloat(str, f);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putBoolean(String str, boolean z) {
            if (ComboPreferences.isGlobal(str)) {
                this.mEditorGlobal.putBoolean(str, z);
            } else {
                this.mEditorLocal.putBoolean(str, z);
            }
            return this;
        }

        @Override // android.content.SharedPreferences.Editor
        public SharedPreferences.Editor putStringSet(String str, Set<String> set) {
            throw new UnsupportedOperationException();
        }
    }

    @Override // android.content.SharedPreferences
    public SharedPreferences.Editor edit() {
        return new MyEditor();
    }

    @Override // android.content.SharedPreferences
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.mListeners.add(onSharedPreferenceChangeListener);
    }

    @Override // android.content.SharedPreferences
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.mListeners.remove(onSharedPreferenceChangeListener);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        Iterator<T> it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((SharedPreferences.OnSharedPreferenceChangeListener) it.next()).onSharedPreferenceChanged(this, str);
        }
    }
}
