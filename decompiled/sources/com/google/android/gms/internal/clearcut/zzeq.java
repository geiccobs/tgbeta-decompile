package com.google.android.gms.internal.clearcut;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes3.dex */
final class zzeq implements Iterator<Map.Entry<K, V>>, j$.util.Iterator {
    private int pos;
    private Iterator<Map.Entry<K, V>> zzor;
    private final /* synthetic */ zzei zzos;
    private boolean zzow;

    private zzeq(zzei zzeiVar) {
        this.zzos = zzeiVar;
        this.pos = -1;
    }

    public /* synthetic */ zzeq(zzei zzeiVar, zzej zzejVar) {
        this(zzeiVar);
    }

    private final Iterator<Map.Entry<K, V>> zzdw() {
        Map map;
        if (this.zzor == null) {
            map = this.zzos.zzon;
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
        Map map;
        int i = this.pos + 1;
        list = this.zzos.zzom;
        if (i >= list.size()) {
            map = this.zzos.zzon;
            if (map.isEmpty() || !zzdw().hasNext()) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final /* synthetic */ Object next() {
        List list;
        Object next;
        List list2;
        this.zzow = true;
        int i = this.pos + 1;
        this.pos = i;
        list = this.zzos.zzom;
        if (i < list.size()) {
            list2 = this.zzos.zzom;
            next = list2.get(this.pos);
        } else {
            next = zzdw().next();
        }
        return (Map.Entry) next;
    }

    @Override // java.util.Iterator, j$.util.Iterator
    public final void remove() {
        List list;
        if (this.zzow) {
            this.zzow = false;
            this.zzos.zzdu();
            int i = this.pos;
            list = this.zzos.zzom;
            if (i >= list.size()) {
                zzdw().remove();
                return;
            }
            zzei zzeiVar = this.zzos;
            int i2 = this.pos;
            this.pos = i2 - 1;
            zzeiVar.zzal(i2);
            return;
        }
        throw new IllegalStateException("remove() was called before next()");
    }
}
