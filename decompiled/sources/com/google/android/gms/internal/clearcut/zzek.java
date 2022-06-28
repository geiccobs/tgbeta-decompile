package com.google.android.gms.internal.clearcut;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
final class zzek implements Iterator<Map.Entry<K, V>>, j$.util.Iterator {
    private int pos;
    private Iterator<Map.Entry<K, V>> zzor;
    private final /* synthetic */ zzei zzos;

    private zzek(zzei zzeiVar) {
        List list;
        this.zzos = zzeiVar;
        list = zzeiVar.zzom;
        this.pos = list.size();
    }

    public /* synthetic */ zzek(zzei zzeiVar, zzej zzejVar) {
        this(zzeiVar);
    }

    private final Iterator<Map.Entry<K, V>> zzdw() {
        Map map;
        if (this.zzor == null) {
            map = this.zzos.zzop;
            this.zzor = map.entrySet().iterator();
        }
        return this.zzor;
    }

    @Override // j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    @Override // java.util.Iterator
    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final boolean hasNext() {
        List list;
        int i = this.pos;
        if (i > 0) {
            list = this.zzos.zzom;
            if (i <= list.size()) {
                return true;
            }
        }
        return zzdw().hasNext();
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final /* synthetic */ Object next() {
        List list;
        Object obj;
        if (zzdw().hasNext()) {
            obj = zzdw().next();
        } else {
            list = this.zzos.zzom;
            int i = this.pos - 1;
            this.pos = i;
            obj = list.get(i);
        }
        return (Map.Entry) obj;
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
