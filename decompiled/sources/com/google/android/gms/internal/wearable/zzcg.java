package com.google.android.gms.internal.wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzcg extends zzag<String> implements RandomAccess, zzch {
    public static final zzch zza;
    private static final zzcg zzb;
    private final List<Object> zzc;

    static {
        zzcg zzcgVar = new zzcg(10);
        zzb = zzcgVar;
        zzcgVar.zzb();
        zza = zzcgVar;
    }

    public zzcg() {
        this(10);
    }

    private static String zzj(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof zzau) {
            return ((zzau) obj).zzo(zzca.zza);
        }
        return zzca.zzd((byte[]) obj);
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ void add(int i, Object obj) {
        zzad();
        this.zzc.add(i, (String) obj);
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractList, java.util.List
    public final boolean addAll(int i, Collection<? extends String> collection) {
        zzad();
        if (collection instanceof zzch) {
            collection = ((zzch) collection).zzh();
        }
        boolean addAll = this.zzc.addAll(i, collection);
        this.modCount++;
        return addAll;
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final void clear() {
        zzad();
        this.zzc.clear();
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object remove(int i) {
        zzad();
        Object remove = this.zzc.remove(i);
        this.modCount++;
        return zzj(remove);
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object set(int i, Object obj) {
        zzad();
        return zzj(this.zzc.set(i, (String) obj));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zzc.size();
    }

    /* renamed from: zzd */
    public final String get(int i) {
        Object obj = this.zzc.get(i);
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof zzau) {
            zzau zzauVar = (zzau) obj;
            String zzo = zzauVar.zzo(zzca.zza);
            if (zzauVar.zzi()) {
                this.zzc.set(i, zzo);
            }
            return zzo;
        }
        byte[] bArr = (byte[]) obj;
        String zzd = zzca.zzd(bArr);
        if (zzca.zzc(bArr)) {
            this.zzc.set(i, zzd);
        }
        return zzd;
    }

    @Override // com.google.android.gms.internal.wearable.zzbz
    public final /* bridge */ /* synthetic */ zzbz zze(int i) {
        if (i < size()) {
            throw new IllegalArgumentException();
        }
        ArrayList arrayList = new ArrayList(i);
        arrayList.addAll(this.zzc);
        return new zzcg(arrayList);
    }

    @Override // com.google.android.gms.internal.wearable.zzch
    public final void zzf(zzau zzauVar) {
        zzad();
        this.zzc.add(zzauVar);
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.wearable.zzch
    public final Object zzg(int i) {
        return this.zzc.get(i);
    }

    @Override // com.google.android.gms.internal.wearable.zzch
    public final List<?> zzh() {
        return Collections.unmodifiableList(this.zzc);
    }

    @Override // com.google.android.gms.internal.wearable.zzch
    public final zzch zzi() {
        return zza() ? new zzeb(this) : this;
    }

    public zzcg(int i) {
        this.zzc = new ArrayList(i);
    }

    private zzcg(ArrayList<Object> arrayList) {
        this.zzc = arrayList;
    }

    @Override // com.google.android.gms.internal.wearable.zzag, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends String> collection) {
        return addAll(size(), collection);
    }
}
