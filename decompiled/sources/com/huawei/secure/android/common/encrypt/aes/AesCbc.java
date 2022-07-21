package com.huawei.secure.android.common.encrypt.aes;

import android.text.TextUtils;
import com.huawei.secure.android.common.encrypt.utils.EncryptUtil;
import com.huawei.secure.android.common.encrypt.utils.HexUtil;
import com.huawei.secure.android.common.encrypt.utils.b;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes.dex */
public final class AesCbc {
    private static byte[] a(String str, byte[] bArr, byte[] bArr2) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "encrypt 5 content is null");
            return new byte[0];
        } else if (bArr == null) {
            b.b("CBC", "encrypt 5 key is null");
            return new byte[0];
        } else if (bArr.length < 16) {
            b.b("CBC", "encrypt 5 key lengh is not right");
            return new byte[0];
        } else if (bArr2 == null) {
            b.b("CBC", "encrypt 5 iv is null");
            return new byte[0];
        } else if (bArr2.length < 16) {
            b.b("CBC", "encrypt 5 iv lengh is not right");
            return new byte[0];
        } else {
            try {
                return encrypt(str.getBytes("UTF-8"), bArr, bArr2);
            } catch (UnsupportedEncodingException e) {
                b.b("CBC", " cbc encrypt data error" + e.getMessage());
                return new byte[0];
            }
        }
    }

    public static String decrypt(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "decrypt 1 content is null");
            return "";
        } else if (TextUtils.isEmpty(str2)) {
            b.b("CBC", "decrypt 1 key is null");
            return "";
        } else {
            byte[] hexStr2ByteArray = HexUtil.hexStr2ByteArray(str2);
            if (hexStr2ByteArray.length < 16) {
                b.b("CBC", "decrypt 1 key length is not right");
                return "";
            }
            return decrypt(str, hexStr2ByteArray);
        }
    }

    public static String encrypt(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "encrypt 1 content is null");
            return "";
        } else if (TextUtils.isEmpty(str2)) {
            b.b("CBC", "encrypt 1 key is null");
            return "";
        } else {
            byte[] hexStr2ByteArray = HexUtil.hexStr2ByteArray(str2);
            if (hexStr2ByteArray.length < 16) {
                b.b("CBC", "encrypt 1 key length is not right");
                return "";
            }
            return encrypt(str, hexStr2ByteArray);
        }
    }

    private static String b(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return str.substring(6, 12) + str.substring(16, 26) + str.substring(32, 48);
        } catch (Exception e) {
            b.b("CBC", "getIv exception : " + e.getMessage());
            return "";
        }
    }

    public static String decrypt(String str, byte[] bArr) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "decrypt 2 content is null");
            return "";
        } else if (bArr == null) {
            b.b("CBC", "decrypt 2 key is null");
            return "";
        } else if (bArr.length < 16) {
            b.b("CBC", "decrypt 2 key lengh is not right");
            return "";
        } else {
            String b = b(str);
            String a = a(str);
            if (TextUtils.isEmpty(b)) {
                b.b("CBC", "decrypt 2 iv is null");
                return "";
            } else if (TextUtils.isEmpty(a)) {
                b.b("CBC", "decrypt 2 encrypt content is null");
                return "";
            } else {
                return decrypt(a, bArr, HexUtil.hexStr2ByteArray(b));
            }
        }
    }

    public static String encrypt(String str, byte[] bArr) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "encrypt 2 content is null");
            return "";
        } else if (bArr == null) {
            b.b("CBC", "encrypt 2 key is null");
            return "";
        } else if (bArr.length < 16) {
            b.b("CBC", "encrypt 2 key lengh is not right");
            return "";
        } else {
            byte[] generateSecureRandom = EncryptUtil.generateSecureRandom(16);
            byte[] a = a(str, bArr, generateSecureRandom);
            return (a == null || a.length == 0) ? "" : a(HexUtil.byteArray2HexStr(generateSecureRandom), HexUtil.byteArray2HexStr(a));
        }
    }

    private static String a(String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                return str2.substring(0, 6) + str.substring(0, 6) + str2.substring(6, 10) + str.substring(6, 16) + str2.substring(10, 16) + str.substring(16) + str2.substring(16);
            } catch (Exception e) {
                b.b("CBC", "mix exception: " + e.getMessage());
            }
        }
        return "";
    }

    private static byte[] a(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    private static String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return str.substring(0, 6) + str.substring(12, 16) + str.substring(26, 32) + str.substring(48);
        } catch (Exception e) {
            b.b("CBC", "get encryptword exception : " + e.getMessage());
            return "";
        }
    }

    public static String decrypt(String str, byte[] bArr, byte[] bArr2) {
        if (TextUtils.isEmpty(str)) {
            b.b("CBC", "decrypt 4 content is null");
            return "";
        } else if (bArr == null) {
            b.b("CBC", "decrypt 4 key is null");
            return "";
        } else if (bArr.length < 16) {
            b.b("CBC", "decrypt 4 key lengh is not right");
            return "";
        } else if (bArr2 == null) {
            b.b("CBC", "decrypt 4 iv is null");
            return "";
        } else if (bArr2.length < 16) {
            b.b("CBC", "decrypt 4 iv lengh is not right");
            return "";
        } else {
            try {
                return new String(decrypt(HexUtil.hexStr2ByteArray(str), bArr, bArr2), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                b.b("CBC", " cbc decrypt data error" + e.getMessage());
                return "";
            }
        }
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null) {
            b.b("CBC", "encrypt 6 content is null");
            return new byte[0];
        } else if (bArr.length == 0) {
            b.b("CBC", "encrypt 6 content length is 0");
            return new byte[0];
        } else if (bArr2 == null) {
            b.b("CBC", "encrypt 6 key is null");
            return new byte[0];
        } else if (bArr2.length < 16) {
            b.b("CBC", "encrypt 6 key length is error");
            return new byte[0];
        } else if (bArr3 == null) {
            b.b("CBC", "encrypt 6 iv is null");
            return new byte[0];
        } else if (bArr3.length < 16) {
            b.b("CBC", "encrypt 6 iv length is error");
            return new byte[0];
        } else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(1, secretKeySpec, new IvParameterSpec(bArr3));
                return cipher.doFinal(bArr);
            } catch (InvalidAlgorithmParameterException e) {
                b.b("CBC", "InvalidAlgorithmParameterException: " + e.getMessage());
                return new byte[0];
            } catch (InvalidKeyException e2) {
                b.b("CBC", "InvalidKeyException: " + e2.getMessage());
                return new byte[0];
            } catch (NoSuchAlgorithmException e3) {
                b.b("CBC", "NoSuchAlgorithmException: " + e3.getMessage());
                return new byte[0];
            } catch (BadPaddingException e4) {
                b.b("CBC", "BadPaddingException: " + e4.getMessage());
                return new byte[0];
            } catch (IllegalBlockSizeException e5) {
                b.b("CBC", "IllegalBlockSizeException: " + e5.getMessage());
                return new byte[0];
            } catch (NoSuchPaddingException e6) {
                b.b("CBC", "NoSuchPaddingException: " + e6.getMessage());
                return new byte[0];
            }
        }
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (bArr == null) {
            b.b("CBC", "decrypt 6 content is null");
            return new byte[0];
        } else if (bArr.length == 0) {
            b.b("CBC", "decrypt 6 content length is 0");
            return new byte[0];
        } else if (bArr2 == null) {
            b.b("CBC", "decrypt 6 key is null");
            return new byte[0];
        } else if (bArr2.length < 16) {
            b.b("CBC", "decrypt 6 key length is error");
            return new byte[0];
        } else if (bArr3 == null) {
            b.b("CBC", "decrypt 6 iv is null");
            return new byte[0];
        } else if (bArr3.length < 16) {
            b.b("CBC", "decrypt 6 iv length is error");
            return new byte[0];
        } else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(2, secretKeySpec, new IvParameterSpec(bArr3));
                return cipher.doFinal(bArr);
            } catch (InvalidAlgorithmParameterException e) {
                b.b("CBC", "InvalidAlgorithmParameterException: " + e.getMessage());
                return new byte[0];
            } catch (InvalidKeyException e2) {
                b.b("CBC", "InvalidKeyException: " + e2.getMessage());
                return new byte[0];
            } catch (NoSuchAlgorithmException e3) {
                b.b("CBC", "NoSuchAlgorithmException: " + e3.getMessage());
                return new byte[0];
            } catch (BadPaddingException e4) {
                b.b("CBC", "BadPaddingException: " + e4.getMessage());
                b.b("CBC", "key is not right");
                return new byte[0];
            } catch (IllegalBlockSizeException e5) {
                b.b("CBC", "IllegalBlockSizeException: " + e5.getMessage());
                return new byte[0];
            } catch (NoSuchPaddingException e6) {
                b.b("CBC", "NoSuchPaddingException: " + e6.getMessage());
                return new byte[0];
            }
        }
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2) {
        byte[] generateSecureRandom = EncryptUtil.generateSecureRandom(16);
        return a(generateSecureRandom, encrypt(bArr, bArr2, generateSecureRandom));
    }
}
