package com.google.android.gms.internal.location;

import j$.util.Iterator;
import j$.util.function.Consumer;
import j$.wrappers.C$r8$wrapper$java$util$function$Consumer$VWRP;
import java.util.Iterator;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
public abstract class zzbu<E> implements Iterator<E>, j$.util.Iterator {
    @Override // j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Iterator.CC.$default$forEachRemaining(this, consumer);
    }

    @Override // java.util.Iterator
    public /* synthetic */ void forEachRemaining(java.util.function.Consumer consumer) {
        forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    @Override // java.util.Iterator, j$.util.Iterator
    @Deprecated
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}