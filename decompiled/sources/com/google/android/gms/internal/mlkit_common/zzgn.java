package com.google.android.gms.internal.mlkit_common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzgn extends zzgl {
    private static final Class<?> zza = Collections.unmodifiableList(Collections.emptyList()).getClass();

    /* JADX INFO: Access modifiers changed from: private */
    public zzgn() {
        super();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgl
    public final void zza(Object obj, long j) {
        Object obj2;
        List list = (List) zzip.zzf(obj, j);
        if (list instanceof zzgi) {
            obj2 = ((zzgi) list).a_();
        } else if (zza.isAssignableFrom(list.getClass())) {
            return;
        } else {
            if ((list instanceof zzhk) && (list instanceof zzfy)) {
                zzfy zzfyVar = (zzfy) list;
                if (zzfyVar.zza()) {
                    zzfyVar.b_();
                    return;
                }
                return;
            }
            obj2 = Collections.unmodifiableList(list);
        }
        zzip.zza(obj, j, obj2);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgl
    public final <E> void zza(Object obj, Object obj2, long j) {
        List zzb = zzb(obj2, j);
        int size = zzb.size();
        List zzb2 = zzb(obj, j);
        if (zzb2.isEmpty()) {
            if (zzb2 instanceof zzgi) {
                zzb2 = new zzgj(size);
            } else if ((zzb2 instanceof zzhk) && (zzb2 instanceof zzfy)) {
                zzb2 = ((zzfy) zzb2).zzb(size);
            } else {
                zzb2 = new ArrayList(size);
            }
            zzip.zza(obj, j, zzb2);
        } else if (zza.isAssignableFrom(zzb2.getClass())) {
            ArrayList arrayList = new ArrayList(zzb2.size() + size);
            arrayList.addAll(zzb2);
            zzip.zza(obj, j, arrayList);
            zzb2 = arrayList;
        } else if (zzb2 instanceof zzik) {
            List zzgjVar = new zzgj(zzb2.size() + size);
            zzgjVar.addAll((zzik) zzb2);
            zzip.zza(obj, j, zzgjVar);
            zzb2 = zzgjVar;
        } else if ((zzb2 instanceof zzhk) && (zzb2 instanceof zzfy)) {
            zzfy zzfyVar = (zzfy) zzb2;
            if (!zzfyVar.zza()) {
                zzb2 = zzfyVar.zzb(zzb2.size() + size);
                zzip.zza(obj, j, zzb2);
            }
        }
        int size2 = zzb2.size();
        int size3 = zzb.size();
        if (size2 > 0 && size3 > 0) {
            zzb2.addAll(zzb);
        }
        if (size2 > 0) {
            zzb = zzb2;
        }
        zzip.zza(obj, j, zzb);
    }

    private static <E> List<E> zzb(Object obj, long j) {
        return (List) zzip.zzf(obj, j);
    }
}
