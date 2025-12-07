package android.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
@Retention(RetentionPolicy.CLASS)
/* loaded from: classes.dex */
public @interface RestrictTo {
    Scope[] value();

    public enum Scope {
        LIBRARY,
        LIBRARY_GROUP,
        GROUP_ID,
        TESTS,
        SUBCLASSES;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Scope[] valuesCustom() {
            return values();
        }
    }
}
