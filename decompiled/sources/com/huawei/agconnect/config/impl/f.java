package com.huawei.agconnect.config.impl;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
/* loaded from: classes.dex */
class f implements g {
    private SecretKey a;

    public f(String str, String str2, String str3, String str4) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalArgumentException {
        if (str == null || str2 == null || str3 == null || str4 == null) {
            return;
        }
        this.a = i.a(Hex.decodeHexString(str), Hex.decodeHexString(str2), Hex.decodeHexString(str3), Hex.decodeHexString(str4), 5000);
    }

    @Override // com.huawei.agconnect.config.impl.g
    public String a(String str, String str2) {
        SecretKey secretKey = this.a;
        if (secretKey == null) {
            return str;
        }
        try {
            return new String(i.a(secretKey, Hex.decodeHexString(str)), "UTF-8");
        } catch (UnsupportedEncodingException | IllegalArgumentException | GeneralSecurityException unused) {
            return str2;
        }
    }
}
