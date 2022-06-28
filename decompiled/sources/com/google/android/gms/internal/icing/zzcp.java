package com.google.android.gms.internal.icing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzcp {
    private static volatile zzcp zzc;
    private final Map zzd;
    private static volatile boolean zzb = false;
    static final zzcp zza = new zzcp(true);

    zzcp() {
        this.zzd = new HashMap();
    }

    public static zzcp zza() {
        zzcp zzcpVar = zzc;
        if (zzcpVar == null) {
            synchronized (zzcp.class) {
                zzcpVar = zzc;
                if (zzcpVar == null) {
                    zzcpVar = zza;
                    zzc = zzcpVar;
                }
            }
        }
        return zzcpVar;
    }

    zzcp(boolean z) {
        this.zzd = Collections.emptyMap();
    }
}
