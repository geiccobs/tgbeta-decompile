package com.huawei.hms.hatool;

import android.content.Context;
import android.text.TextUtils;
/* loaded from: classes.dex */
public abstract class a1 {
    public final x0 a(int i) {
        String str;
        if (i != 0) {
            str = f();
            if (!TextUtils.isEmpty(str)) {
                return new x0(y0.UDID, str);
            }
        } else {
            str = "";
        }
        return new x0(y0.EMPTY, str);
    }

    public x0 a(Context context) {
        String c = c();
        if (!TextUtils.isEmpty(c)) {
            return new x0(y0.UDID, c);
        }
        String a = a();
        if (!TextUtils.isEmpty(a)) {
            return new x0(y0.IMEI, a);
        }
        boolean e = e();
        String b = b();
        return !TextUtils.isEmpty(b) ? e ? new x0(y0.SN, b) : new x0(y0.UDID, a(b)) : e ? a(d()) : b(d());
    }

    public abstract String a();

    public abstract String a(String str);

    public final x0 b(int i) {
        String str;
        if ((i & 4) != 0) {
            str = f();
            if (!TextUtils.isEmpty(str)) {
                return new x0(y0.UDID, str);
            }
        } else {
            str = "";
        }
        return new x0(y0.EMPTY, str);
    }

    public abstract String b();

    public abstract String c();

    public abstract int d();

    public final boolean e() {
        l b = i.c().b();
        if (TextUtils.isEmpty(b.l())) {
            b.h(f.a());
        }
        return !TextUtils.isEmpty(b.l());
    }

    public final String f() {
        l b = i.c().b();
        if (TextUtils.isEmpty(b.i())) {
            b.e(b1.c());
        }
        return b.i();
    }
}
