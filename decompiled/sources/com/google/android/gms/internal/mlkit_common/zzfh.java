package com.google.android.gms.internal.mlkit_common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class zzfh {
    private static volatile zzfh zzc;
    private final Map<Object, Object> zze;
    private static volatile boolean zza = false;
    private static boolean zzb = true;
    private static final zzfh zzd = new zzfh(true);

    public static zzfh zza() {
        zzfh zzfhVar = zzc;
        if (zzfhVar == null) {
            synchronized (zzfh.class) {
                zzfhVar = zzc;
                if (zzfhVar == null) {
                    zzfhVar = zzd;
                    zzc = zzfhVar;
                }
            }
        }
        return zzfhVar;
    }

    zzfh() {
        this.zze = new HashMap();
    }

    private zzfh(boolean z) {
        this.zze = Collections.emptyMap();
    }
}
