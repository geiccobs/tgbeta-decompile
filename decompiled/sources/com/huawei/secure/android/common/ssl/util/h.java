package com.huawei.secure.android.common.ssl.util;

import android.content.Context;
import android.content.pm.PackageManager;
/* loaded from: classes.dex */
public class h {
    private static final String a = "h";

    public static String a(String str) {
        Context a2 = c.a();
        if (a2 == null) {
            return "";
        }
        try {
            return a2.getPackageManager().getPackageInfo(str, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = a;
            g.b(str2, "getVersion NameNotFoundException : " + e.getMessage());
            return "";
        } catch (Exception e2) {
            String str3 = a;
            g.b(str3, "getVersion: " + e2.getMessage());
            return "";
        } catch (Throwable unused) {
            g.b(a, "throwable");
            return "";
        }
    }
}
