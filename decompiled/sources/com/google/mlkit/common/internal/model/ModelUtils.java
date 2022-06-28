package com.google.mlkit.common.internal.model;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.internal.mlkit_common.zzx;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class ModelUtils {
    private static final GmsLogger zza = new GmsLogger("ModelUtils", "");

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static abstract class ModelLoggingInfo {
        public abstract String getHash();

        public abstract long getSize();

        static ModelLoggingInfo zza(long j, String str) {
            return new AutoValue_ModelUtils_ModelLoggingInfo(j, zzx.zza(str));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x00c9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.mlkit.common.internal.model.ModelUtils.ModelLoggingInfo getModelLoggingInfo(android.content.Context r11, com.google.mlkit.common.model.LocalModel r12) {
        /*
            Method dump skipped, instructions count: 231
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.mlkit.common.internal.model.ModelUtils.getModelLoggingInfo(android.content.Context, com.google.mlkit.common.model.LocalModel):com.google.mlkit.common.internal.model.ModelUtils$ModelLoggingInfo");
    }

    public static boolean zza(File file, String str) {
        String sha256 = getSHA256(file);
        GmsLogger gmsLogger = zza;
        String valueOf = String.valueOf(sha256);
        gmsLogger.d("ModelUtils", valueOf.length() != 0 ? "Calculated hash value is: ".concat(valueOf) : new String("Calculated hash value is: "));
        return str.equals(sha256);
    }

    public static String getSHA256(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String zza2 = zza(fileInputStream);
            fileInputStream.close();
            return zza2;
        } catch (IOException e) {
            GmsLogger gmsLogger = zza;
            String valueOf = String.valueOf(e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 44);
            sb.append("Failed to create FileInputStream for model: ");
            sb.append(valueOf);
            gmsLogger.e("ModelUtils", sb.toString());
            return null;
        }
    }

    private static String zza(InputStream inputStream) {
        int i;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bArr = new byte[1048576];
            while (true) {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                messageDigest.update(bArr, 0, read);
            }
            byte[] digest = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hexString = Integer.toHexString(b & 255);
                if (hexString.length() == 1) {
                    sb.append('0');
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (IOException e) {
            zza.e("ModelUtils", "Failed to read model file");
            return null;
        } catch (NoSuchAlgorithmException e2) {
            zza.e("ModelUtils", "Do not have SHA-256 algorithm");
            return null;
        }
    }

    private ModelUtils() {
    }
}
