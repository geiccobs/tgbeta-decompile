package com.google.android.gms.internal.mlkit_language_id;

import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzfv implements zzfs {
    @Override // com.google.android.gms.internal.mlkit_language_id.zzfs
    public final zzfq<?, ?> zzc(Object obj) {
        zzfr zzfrVar = (zzfr) obj;
        throw new NoSuchMethodError();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfs
    public final Map<?, ?> zza(Object obj) {
        return (zzft) obj;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfs
    public final Object zzb(Object obj) {
        ((zzft) obj).zzb();
        return obj;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfs
    public final Object zza(Object obj, Object obj2) {
        zzft zzftVar = (zzft) obj;
        zzft zzftVar2 = (zzft) obj2;
        if (!zzftVar2.isEmpty()) {
            if (!zzftVar.zzc()) {
                zzftVar = zzftVar.zza();
            }
            zzftVar.zza(zzftVar2);
        }
        return zzftVar;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfs
    public final int zza(int i, Object obj, Object obj2) {
        zzft zzftVar = (zzft) obj;
        zzfr zzfrVar = (zzfr) obj2;
        if (zzftVar.isEmpty()) {
            return 0;
        }
        Iterator it = zzftVar.entrySet().iterator();
        if (!it.hasNext()) {
            return 0;
        }
        Map.Entry entry = (Map.Entry) it.next();
        entry.getKey();
        entry.getValue();
        throw new NoSuchMethodError();
    }
}
