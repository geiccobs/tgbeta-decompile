package com.google.android.gms.internal.mlkit_language_id;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzhi extends AbstractList<String> implements zzfg, RandomAccess {
    private final zzfg zza;

    public zzhi(zzfg zzfgVar) {
        this.zza = zzfgVar;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final Object zza(int i) {
        return this.zza.zza(i);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zza.size();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final void zza(zzdn zzdnVar) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractList, java.util.List
    public final ListIterator<String> listIterator(int i) {
        return new zzhl(this, i);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public final Iterator<String> iterator() {
        return new zzhk(this);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final List<?> zzb() {
        return this.zza.zzb();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfg
    public final zzfg a_() {
        return this;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        return (String) this.zza.get(i);
    }
}
