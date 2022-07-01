package com.google.android.gms.internal.clearcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
final class zzda extends zzcy {
    private static final Class<?> zzlv = Collections.unmodifiableList(Collections.emptyList()).getClass();

    /* JADX INFO: Access modifiers changed from: private */
    public zzda() {
        super();
    }

    private static <E> List<E> zzb(Object obj, long j) {
        return (List) zzfd.zzo(obj, j);
    }

    @Override // com.google.android.gms.internal.clearcut.zzcy
    public final void zza(Object obj, long j) {
        Object obj2;
        List list = (List) zzfd.zzo(obj, j);
        if (list instanceof zzcx) {
            obj2 = ((zzcx) list).zzbu();
        } else if (zzlv.isAssignableFrom(list.getClass())) {
            return;
        } else {
            obj2 = Collections.unmodifiableList(list);
        }
        zzfd.zza(obj, j, obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.clearcut.zzcy
    public final <E> void zza(Object obj, Object obj2, long j) {
        zzcw zzcwVar;
        List zzb = zzb(obj2, j);
        int size = zzb.size();
        List zzb2 = zzb(obj, j);
        if (zzb2.isEmpty()) {
            zzb2 = zzb2 instanceof zzcx ? new zzcw(size) : new ArrayList(size);
            zzfd.zza(obj, j, zzb2);
        } else {
            if (zzlv.isAssignableFrom(zzb2.getClass())) {
                ArrayList arrayList = new ArrayList(zzb2.size() + size);
                arrayList.addAll(zzb2);
                zzcwVar = arrayList;
            } else if (zzb2 instanceof zzfa) {
                zzcw zzcwVar2 = new zzcw(zzb2.size() + size);
                zzcwVar2.addAll((zzfa) zzb2);
                zzcwVar = zzcwVar2;
            }
            zzfd.zza(obj, j, zzcwVar);
            zzb2 = zzcwVar;
        }
        int size2 = zzb2.size();
        int size3 = zzb.size();
        if (size2 > 0 && size3 > 0) {
            zzb2.addAll(zzb);
        }
        if (size2 > 0) {
            zzb = zzb2;
        }
        zzfd.zza(obj, j, zzb);
    }
}
