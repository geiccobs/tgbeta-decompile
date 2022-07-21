package com.huawei.agconnect.config.impl;

import android.content.Context;
import android.text.TextUtils;
import com.huawei.agconnect.AGConnectApp;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class a extends AGConnectApp {
    private static final Map<String, AGConnectApp> a = new HashMap();
    private static final Object b = new Object();
    private static String c;

    private a(Context context, String str) {
        AGConnectServicesConfig.fromContext(context, str);
    }

    public static AGConnectApp a(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            context = applicationContext;
        }
        String packageName = context.getPackageName();
        c = packageName;
        return a(context, packageName);
    }

    public static AGConnectApp a(Context context, String str) {
        AGConnectApp aGConnectApp;
        if (!TextUtils.isEmpty(str)) {
            synchronized (b) {
                Map<String, AGConnectApp> map = a;
                aGConnectApp = map.get(str);
                if (aGConnectApp == null) {
                    map.put(str, new a(context, str));
                }
            }
            return aGConnectApp;
        }
        throw new IllegalArgumentException("packageName can not be empty");
    }
}
