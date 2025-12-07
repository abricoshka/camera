package com.mediatek.camera.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ReflectUtil {
    public static Object callMethodOnObject(Object obj, Method method, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            Log.m33e("ReflectUtil", "[callMethodOnObject]", e);
            return null;
        } catch (InvocationTargetException e2) {
            Log.m33e("ReflectUtil", "[callMethodOnObject]", e2);
            return null;
        }
    }

    public static Object callMethodOnObjectWithExp(Object obj, Method method, Object... objArr) throws InvocationTargetException {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            Log.m33e("ReflectUtil", "[callMethodOnObjectWithExp]", e);
            return null;
        }
    }

    public static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException, SecurityException {
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException e) {
            Log.m33e("ReflectUtil", "[getMethod]", e);
            return null;
        }
    }
}
