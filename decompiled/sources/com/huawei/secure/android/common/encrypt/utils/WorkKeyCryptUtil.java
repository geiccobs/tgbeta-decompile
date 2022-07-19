package com.huawei.secure.android.common.encrypt.utils;

import com.huawei.secure.android.common.encrypt.aes.AesCbc;
/* loaded from: classes.dex */
public class WorkKeyCryptUtil {
    public static String decryptWorkKey(String str, RootKeyUtil rootKeyUtil) {
        return AesCbc.decrypt(str, rootKeyUtil.getRootKey());
    }

    public static String decryptWorkKey(String str, byte[] bArr) {
        return AesCbc.decrypt(str, bArr);
    }

    public static String encryptWorkKey(String str, byte[] bArr) {
        return AesCbc.encrypt(str, bArr);
    }
}
