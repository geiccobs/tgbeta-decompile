package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import java.security.KeyStore;
import java.util.Calendar;
import javax.crypto.spec.IvParameterSpec;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class CryptoAesHandler implements CryptoHandler {
    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public String getAlgorithm() {
        return "AES/CBC/PKCS7Padding/256";
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public void generateKey(CryptoUtils.ICryptoFactory iCryptoFactory, String str, Context context) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(1, 1);
        CryptoUtils.IKeyGenerator keyGenerator = iCryptoFactory.getKeyGenerator("AES", "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder(str, 3).setBlockModes("CBC").setEncryptionPaddings("PKCS7Padding").setKeySize(256).setKeyValidityForOriginationEnd(calendar.getTime()).build());
        keyGenerator.generateKey();
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] encrypt(CryptoUtils.ICryptoFactory iCryptoFactory, int i, KeyStore.Entry entry, byte[] bArr) throws Exception {
        CryptoUtils.ICipher cipher = iCryptoFactory.getCipher("AES/CBC/PKCS7Padding", "AndroidKeyStoreBCWorkaround");
        cipher.init(1, ((KeyStore.SecretKeyEntry) entry).getSecretKey());
        byte[] iv = cipher.getIV();
        byte[] doFinal = cipher.doFinal(bArr);
        byte[] bArr2 = new byte[iv.length + doFinal.length];
        System.arraycopy(iv, 0, bArr2, 0, iv.length);
        System.arraycopy(doFinal, 0, bArr2, iv.length, doFinal.length);
        return bArr2;
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] decrypt(CryptoUtils.ICryptoFactory iCryptoFactory, int i, KeyStore.Entry entry, byte[] bArr) throws Exception {
        CryptoUtils.ICipher cipher = iCryptoFactory.getCipher("AES/CBC/PKCS7Padding", "AndroidKeyStoreBCWorkaround");
        int blockSize = cipher.getBlockSize();
        cipher.init(2, ((KeyStore.SecretKeyEntry) entry).getSecretKey(), new IvParameterSpec(bArr, 0, blockSize));
        return cipher.doFinal(bArr, blockSize, bArr.length - blockSize);
    }
}
