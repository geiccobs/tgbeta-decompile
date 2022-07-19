package j$.wrappers;

import j$.util.AbstractC0053n;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
/* renamed from: j$.wrappers.b */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0202b implements PrimitiveIterator.OfDouble {
    final /* synthetic */ AbstractC0053n a;

    private /* synthetic */ C0202b(AbstractC0053n abstractC0053n) {
        this.a = abstractC0053n;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(AbstractC0053n abstractC0053n) {
        if (abstractC0053n == null) {
            return null;
        }
        return abstractC0053n instanceof C0200a ? ((C0200a) abstractC0053n).a : new C0202b(abstractC0053n);
    }

    @Override // java.util.PrimitiveIterator
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.forEachRemaining(doubleConsumer);
    }

    @Override // java.util.PrimitiveIterator.OfDouble, java.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(C0237w.b(consumer));
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(DoubleConsumer doubleConsumer) {
        this.a.e(A.b(doubleConsumer));
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // java.util.PrimitiveIterator.OfDouble
    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
