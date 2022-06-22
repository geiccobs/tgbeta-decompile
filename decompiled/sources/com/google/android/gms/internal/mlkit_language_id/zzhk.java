package com.google.android.gms.internal.mlkit_language_id;

import j$.util.Iterator;
import j$.util.function.Consumer;
import java.util.Iterator;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzhk implements Iterator<String>, j$.util.Iterator {
    private Iterator<String> zza;
    private final /* synthetic */ zzhi zzb;

    public zzhk(zzhi zzhiVar) {
        zzfg zzfgVar;
        this.zzb = zzhiVar;
        zzfgVar = zzhiVar.zza;
        this.zza = zzfgVar.iterator();
    }

    @Override // j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        return this.zza.hasNext();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final /* synthetic */ Object next() {
        return this.zza.next();
    }
}
