package com.google.android.gms.internal.icing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzdn extends zzbt<String> implements RandomAccess, zzdo {
    public static final zzdo zza;
    private static final zzdn zzb;
    private final List<Object> zzc;

    static {
        zzdn zzdnVar = new zzdn(10);
        zzb = zzdnVar;
        zzdnVar.zzb();
        zza = zzdnVar;
    }

    public zzdn() {
        this(10);
    }

    private static String zzj(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof zzcf) {
            return ((zzcf) obj).zzk(zzdh.zza);
        }
        return zzdh.zzd((byte[]) obj);
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ void add(int i, Object obj) {
        zzc();
        this.zzc.add(i, (String) obj);
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractList, java.util.List
    public final boolean addAll(int i, Collection<? extends String> collection) {
        zzc();
        if (collection instanceof zzdo) {
            collection = ((zzdo) collection).zzh();
        }
        boolean addAll = this.zzc.addAll(i, collection);
        this.modCount++;
        return addAll;
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final void clear() {
        zzc();
        this.zzc.clear();
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object remove(int i) {
        zzc();
        Object remove = this.zzc.remove(i);
        this.modCount++;
        return zzj(remove);
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractList, java.util.List
    public final /* bridge */ /* synthetic */ Object set(int i, Object obj) {
        zzc();
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
        if (obj instanceof zzcf) {
            zzcf zzcfVar = (zzcf) obj;
            String zzk = zzcfVar.zzk(zzdh.zza);
            if (zzcfVar.zzh()) {
                this.zzc.set(i, zzk);
            }
            return zzk;
        }
        byte[] bArr = (byte[]) obj;
        String zzd = zzdh.zzd(bArr);
        if (zzdh.zzc(bArr)) {
            this.zzc.set(i, zzd);
        }
        return zzd;
    }

    @Override // com.google.android.gms.internal.icing.zzdg
    public final /* bridge */ /* synthetic */ zzdg zze(int i) {
        if (i < size()) {
            throw new IllegalArgumentException();
        }
        ArrayList arrayList = new ArrayList(i);
        arrayList.addAll(this.zzc);
        return new zzdn(arrayList);
    }

    @Override // com.google.android.gms.internal.icing.zzdo
    public final void zzf(zzcf zzcfVar) {
        zzc();
        this.zzc.add(zzcfVar);
        this.modCount++;
    }

    @Override // com.google.android.gms.internal.icing.zzdo
    public final Object zzg(int i) {
        return this.zzc.get(i);
    }

    @Override // com.google.android.gms.internal.icing.zzdo
    public final List<?> zzh() {
        return Collections.unmodifiableList(this.zzc);
    }

    @Override // com.google.android.gms.internal.icing.zzdo
    public final zzdo zzi() {
        return zza() ? new zzfi(this) : this;
    }

    public zzdn(int i) {
        this.zzc = new ArrayList(i);
    }

    private zzdn(ArrayList<Object> arrayList) {
        this.zzc = arrayList;
    }

    @Override // com.google.android.gms.internal.icing.zzbt, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends String> collection) {
        return addAll(size(), collection);
    }
}
