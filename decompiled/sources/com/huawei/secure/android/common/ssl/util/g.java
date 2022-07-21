package com.huawei.secure.android.common.ssl.util;

import android.util.Log;
/* loaded from: classes.dex */
public class g {
    public static void a(String str, String str2) {
    }

    public static void a(String str, String str2, Throwable th) {
        Log.e(a(str), str2, th);
    }

    public static void b(String str, String str2) {
        Log.e(a(str), str2);
    }

    public static void c(String str, String str2) {
        Log.i(a(str), str2);
    }

    public static void e(String str, String str2) {
        Log.w(a(str), str2);
    }

    private static String a(String str) {
        return "SecurityComp10105310: " + str;
    }
}
