package com.huawei.secure.android.common.encrypt.keystore.aes;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.text.TextUtils;
import com.huawei.secure.android.common.encrypt.utils.HexUtil;
import com.huawei.secure.android.common.encrypt.utils.b;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class AesGcmKS {
    private static Map<String, SecretKey> g = new HashMap();

    private static SecretKey a(String str) {
        b.c("GCMKS", "load key");
        SecretKey secretKey = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            Key key = keyStore.getKey(str, null);
            if (key instanceof SecretKey) {
                secretKey = (SecretKey) key;
            } else {
                b.c("GCMKS", "generate key");
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
                keyGenerator.init(new KeyGenParameterSpec.Builder(str, 3).setBlockModes("GCM").setEncryptionPaddings("NoPadding").setKeySize(256).build());
                secretKey = keyGenerator.generateKey();
            }
        } catch (IOException e) {
            b.b("GCMKS", "IOException : " + e.getMessage());
        } catch (InvalidAlgorithmParameterException e2) {
            b.b("GCMKS", "InvalidAlgorithmParameterException : " + e2.getMessage());
        } catch (KeyStoreException e3) {
            b.b("GCMKS", "KeyStoreException : " + e3.getMessage());
        } catch (NoSuchAlgorithmException e4) {
            b.b("GCMKS", "NoSuchAlgorithmException : " + e4.getMessage());
        } catch (NoSuchProviderException e5) {
            b.b("GCMKS", "NoSuchProviderException : " + e5.getMessage());
        } catch (UnrecoverableKeyException e6) {
            b.b("GCMKS", "UnrecoverableKeyException : " + e6.getMessage());
        } catch (CertificateException e7) {
            b.b("GCMKS", "CertificateException : " + e7.getMessage());
        } catch (Exception e8) {
            b.b("GCMKS", "Exception: " + e8.getMessage());
        }
        g.put(str, secretKey);
        return secretKey;
    }

    private static SecretKey b(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (g.get(str) == null) {
            a(str);
        }
        return g.get(str);
    }

    public static String decrypt(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                return new String(decrypt(str, HexUtil.hexStr2ByteArray(str2)), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                b.b("GCMKS", "decrypt: UnsupportedEncodingException : " + e.getMessage());
                return "";
            }
        }
        b.b("GCMKS", "alias or encrypt content is null");
        return "";
    }

    public static String encrypt(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                return HexUtil.byteArray2HexStr(encrypt(str, str2.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                b.b("GCMKS", "encrypt: UnsupportedEncodingException : " + e.getMessage());
                return "";
            }
        }
        b.b("GCMKS", "alias or encrypt content is null");
        return "";
    }

    public static byte[] decrypt(String str, byte[] bArr) {
        byte[] bArr2 = new byte[0];
        if (!TextUtils.isEmpty(str) && bArr != null) {
            if (!a()) {
                b.b("GCMKS", "sdk version is too low");
                return bArr2;
            } else if (bArr.length <= 12) {
                b.b("GCMKS", "Decrypt source data is invalid.");
                return bArr2;
            } else {
                return decrypt(b(str), bArr);
            }
        }
        b.b("GCMKS", "alias or encrypt content is null");
        return bArr2;
    }

    public static byte[] encrypt(String str, byte[] bArr) {
        byte[] bArr2 = new byte[0];
        if (!TextUtils.isEmpty(str) && bArr != null) {
            if (!a()) {
                b.b("GCMKS", "sdk version is too low");
                return bArr2;
            }
            return encrypt(b(str), bArr);
        }
        b.b("GCMKS", "alias or encrypt content is null");
        return bArr2;
    }

    public static byte[] encrypt(SecretKey secretKey, byte[] bArr) {
        byte[] bArr2 = new byte[0];
        if (bArr == null) {
            b.b("GCMKS", "content is null");
            return bArr2;
        } else if (secretKey == null) {
            b.b("GCMKS", "secret key is null");
            return bArr2;
        } else if (!a()) {
            b.b("GCMKS", "sdk version is too low");
            return bArr2;
        } else {
            try {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(1, secretKey);
                byte[] doFinal = cipher.doFinal(bArr);
                byte[] iv = cipher.getIV();
                if (iv != null && iv.length == 12) {
                    byte[] copyOf = Arrays.copyOf(iv, iv.length + doFinal.length);
                    System.arraycopy(doFinal, 0, copyOf, iv.length, doFinal.length);
                    return copyOf;
                }
                b.b("GCMKS", "IV is invalid.");
                return bArr2;
            } catch (InvalidKeyException e) {
                b.b("GCMKS", "InvalidKeyException : " + e.getMessage());
                return bArr2;
            } catch (NoSuchAlgorithmException e2) {
                b.b("GCMKS", "NoSuchAlgorithmException : " + e2.getMessage());
                return bArr2;
            } catch (BadPaddingException e3) {
                b.b("GCMKS", "BadPaddingException : " + e3.getMessage());
                return bArr2;
            } catch (IllegalBlockSizeException e4) {
                b.b("GCMKS", "IllegalBlockSizeException : " + e4.getMessage());
                return bArr2;
            } catch (NoSuchPaddingException e5) {
                b.b("GCMKS", "NoSuchPaddingException : " + e5.getMessage());
                return bArr2;
            } catch (Exception e6) {
                b.b("GCMKS", "Exception: " + e6.getMessage());
                return bArr2;
            }
        }
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] bArr) {
        byte[] bArr2 = new byte[0];
        if (secretKey == null) {
            b.b("GCMKS", "Decrypt secret key is null");
            return bArr2;
        } else if (bArr == null) {
            b.b("GCMKS", "content is null");
            return bArr2;
        } else if (!a()) {
            b.b("GCMKS", "sdk version is too low");
            return bArr2;
        } else if (bArr.length <= 12) {
            b.b("GCMKS", "Decrypt source data is invalid.");
            return bArr2;
        } else {
            byte[] copyOf = Arrays.copyOf(bArr, 12);
            try {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(2, secretKey, new GCMParameterSpec(ConnectionsManager.RequestFlagNeedQuickAck, copyOf));
                return cipher.doFinal(bArr, 12, bArr.length - 12);
            } catch (InvalidAlgorithmParameterException e) {
                b.b("GCMKS", "InvalidAlgorithmParameterException : " + e.getMessage());
                return bArr2;
            } catch (InvalidKeyException e2) {
                b.b("GCMKS", "InvalidKeyException : " + e2.getMessage());
                return bArr2;
            } catch (NoSuchAlgorithmException e3) {
                b.b("GCMKS", "NoSuchAlgorithmException : " + e3.getMessage());
                return bArr2;
            } catch (BadPaddingException e4) {
                b.b("GCMKS", "BadPaddingException : " + e4.getMessage());
                return bArr2;
            } catch (IllegalBlockSizeException e5) {
                b.b("GCMKS", "IllegalBlockSizeException : " + e5.getMessage());
                return bArr2;
            } catch (NoSuchPaddingException e6) {
                b.b("GCMKS", "NoSuchPaddingException : " + e6.getMessage());
                return bArr2;
            } catch (Exception e7) {
                b.b("GCMKS", "Exception: " + e7.getMessage());
                return bArr2;
            }
        }
    }

    private static boolean a() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
