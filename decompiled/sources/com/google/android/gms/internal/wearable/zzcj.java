package com.google.android.gms.internal.wearable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzcj extends zzcl {
    private static final Class<?> zza = Collections.unmodifiableList(Collections.emptyList()).getClass();

    private zzcj() {
        super(null);
    }

    public /* synthetic */ zzcj(zzci zzciVar) {
        super(null);
    }

    @Override // com.google.android.gms.internal.wearable.zzcl
    public final void zza(Object obj, long j) {
        Object obj2;
        List list = (List) zzeg.zzn(obj, j);
        if (list instanceof zzch) {
            obj2 = ((zzch) list).zzi();
        } else if (zza.isAssignableFrom(list.getClass())) {
            return;
        } else {
            if (!(list instanceof zzde) || !(list instanceof zzbz)) {
                obj2 = Collections.unmodifiableList(list);
            } else {
                zzbz zzbzVar = (zzbz) list;
                if (!zzbzVar.zza()) {
                    return;
                }
                zzbzVar.zzb();
                return;
            }
        }
        zzeg.zzo(obj, j, obj2);
    }

    @Override // com.google.android.gms.internal.wearable.zzcl
    public final <E> void zzb(Object obj, Object obj2, long j) {
        List list = (List) zzeg.zzn(obj2, j);
        int size = list.size();
        List list2 = (List) zzeg.zzn(obj, j);
        if (list2.isEmpty()) {
            if (list2 instanceof zzch) {
                list2 = new zzcg(size);
            } else if (!(list2 instanceof zzde) || !(list2 instanceof zzbz)) {
                list2 = new ArrayList(size);
            } else {
                list2 = ((zzbz) list2).zze(size);
            }
            zzeg.zzo(obj, j, list2);
        } else if (zza.isAssignableFrom(list2.getClass())) {
            ArrayList arrayList = new ArrayList(list2.size() + size);
            arrayList.addAll(list2);
            zzeg.zzo(obj, j, arrayList);
            list2 = arrayList;
        } else if (list2 instanceof zzeb) {
            zzcg zzcgVar = new zzcg(list2.size() + size);
            zzcgVar.addAll(zzcgVar.size(), (zzeb) list2);
            zzeg.zzo(obj, j, zzcgVar);
            list2 = zzcgVar;
        } else if ((list2 instanceof zzde) && (list2 instanceof zzbz)) {
            zzbz zzbzVar = (zzbz) list2;
            if (!zzbzVar.zza()) {
                list2 = zzbzVar.zze(list2.size() + size);
                zzeg.zzo(obj, j, list2);
            }
        }
        int size2 = list2.size();
        int size3 = list.size();
        if (size2 > 0 && size3 > 0) {
            list2.addAll(list);
        }
        if (size2 > 0) {
            list = list2;
        }
        zzeg.zzo(obj, j, list);
    }
}
