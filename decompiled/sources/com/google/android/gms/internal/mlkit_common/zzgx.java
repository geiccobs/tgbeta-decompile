package com.google.android.gms.internal.mlkit_common;

import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzgx implements zzgu {
    @Override // com.google.android.gms.internal.mlkit_common.zzgu
    public final zzgs<?, ?> zzc(Object obj) {
        zzgt zzgtVar = (zzgt) obj;
        throw new NoSuchMethodError();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgu
    public final Map<?, ?> zza(Object obj) {
        return (zzgv) obj;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgu
    public final Object zzb(Object obj) {
        ((zzgv) obj).zzb();
        return obj;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgu
    public final Object zza(Object obj, Object obj2) {
        zzgv zzgvVar = (zzgv) obj;
        zzgv zzgvVar2 = (zzgv) obj2;
        if (!zzgvVar2.isEmpty()) {
            if (!zzgvVar.zzc()) {
                zzgvVar = zzgvVar.zza();
            }
            zzgvVar.zza(zzgvVar2);
        }
        return zzgvVar;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgu
    public final int zza(int i, Object obj, Object obj2) {
        zzgv zzgvVar = (zzgv) obj;
        zzgt zzgtVar = (zzgt) obj2;
        if (zzgvVar.isEmpty()) {
            return 0;
        }
        Iterator it = zzgvVar.entrySet().iterator();
        if (!it.hasNext()) {
            return 0;
        }
        Map.Entry entry = (Map.Entry) it.next();
        entry.getKey();
        entry.getValue();
        throw new NoSuchMethodError();
    }
}
