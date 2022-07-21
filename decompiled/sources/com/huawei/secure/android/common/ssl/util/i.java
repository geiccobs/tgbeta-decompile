package com.huawei.secure.android.common.ssl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
/* loaded from: classes.dex */
public class i {
    private static SharedPreferences b;

    public static synchronized SharedPreferences b(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (i.class) {
            if (b == null) {
                if (Build.VERSION.SDK_INT >= 24) {
                    b = context.createDeviceProtectedStorageContext().getSharedPreferences("aegis", 0);
                } else {
                    b = context.getApplicationContext().getSharedPreferences("aegis", 0);
                }
            }
            sharedPreferences = b;
        }
        return sharedPreferences;
    }

    public static String a(String str, String str2, Context context) {
        return b(context).getString(str, str2);
    }

    public static void b(String str, String str2, Context context) {
        b(context).edit().putString(str, str2).apply();
    }
}
