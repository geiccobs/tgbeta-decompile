package j$.wrappers;

import j$.util.function.Consumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.e */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0202e implements j$.util.r {
    final /* synthetic */ PrimitiveIterator.OfLong a;

    private /* synthetic */ C0202e(PrimitiveIterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ j$.util.r a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C0204f ? ((C0204f) ofLong).a : new C0202e(ofLong);
    }

    @Override // j$.util.r
    public /* synthetic */ void d(j$.util.function.q qVar) {
        this.a.forEachRemaining(C0207g0.a(qVar));
    }

    @Override // j$.util.r, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(C0232x.a(consumer));
    }

    @Override // j$.util.p
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((PrimitiveIterator.OfLong) obj);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    @Override // j$.util.r
    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
