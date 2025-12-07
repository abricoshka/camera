package junit.framework;

@Deprecated
/* loaded from: classes.dex */
public class Assert {
    protected Assert() {
    }

    public static void assertTrue(String str, boolean z) {
        if (!z) {
            fail(str);
        }
    }

    public static void assertTrue(boolean z) {
        assertTrue(null, z);
    }

    public static void fail(String str) {
        if (str == null) {
            throw new AssertionFailedError();
        }
        throw new AssertionFailedError(str);
    }

    public static void assertNotNull(Object obj) {
        assertNotNull(null, obj);
    }

    public static void assertNotNull(String str, Object obj) {
        assertTrue(str, obj != null);
    }
}
