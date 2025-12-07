package android.support.v4.content;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public final class SharedPreferencesCompat$EditorCompat {
    private static SharedPreferencesCompat$EditorCompat sInstance;
    private final Helper mHelper = new Helper();

    private static class Helper {
        Helper() {
        }

        public void apply(@NonNull SharedPreferences.Editor editor) {
            try {
                editor.apply();
            } catch (AbstractMethodError e) {
                editor.commit();
            }
        }
    }

    private SharedPreferencesCompat$EditorCompat() {
    }

    public static SharedPreferencesCompat$EditorCompat getInstance() {
        if (sInstance == null) {
            sInstance = new SharedPreferencesCompat$EditorCompat();
        }
        return sInstance;
    }

    public void apply(@NonNull SharedPreferences.Editor editor) {
        this.mHelper.apply(editor);
    }
}
