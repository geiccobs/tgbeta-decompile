package com.google.android.gms.internal.clearcut;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Iterator;
/* loaded from: classes.dex */
final class zzfc implements Iterator<String>, j$.util.Iterator {
    private final /* synthetic */ zzfa zzpe;
    private Iterator<String> zzpf;

    public zzfc(zzfa zzfaVar) {
        zzcx zzcxVar;
        this.zzpe = zzfaVar;
        zzcxVar = zzfaVar.zzpb;
        this.zzpf = zzcxVar.iterator();
    }

    @Override // j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zzpf.hasNext();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final /* synthetic */ Object next() {
        return this.zzpf.next();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
