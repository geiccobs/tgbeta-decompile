package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import java.security.KeyStore;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CryptoNoOpHandler implements CryptoHandler {
    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] decrypt(CryptoUtils.ICryptoFactory iCryptoFactory, int i, KeyStore.Entry entry, byte[] bArr) {
        return bArr;
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] encrypt(CryptoUtils.ICryptoFactory iCryptoFactory, int i, KeyStore.Entry entry, byte[] bArr) {
        return bArr;
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public void generateKey(CryptoUtils.ICryptoFactory iCryptoFactory, String str, Context context) {
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public String getAlgorithm() {
        return "None";
    }
}
