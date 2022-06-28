package org.aspectj.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
/* loaded from: classes3.dex */
public class Aspects14 {
    private static final String ASPECTOF = "aspectOf";
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private static final String HASASPECT = "hasAspect";
    private static final Class[] PEROBJECT_CLASS_ARRAY;
    private static final Class[] PERTYPEWITHIN_CLASS_ARRAY;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$java$lang$Object;

    static {
        Class[] clsArr = new Class[1];
        Class cls = class$java$lang$Object;
        if (cls == null) {
            cls = class$("java.lang.Object");
            class$java$lang$Object = cls;
        }
        clsArr[0] = cls;
        PEROBJECT_CLASS_ARRAY = clsArr;
        Class[] clsArr2 = new Class[1];
        Class cls2 = class$java$lang$Class;
        if (cls2 == null) {
            cls2 = class$("java.lang.Class");
            class$java$lang$Class = cls2;
        }
        clsArr2[0] = cls2;
        PERTYPEWITHIN_CLASS_ARRAY = clsArr2;
        EMPTY_OBJECT_ARRAY = new Object[0];
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static Object aspectOf(Class aspectClass) throws NoAspectBoundException {
        try {
            return getSingletonOrThreadAspectOf(aspectClass).invoke(null, EMPTY_OBJECT_ARRAY);
        } catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        } catch (Exception e2) {
            throw new NoAspectBoundException(aspectClass.getName(), e2);
        }
    }

    public static Object aspectOf(Class aspectClass, Object perObject) throws NoAspectBoundException {
        try {
            return getPerObjectAspectOf(aspectClass).invoke(null, perObject);
        } catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        } catch (Exception e2) {
            throw new NoAspectBoundException(aspectClass.getName(), e2);
        }
    }

    public static Object aspectOf(Class aspectClass, Class perTypeWithin) throws NoAspectBoundException {
        try {
            return getPerTypeWithinAspectOf(aspectClass).invoke(null, perTypeWithin);
        } catch (InvocationTargetException e) {
            throw new NoAspectBoundException(aspectClass.getName(), e);
        } catch (Exception e2) {
            throw new NoAspectBoundException(aspectClass.getName(), e2);
        }
    }

    public static boolean hasAspect(Class aspectClass) throws NoAspectBoundException {
        try {
            return ((Boolean) getSingletonOrThreadHasAspect(aspectClass).invoke(null, EMPTY_OBJECT_ARRAY)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class aspectClass, Object perObject) throws NoAspectBoundException {
        try {
            return ((Boolean) getPerObjectHasAspect(aspectClass).invoke(null, perObject)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasAspect(Class aspectClass, Class perTypeWithin) throws NoAspectBoundException {
        try {
            return ((Boolean) getPerTypeWithinHasAspect(aspectClass).invoke(null, perTypeWithin)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    private static Method getSingletonOrThreadAspectOf(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, EMPTY_CLASS_ARRAY);
        return checkAspectOf(method, aspectClass);
    }

    private static Method getPerObjectAspectOf(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, PEROBJECT_CLASS_ARRAY);
        return checkAspectOf(method, aspectClass);
    }

    private static Method getPerTypeWithinAspectOf(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(ASPECTOF, PERTYPEWITHIN_CLASS_ARRAY);
        return checkAspectOf(method, aspectClass);
    }

    private static Method checkAspectOf(Method method, Class aspectClass) throws NoSuchMethodException {
        method.setAccessible(true);
        if (!method.isAccessible() || !Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(aspectClass.getName());
            stringBuffer.append(".aspectOf(..) is not accessible public static");
            throw new NoSuchMethodException(stringBuffer.toString());
        }
        return method;
    }

    private static Method getSingletonOrThreadHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, EMPTY_CLASS_ARRAY);
        return checkHasAspect(method, aspectClass);
    }

    private static Method getPerObjectHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, PEROBJECT_CLASS_ARRAY);
        return checkHasAspect(method, aspectClass);
    }

    private static Method getPerTypeWithinHasAspect(Class aspectClass) throws NoSuchMethodException {
        Method method = aspectClass.getDeclaredMethod(HASASPECT, PERTYPEWITHIN_CLASS_ARRAY);
        return checkHasAspect(method, aspectClass);
    }

    private static Method checkHasAspect(Method method, Class aspectClass) throws NoSuchMethodException {
        method.setAccessible(true);
        if (!method.isAccessible() || !Modifier.isPublic(method.getModifiers()) || !Modifier.isStatic(method.getModifiers())) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(aspectClass.getName());
            stringBuffer.append(".hasAspect(..) is not accessible public static");
            throw new NoSuchMethodException(stringBuffer.toString());
        }
        return method;
    }
}
