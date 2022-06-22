package com.google.android.gms.internal.mlkit_language_id;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzfh extends zzdi<String> implements zzfg, RandomAccess {
    private static final zzfh zza;
    private final List<Object> zzc;

    public zzfh() {
        this(10);
    }

    public zzfh(int i) {
        this(new ArrayList(i));
    }

    private zzfh(ArrayList<Object> arrayList) {
        this.zzc = arrayList;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zzc.size();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdi, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends String> collection) {
        return addAll(size(), collection);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdi, java.util.AbstractList, java.util.List
    public final boolean addAll(int i, Collection<? extends String> collection) {
        zzc();
        if (collection instanceof zzfg) {
            collection = ((zzfg) collection).zzb();
        }
        boolean addAll = this.zzc.addAll(i, collection);
        ((AbstractList) this).modCount++;
        return addAll;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzdi, java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final void clear() {
        zzc();
        this.zzc.clear();
        ((AbstractList) this).modCount++;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final void zza(zzdn zzdnVar) {
        zzc();
        this.zzc.add(zzdnVar);
        ((AbstractList) this).modCount++;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final Object zza(int i) {
        return this.zzc.get(i);
    }

    private static String zza(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof zzdn) {
            return ((zzdn) obj).zzb();
        }
        return zzeq.zzb((byte[]) obj);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final List<?> zzb() {
        return Collections.unmodifiableList(this.zzc);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final zzfg a_() {
        return zza() ? new zzhi(this) : this;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object set(int i, Object obj) {
        zzc();
        return zza(this.zzc.set(i, (String) obj));
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object remove(int i) {
        zzc();
        Object remove = this.zzc.remove(i);
        ((AbstractList) this).modCount++;
        return zza(remove);
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ void add(int i, Object obj) {
        zzc();
        this.zzc.add(i, (String) obj);
        ((AbstractList) this).modCount++;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzew
    public final /* synthetic */ zzew zzb(int i) {
        if (i < size()) {
            throw new IllegalArgumentException();
        }
        ArrayList arrayList = new ArrayList(i);
        arrayList.addAll(this.zzc);
        return new zzfh(arrayList);
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        Object obj = this.zzc.get(i);
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof zzdn) {
            zzdn zzdnVar = (zzdn) obj;
            String zzb = zzdnVar.zzb();
            if (zzdnVar.zzc()) {
                this.zzc.set(i, zzb);
            }
            return zzb;
        }
        byte[] bArr = (byte[]) obj;
        String zzb2 = zzeq.zzb(bArr);
        if (zzeq.zza(bArr)) {
            this.zzc.set(i, zzb2);
        }
        return zzb2;
    }

    static {
        zzfh zzfhVar = new zzfh();
        zza = zzfhVar;
        zzfhVar.b_();
    }
}
