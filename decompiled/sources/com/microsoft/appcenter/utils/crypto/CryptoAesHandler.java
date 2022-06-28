package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import java.security.KeyStore;
import java.util.Calendar;
import javax.crypto.spec.IvParameterSpec;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class CryptoAesHandler implements CryptoHandler {
    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public String getAlgorithm() {
        return "AES/CBC/PKCS7Padding/256";
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public void generateKey(CryptoUtils.ICryptoFactory cryptoFactory, String alias, Context context) throws Exception {
        Calendar writeExpiry = Calendar.getInstance();
        writeExpiry.add(1, 1);
        CryptoUtils.IKeyGenerator keyGenerator = cryptoFactory.getKeyGenerator("AES", "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder(alias, 3).setBlockModes("CBC").setEncryptionPaddings("PKCS7Padding").setKeySize(256).setKeyValidityForOriginationEnd(writeExpiry.getTime()).build());
        keyGenerator.generateKey();
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] encrypt(CryptoUtils.ICryptoFactory cryptoFactory, int apiLevel, KeyStore.Entry keyStoreEntry, byte[] input) throws Exception {
        CryptoUtils.ICipher cipher = cryptoFactory.getCipher("AES/CBC/PKCS7Padding", "AndroidKeyStoreBCWorkaround");
        cipher.init(1, ((KeyStore.SecretKeyEntry) keyStoreEntry).getSecretKey());
        byte[] cipherIV = cipher.getIV();
        byte[] output = cipher.doFinal(input);
        byte[] encryptedBytes = new byte[cipherIV.length + output.length];
        System.arraycopy(cipherIV, 0, encryptedBytes, 0, cipherIV.length);
        System.arraycopy(output, 0, encryptedBytes, cipherIV.length, output.length);
        return encryptedBytes;
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] decrypt(CryptoUtils.ICryptoFactory cryptoFactory, int apiLevel, KeyStore.Entry keyStoreEntry, byte[] data) throws Exception {
        CryptoUtils.ICipher cipher = cryptoFactory.getCipher("AES/CBC/PKCS7Padding", "AndroidKeyStoreBCWorkaround");
        int blockSize = cipher.getBlockSize();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(data, 0, blockSize);
        cipher.init(2, ((KeyStore.SecretKeyEntry) keyStoreEntry).getSecretKey(), ivParameterSpec);
        return cipher.doFinal(data, blockSize, data.length - blockSize);
    }
}
