package com.huawei.agconnect.config.impl;

import android.content.Context;
import android.util.Log;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class k extends j {
    private final Map<String, String> a = new HashMap();
    private final Object b = new Object();
    private g c;
    private boolean d;

    public k(Context context, String str) {
        super(context, str);
        this.d = true;
        try {
            String a = a("/AD91D45E3E72DB6989DDCB13287E75061FABCB933D886E6C6ABEF0939B577138");
            String a2 = a("/B314B3BF013DF5AC4134E880AF3D2B7C9FFBE8F0305EAC1C898145E2BCF1F21C");
            String a3 = a("/C767BD8FDF53E53D059BE95B09E2A71056F5F180AECC62836B287ACA5793421B");
            String a4 = a("/DCB3E6D4C2CF80F30D89CDBC412C964DA8381BB84668769391FBCC3E329AD0FD");
            if (a == null || a2 == null || a3 == null || a4 == null) {
                this.d = false;
            } else {
                this.c = new f(a, a2, a3, a4);
            }
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException unused) {
            Log.e("SecurityResourcesReader", "Exception when reading the 'K&I' for 'Config'.");
            this.c = null;
        }
    }

    private String a(String str) {
        return super.a(str, null);
    }

    @Override // com.huawei.agconnect.config.impl.j, com.huawei.agconnect.config.impl.d
    public String a(String str, String str2) {
        if (!this.d) {
            String a = a(str);
            return a != null ? a : str2;
        } else if (this.c == null) {
            Log.e("SecurityResourcesReader", "KEY is null return def directly");
            return str2;
        } else {
            synchronized (this.b) {
                String str3 = this.a.get(str);
                if (str3 != null) {
                    return str3;
                }
                String a2 = a(str);
                if (a2 == null) {
                    return str2;
                }
                String a3 = this.c.a(a2, str2);
                this.a.put(str, a3);
                return a3;
            }
        }
    }

    public String toString() {
        return "SecurityResourcesReader{mKey=, encrypt=" + this.d + '}';
    }
}
