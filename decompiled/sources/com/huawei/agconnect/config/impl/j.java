package com.huawei.agconnect.config.impl;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes.dex */
class j implements d {
    private final Context a;
    private final String b;

    public j(Context context, String str) {
        this.a = context;
        this.b = str;
    }

    private static String a(String str) {
        try {
            return "agc_" + Hex.encodeHexString(a(str.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException unused) {
            return "";
        }
    }

    private static byte[] a(byte[] bArr) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(bArr);
    }

    @Override // com.huawei.agconnect.config.impl.d
    public String a(String str, String str2) {
        int identifier;
        String a = a(str);
        if (!TextUtils.isEmpty(a) && (identifier = this.a.getResources().getIdentifier(a, "string", this.b)) != 0) {
            try {
                return this.a.getResources().getString(identifier);
            } catch (Resources.NotFoundException unused) {
                return str2;
            }
        }
        return str2;
    }
}
