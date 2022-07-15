package j$.wrappers;

import j$.util.AbstractC0048n;
import j$.util.function.Consumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.a */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0195a implements AbstractC0048n {
    final /* synthetic */ PrimitiveIterator.OfDouble a;

    private /* synthetic */ C0195a(PrimitiveIterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ AbstractC0048n a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof C0197b ? ((C0197b) ofDouble).a : new C0195a(ofDouble);
    }

    @Override // j$.util.AbstractC0048n
    public /* synthetic */ void e(j$.util.function.f fVar) {
        this.a.forEachRemaining(B.a(fVar));
    }

    @Override // j$.util.AbstractC0048n, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(C0233x.a(consumer));
    }

    @Override // j$.util.p
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((PrimitiveIterator.OfDouble) obj);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // j$.util.AbstractC0048n
    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
