package androidx.core.view;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import java.lang.reflect.Field;
/* loaded from: classes.dex */
public final class LayoutInflaterCompat {
    private static boolean sCheckedField;
    private static Field sLayoutInflaterFactory2Field;

    private static void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        if (!sCheckedField) {
            try {
                Field declaredField = LayoutInflater.class.getDeclaredField("mFactory2");
                sLayoutInflaterFactory2Field = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 Could not find field 'mFactory2' on class " + LayoutInflater.class.getName() + "; inflation may have unexpected results.", e);
            }
            sCheckedField = true;
        }
        Field field = sLayoutInflaterFactory2Field;
        if (field != null) {
            try {
                field.set(inflater, factory);
            } catch (IllegalAccessException e2) {
                Log.e("LayoutInflaterCompatHC", "forceSetFactory2 could not set the Factory2 on LayoutInflater " + inflater + "; inflation may have unexpected results.", e2);
            }
        }
    }

    public static void setFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        inflater.setFactory2(factory);
        if (Build.VERSION.SDK_INT < 21) {
            LayoutInflater.Factory factory2 = inflater.getFactory();
            if (factory2 instanceof LayoutInflater.Factory2) {
                forceSetFactory2(inflater, (LayoutInflater.Factory2) factory2);
            } else {
                forceSetFactory2(inflater, factory);
            }
        }
    }
}
