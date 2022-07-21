package com.huawei.hms.utils;

import com.huawei.hms.support.log.HMSLog;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* loaded from: classes.dex */
public abstract class SHA256 {
    public static byte[] digest(byte[] bArr) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bArr);
        } catch (NoSuchAlgorithmException e) {
            HMSLog.e("SHA256", "NoSuchAlgorithmException" + e.getMessage());
            return new byte[0];
        }
    }

    public static byte[] digest(File file) {
        Throwable th;
        MessageDigest messageDigest;
        BufferedInputStream bufferedInputStream;
        int i;
        BufferedInputStream bufferedInputStream2 = null;
        try {
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
                bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException | NoSuchAlgorithmException unused) {
        }
        try {
            byte[] bArr = new byte[4096];
            i = 0;
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                messageDigest.update(bArr, 0, read);
                i += read;
            }
        } catch (IOException | NoSuchAlgorithmException unused2) {
            bufferedInputStream2 = bufferedInputStream;
            HMSLog.e("SHA256", "An exception occurred while computing file 'SHA-256'.");
            IOUtils.closeQuietly((InputStream) bufferedInputStream2);
            bufferedInputStream2 = bufferedInputStream2;
            return new byte[0];
        } catch (Throwable th3) {
            th = th3;
            bufferedInputStream2 = bufferedInputStream;
            IOUtils.closeQuietly((InputStream) bufferedInputStream2);
            throw th;
        }
        if (i > 0) {
            byte[] digest = messageDigest.digest();
            IOUtils.closeQuietly((InputStream) bufferedInputStream);
            return digest;
        }
        IOUtils.closeQuietly((InputStream) bufferedInputStream);
        bufferedInputStream2 = i;
        return new byte[0];
    }
}
