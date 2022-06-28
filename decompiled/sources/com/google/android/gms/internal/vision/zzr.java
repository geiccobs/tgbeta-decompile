package com.google.android.gms.internal.vision;

import com.google.android.gms.vision.L;
import java.util.HashMap;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzr {
    private static final Object zza = new Object();
    private static final HashMap<String, Integer> zzb = new HashMap<>();

    public static boolean zza(String str, String str2) {
        synchronized (zza) {
            String valueOf = String.valueOf(str);
            String valueOf2 = String.valueOf(str2);
            String concat = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
            HashMap<String, Integer> hashMap = zzb;
            int intValue = hashMap.containsKey(concat) ? hashMap.get(concat).intValue() : 0;
            if ((intValue & 1) != 0) {
                return true;
            }
            try {
                System.loadLibrary(str2);
                hashMap.put(concat, Integer.valueOf(intValue | 1));
                return true;
            } catch (UnsatisfiedLinkError e) {
                if ((intValue & 4) == 0) {
                    L.e(e, "System.loadLibrary failed: %s", str2);
                    zzb.put(concat, Integer.valueOf(intValue | 4));
                }
                return false;
            }
        }
    }
}
