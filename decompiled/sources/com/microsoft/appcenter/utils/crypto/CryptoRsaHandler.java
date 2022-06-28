package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class CryptoRsaHandler implements CryptoHandler {
    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public String getAlgorithm() {
        return "RSA/ECB/PKCS1Padding/2048";
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public void generateKey(CryptoUtils.ICryptoFactory cryptoFactory, String alias, Context context) throws Exception {
        Calendar writeExpiry = Calendar.getInstance();
        writeExpiry.add(1, 1);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        KeyPairGeneratorSpec.Builder alias2 = new KeyPairGeneratorSpec.Builder(context).setAlias(alias);
        generator.initialize(alias2.setSubject(new X500Principal("CN=" + alias)).setStartDate(new Date()).setEndDate(writeExpiry.getTime()).setSerialNumber(BigInteger.TEN).setKeySize(2048).build());
        generator.generateKeyPair();
    }

    private CryptoUtils.ICipher getCipher(CryptoUtils.ICryptoFactory cipherFactory, int apiLevel) throws Exception {
        String provider;
        if (apiLevel >= 23) {
            provider = "AndroidKeyStoreBCWorkaround";
        } else {
            provider = "AndroidOpenSSL";
        }
        return cipherFactory.getCipher("RSA/ECB/PKCS1Padding", provider);
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] encrypt(CryptoUtils.ICryptoFactory cryptoFactory, int apiLevel, KeyStore.Entry keyStoreEntry, byte[] input) throws Exception {
        CryptoUtils.ICipher cipher = getCipher(cryptoFactory, apiLevel);
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStoreEntry;
        X509Certificate certificate = (X509Certificate) privateKeyEntry.getCertificate();
        try {
            certificate.checkValidity();
            cipher.init(1, certificate.getPublicKey());
            return cipher.doFinal(input);
        } catch (CertificateExpiredException e) {
            throw new InvalidKeyException(e);
        }
    }

    @Override // com.microsoft.appcenter.utils.crypto.CryptoHandler
    public byte[] decrypt(CryptoUtils.ICryptoFactory cryptoFactory, int apiLevel, KeyStore.Entry keyStoreEntry, byte[] data) throws Exception {
        CryptoUtils.ICipher cipher = getCipher(cryptoFactory, apiLevel);
        cipher.init(2, ((KeyStore.PrivateKeyEntry) keyStoreEntry).getPrivateKey());
        return cipher.doFinal(data);
    }
}
