package com.microsoft.appcenter.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes3.dex */
public class HashUtils {
    private static final char[] HEXADECIMAL_OUTPUT = "0123456789abcdef".toCharArray();

    HashUtils() {
    }

    public static String sha256(String data) {
        return sha256(data, "UTF-8");
    }

    static String sha256(String data, String charsetName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data.getBytes(charsetName));
            return encodeHex(digest.digest());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeHex(byte[] bytes) {
        char[] output = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            char[] cArr = HEXADECIMAL_OUTPUT;
            output[j * 2] = cArr[v >>> 4];
            output[(j * 2) + 1] = cArr[v & 15];
        }
        return new String(output);
    }
}
