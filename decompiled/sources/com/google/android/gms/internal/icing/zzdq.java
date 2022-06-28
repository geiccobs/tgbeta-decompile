package com.google.android.gms.internal.icing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzdq extends zzds {
    private static final Class<?> zza = Collections.unmodifiableList(Collections.emptyList()).getClass();

    private zzdq() {
        super(null);
    }

    public /* synthetic */ zzdq(zzdp zzdpVar) {
        super(null);
    }

    @Override // com.google.android.gms.internal.icing.zzds
    public final void zza(Object obj, long j) {
        Object obj2;
        List list = (List) zzfn.zzn(obj, j);
        if (list instanceof zzdo) {
            obj2 = ((zzdo) list).zzi();
        } else if (zza.isAssignableFrom(list.getClass())) {
            return;
        } else {
            if (!(list instanceof zzel) || !(list instanceof zzdg)) {
                obj2 = Collections.unmodifiableList(list);
            } else {
                zzdg zzdgVar = (zzdg) list;
                if (!zzdgVar.zza()) {
                    return;
                }
                zzdgVar.zzb();
                return;
            }
        }
        zzfn.zzo(obj, j, obj2);
    }

    @Override // com.google.android.gms.internal.icing.zzds
    public final <E> void zzb(Object obj, Object obj2, long j) {
        List list = (List) zzfn.zzn(obj2, j);
        int size = list.size();
        List list2 = (List) zzfn.zzn(obj, j);
        if (list2.isEmpty()) {
            if (list2 instanceof zzdo) {
                list2 = new zzdn(size);
            } else if (!(list2 instanceof zzel) || !(list2 instanceof zzdg)) {
                list2 = new ArrayList(size);
            } else {
                list2 = ((zzdg) list2).zze(size);
            }
            zzfn.zzo(obj, j, list2);
        } else if (zza.isAssignableFrom(list2.getClass())) {
            ArrayList arrayList = new ArrayList(list2.size() + size);
            arrayList.addAll(list2);
            zzfn.zzo(obj, j, arrayList);
            list2 = arrayList;
        } else if (list2 instanceof zzfi) {
            zzdn zzdnVar = new zzdn(list2.size() + size);
            zzdnVar.addAll(zzdnVar.size(), (zzfi) list2);
            zzfn.zzo(obj, j, zzdnVar);
            list2 = zzdnVar;
        } else if ((list2 instanceof zzel) && (list2 instanceof zzdg)) {
            zzdg zzdgVar = (zzdg) list2;
            if (!zzdgVar.zza()) {
                list2 = zzdgVar.zze(list2.size() + size);
                zzfn.zzo(obj, j, list2);
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
        zzfn.zzo(obj, j, list);
    }
}
