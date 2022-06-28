package com.google.android.gms.internal.wearable;

import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzcs {
    public static final int zza(int i, Object obj, Object obj2) {
        zzcr zzcrVar = (zzcr) obj;
        zzcq zzcqVar = (zzcq) obj2;
        if (zzcrVar.isEmpty()) {
            return 0;
        }
        Iterator it = zzcrVar.entrySet().iterator();
        if (!it.hasNext()) {
            return 0;
        }
        Map.Entry entry = (Map.Entry) it.next();
        entry.getKey();
        entry.getValue();
        throw null;
    }

    public static final Object zzb(Object obj, Object obj2) {
        zzcr zzcrVar = (zzcr) obj;
        zzcr zzcrVar2 = (zzcr) obj2;
        if (!zzcrVar2.isEmpty()) {
            if (!zzcrVar.zze()) {
                zzcrVar = zzcrVar.zzc();
            }
            zzcrVar.zzb(zzcrVar2);
        }
        return zzcrVar;
    }
}
