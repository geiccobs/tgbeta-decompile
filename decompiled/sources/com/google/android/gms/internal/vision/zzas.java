package com.google.android.gms.internal.vision;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class zzas {
    private static UserManager zza;
    private static volatile boolean zzb = !zza();
    private static boolean zzc = false;

    private zzas() {
    }

    public static boolean zza() {
        return Build.VERSION.SDK_INT >= 24;
    }

    public static boolean zza(Context context) {
        return !zza() || zzc(context);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0033, code lost:
        r4 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0042, code lost:
        if (r4 == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0044, code lost:
        com.google.android.gms.internal.vision.zzas.zza = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0046, code lost:
        return r4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static boolean zzb(android.content.Context r6) {
        /*
            r0 = 1
            r1 = 1
        L3:
            r2 = 2
            r3 = 0
            r4 = 0
            if (r1 > r2) goto L42
        L9:
            android.os.UserManager r2 = com.google.android.gms.internal.vision.zzas.zza
            if (r2 != 0) goto L17
            java.lang.Class<android.os.UserManager> r2 = android.os.UserManager.class
            java.lang.Object r2 = r6.getSystemService(r2)
            android.os.UserManager r2 = (android.os.UserManager) r2
            com.google.android.gms.internal.vision.zzas.zza = r2
        L17:
            android.os.UserManager r2 = com.google.android.gms.internal.vision.zzas.zza
            if (r2 != 0) goto L1e
        L1d:
            return r0
        L1e:
            boolean r5 = r2.isUserUnlocked()     // Catch: java.lang.NullPointerException -> L35
            if (r5 != 0) goto L32
            android.os.UserHandle r5 = android.os.Process.myUserHandle()     // Catch: java.lang.NullPointerException -> L35
            boolean r6 = r2.isUserRunning(r5)     // Catch: java.lang.NullPointerException -> L35
            if (r6 != 0) goto L30
            goto L32
        L30:
            r0 = 0
            goto L33
        L32:
        L33:
            r4 = r0
            goto L42
        L35:
            r2 = move-exception
            java.lang.String r4 = "DirectBootUtils"
            java.lang.String r5 = "Failed to check if user is unlocked."
            android.util.Log.w(r4, r5, r2)
            com.google.android.gms.internal.vision.zzas.zza = r3
            int r1 = r1 + 1
            goto L3
        L42:
            if (r4 == 0) goto L46
            com.google.android.gms.internal.vision.zzas.zza = r3
        L46:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzas.zzb(android.content.Context):boolean");
    }

    private static boolean zzc(Context context) {
        if (zzb) {
            return true;
        }
        synchronized (zzas.class) {
            if (zzb) {
                return true;
            }
            boolean zzb2 = zzb(context);
            if (zzb2) {
                zzb = zzb2;
            }
            return zzb2;
        }
    }
}
