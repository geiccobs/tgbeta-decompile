package com.google.android.gms.internal.wearable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzbg {
    private static volatile zzbg zzc;
    private final Map<zzbf, zzbr<?, ?>> zzd;
    private static volatile boolean zzb = false;
    static final zzbg zza = new zzbg(true);

    zzbg() {
        this.zzd = new HashMap();
    }

    public static zzbg zza() {
        zzbg zzbgVar = zzc;
        if (zzbgVar == null) {
            synchronized (zzbg.class) {
                zzbgVar = zzc;
                if (zzbgVar == null) {
                    zzbgVar = zza;
                    zzc = zzbgVar;
                }
            }
        }
        return zzbgVar;
    }

    public final <ContainingType extends zzcx> zzbr<ContainingType, ?> zzb(ContainingType containingtype, int i) {
        return (zzbr<ContainingType, ?>) this.zzd.get(new zzbf(containingtype, i));
    }

    zzbg(boolean z) {
        this.zzd = Collections.emptyMap();
    }
}
