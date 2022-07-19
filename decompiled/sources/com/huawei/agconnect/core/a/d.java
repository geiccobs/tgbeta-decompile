package com.huawei.agconnect.core.a;

import android.content.Context;
import android.util.Log;
import com.huawei.agconnect.core.Service;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class d {
    private static Map<Class<?>, Service> a = new HashMap();
    private static Map<Class<?>, Object> b = new HashMap();
    private Map<Class<?>, Service> c = new HashMap();

    public d(List<Service> list, Context context) {
        new HashMap();
        a(list, context);
    }

    private static Constructor a(Class cls, Class... clsArr) {
        Constructor<?>[] declaredConstructors;
        boolean z = false;
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == clsArr.length) {
                for (int i = 0; i < clsArr.length; i++) {
                    z = parameterTypes[i] == clsArr[i];
                }
                if (z) {
                    return constructor;
                }
            }
        }
        return null;
    }

    private void a(String str, Exception exc) {
        Log.e("ServiceRepository", "Instantiate shared service " + str + exc.getLocalizedMessage());
        StringBuilder sb = new StringBuilder();
        sb.append("cause message:");
        sb.append(exc.getCause() != null ? exc.getCause().getMessage() : "");
        Log.e("ServiceRepository", sb.toString());
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x005b A[Catch: InvocationTargetException -> 0x0076, InstantiationException -> 0x007a, IllegalAccessException -> 0x007e, TryCatch #2 {IllegalAccessException -> 0x007e, InstantiationException -> 0x007a, InvocationTargetException -> 0x0076, blocks: (B:20:0x0049, B:22:0x005b, B:23:0x0064, B:24:0x006c), top: B:33:0x0049 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0064 A[Catch: InvocationTargetException -> 0x0076, InstantiationException -> 0x007a, IllegalAccessException -> 0x007e, TryCatch #2 {IllegalAccessException -> 0x007e, InstantiationException -> 0x007a, InvocationTargetException -> 0x0076, blocks: (B:20:0x0049, B:22:0x005b, B:23:0x0064, B:24:0x006c), top: B:33:0x0049 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void a(java.util.List<com.huawei.agconnect.core.Service> r7, android.content.Context r8) {
        /*
            r6 = this;
            if (r7 != 0) goto L3
            return
        L3:
            java.util.Iterator r7 = r7.iterator()
        L7:
            boolean r0 = r7.hasNext()
            if (r0 == 0) goto L85
            java.lang.Object r0 = r7.next()
            com.huawei.agconnect.core.Service r0 = (com.huawei.agconnect.core.Service) r0
            boolean r1 = r0.isSharedInstance()
            if (r1 == 0) goto L28
            java.util.Map<java.lang.Class<?>, com.huawei.agconnect.core.Service> r1 = com.huawei.agconnect.core.a.d.a
            java.lang.Class r2 = r0.getInterface()
            boolean r1 = r1.containsKey(r2)
            if (r1 != 0) goto L31
            java.util.Map<java.lang.Class<?>, com.huawei.agconnect.core.Service> r1 = com.huawei.agconnect.core.a.d.a
            goto L2a
        L28:
            java.util.Map<java.lang.Class<?>, com.huawei.agconnect.core.Service> r1 = r6.c
        L2a:
            java.lang.Class r2 = r0.getInterface()
            r1.put(r2, r0)
        L31:
            boolean r1 = r0.isAutoCreated()
            if (r1 == 0) goto L7
            java.lang.Class r1 = r0.getType()
            if (r1 == 0) goto L7
            java.util.Map<java.lang.Class<?>, java.lang.Object> r1 = com.huawei.agconnect.core.a.d.b
            java.lang.Class r2 = r0.getInterface()
            boolean r1 = r1.containsKey(r2)
            if (r1 != 0) goto L7
            java.lang.Class r1 = r0.getType()     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            r2 = 1
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            java.lang.Class<android.content.Context> r4 = android.content.Context.class
            r5 = 0
            r3[r5] = r4     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            java.lang.reflect.Constructor r1 = a(r1, r3)     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            if (r1 == 0) goto L64
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            r2[r5] = r8     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            java.lang.Object r1 = r1.newInstance(r2)     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            goto L6c
        L64:
            java.lang.Class r1 = r0.getType()     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            java.lang.Object r1 = r1.newInstance()     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
        L6c:
            java.util.Map<java.lang.Class<?>, java.lang.Object> r2 = com.huawei.agconnect.core.a.d.b     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            java.lang.Class r0 = r0.getInterface()     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            r2.put(r0, r1)     // Catch: java.lang.reflect.InvocationTargetException -> L76 java.lang.InstantiationException -> L7a java.lang.IllegalAccessException -> L7e
            goto L7
        L76:
            r0 = move-exception
            java.lang.String r1 = "TargetException"
            goto L81
        L7a:
            r0 = move-exception
            java.lang.String r1 = "InstantiationException"
            goto L81
        L7e:
            r0 = move-exception
            java.lang.String r1 = "AccessException"
        L81:
            r6.a(r1, r0)
            goto L7
        L85:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.huawei.agconnect.core.a.d.a(java.util.List, android.content.Context):void");
    }
}
