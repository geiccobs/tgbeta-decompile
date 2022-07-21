package com.huawei.hms.hatool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import com.huawei.secure.android.common.encrypt.hash.SHA;
import java.util.UUID;
/* loaded from: classes.dex */
public class z0 {
    public static z0 b;
    public Context a;

    /* loaded from: classes.dex */
    public static class a extends a1 {
        public String a;
        public String b;

        public a(String str, String str2) {
            this.a = str;
            this.b = str2;
        }

        @Override // com.huawei.hms.hatool.a1
        public String a() {
            return com.huawei.hms.hatool.a.d(this.a, this.b);
        }

        @Override // com.huawei.hms.hatool.a1
        public String a(String str) {
            return SHA.sha256Encrypt(str);
        }

        @Override // com.huawei.hms.hatool.a1
        public String b() {
            return com.huawei.hms.hatool.a.g(this.a, this.b);
        }

        @Override // com.huawei.hms.hatool.a1
        public String c() {
            return com.huawei.hms.hatool.a.j(this.a, this.b);
        }

        @Override // com.huawei.hms.hatool.a1
        public int d() {
            int i = 0;
            int i2 = (com.huawei.hms.hatool.a.k(this.a, this.b) ? 4 : 0) | 0;
            if (com.huawei.hms.hatool.a.e(this.a, this.b)) {
                i = 2;
            }
            return i2 | i | (com.huawei.hms.hatool.a.h(this.a, this.b) ? 1 : 0);
        }
    }

    public static z0 a() {
        z0 z0Var;
        synchronized (z0.class) {
            if (b == null) {
                b = new z0();
            }
            z0Var = b;
        }
        return z0Var;
    }

    public String a(String str, String str2) {
        return g.a(this.a, str, str2);
    }

    public String a(boolean z) {
        if (!z) {
            return "";
        }
        String e = b.e();
        if (TextUtils.isEmpty(e)) {
            e = g0.a(this.a, "global_v2", "uuid", "");
            if (TextUtils.isEmpty(e)) {
                e = UUID.randomUUID().toString().replace("-", "");
                g0.b(this.a, "global_v2", "uuid", e);
            }
            b.h(e);
        }
        return e;
    }

    public void a(Context context) {
        if (this.a == null) {
            this.a = context;
        }
    }

    public String b(String str, String str2) {
        return g.b(this.a, str, str2);
    }

    public x0 c(String str, String str2) {
        return new a(str, str2).a(this.a);
    }

    public String d(String str, String str2) {
        return c1.b(str, str2);
    }

    public Pair<String, String> e(String str, String str2) {
        if (com.huawei.hms.hatool.a.f(str, str2)) {
            String p = i.c().b().p();
            String q = i.c().b().q();
            if (!TextUtils.isEmpty(p) && !TextUtils.isEmpty(q)) {
                return new Pair<>(p, q);
            }
            Pair<String, String> e = b1.e(this.a);
            i.c().b().k((String) e.first);
            i.c().b().l((String) e.second);
            return e;
        }
        return new Pair<>("", "");
    }

    public String f(String str, String str2) {
        return c1.a(str, str2);
    }
}
