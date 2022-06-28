package com.google.android.gms.internal.mlkit_common;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzik extends AbstractList<String> implements zzgi, RandomAccess {
    private final zzgi zza;

    public zzik(zzgi zzgiVar) {
        this.zza = zzgiVar;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgi
    public final Object zza(int i) {
        return this.zza.zza(i);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.zza.size();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgi
    public final void zza(zzep zzepVar) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractList, java.util.List
    public final ListIterator<String> listIterator(int i) {
        return new zzin(this, i);
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public final Iterator<String> iterator() {
        return new zzim(this);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgi
    public final List<?> zzb() {
        return this.zza.zzb();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgi
    public final zzgi a_() {
        return this;
    }

    @Override // java.util.AbstractList, java.util.List
    public final /* synthetic */ Object get(int i) {
        return (String) this.zza.get(i);
    }
}
