package j$.util.stream;

import j$.util.Optional;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.C0039a;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.Iterator;
/* renamed from: j$.util.stream.e3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0076e3 extends AbstractC0060c implements Stream {
    public AbstractC0076e3(AbstractC0060c abstractC0060c, int i) {
        super(abstractC0060c, i);
    }

    public AbstractC0076e3(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractC0060c
    final void A0(j$.util.u uVar, AbstractC0124m3 abstractC0124m3) {
        while (!abstractC0124m3.o() && uVar.b(abstractC0124m3)) {
        }
    }

    @Override // j$.util.stream.Stream
    public final Object B(Object obj, BiFunction biFunction, j$.util.function.b bVar) {
        biFunction.getClass();
        bVar.getClass();
        return x0(new C0192z2(EnumC0077e4.REFERENCE, bVar, biFunction, obj));
    }

    @Override // j$.util.stream.AbstractC0060c
    public final EnumC0077e4 B0() {
        return EnumC0077e4.REFERENCE;
    }

    @Override // j$.util.stream.Stream
    public final U E(Function function) {
        function.getClass();
        return new K(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, function);
    }

    @Override // j$.util.stream.AbstractC0060c
    final j$.util.u K0(AbstractC0187y2 abstractC0187y2, j$.util.function.y yVar, boolean z) {
        return new L4(abstractC0187y2, yVar, z);
    }

    @Override // j$.util.stream.Stream
    public final Stream T(Predicate predicate) {
        predicate.getClass();
        return new L(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.t, predicate);
    }

    @Override // j$.util.stream.Stream
    public final Stream V(Consumer consumer) {
        consumer.getClass();
        return new L(this, this, EnumC0077e4.REFERENCE, 0, consumer);
    }

    @Override // j$.util.stream.Stream
    public final boolean W(Predicate predicate) {
        return ((Boolean) x0(AbstractC0134o1.x(predicate, EnumC0110k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final AbstractC0074e1 X(Function function) {
        function.getClass();
        return new N(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, function);
    }

    @Override // j$.util.stream.Stream
    public final boolean a(Predicate predicate) {
        return ((Boolean) x0(AbstractC0134o1.x(predicate, EnumC0110k1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final Object b0(j$.wrappers.J0 j0) {
        Object obj;
        if (!isParallel() || !j0.b().contains(EnumC0090h.CONCURRENT) || (C0() && !j0.b().contains(EnumC0090h.UNORDERED))) {
            j0.getClass();
            j$.util.function.y f = j0.f();
            obj = x0(new I2(EnumC0077e4.REFERENCE, j0.c(), j0.a(), f, j0));
        } else {
            obj = j0.f().get();
            forEach(new C0132o(j0.a(), obj));
        }
        return j0.b().contains(EnumC0090h.IDENTITY_FINISH) ? obj : j0.e().apply(obj);
    }

    @Override // j$.util.stream.Stream
    public final IntStream c(Function function) {
        function.getClass();
        return new M(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, function);
    }

    @Override // j$.util.stream.Stream
    public final long count() {
        return ((AbstractC0068d1) g0(X2.a)).sum();
    }

    @Override // j$.util.stream.Stream
    public final boolean d0(Predicate predicate) {
        return ((Boolean) x0(AbstractC0134o1.x(predicate, EnumC0110k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.Stream
    public final Stream distinct() {
        return new C0154s(this, EnumC0077e4.REFERENCE, EnumC0071d4.m | EnumC0071d4.t);
    }

    public void e(Consumer consumer) {
        consumer.getClass();
        x0(new C0127n0(consumer, true));
    }

    @Override // j$.util.stream.Stream
    public final Optional findAny() {
        return (Optional) x0(new C0067d0(false, EnumC0077e4.REFERENCE, Optional.empty(), V.a, C0061c0.a));
    }

    @Override // j$.util.stream.Stream
    public final Optional findFirst() {
        return (Optional) x0(new C0067d0(true, EnumC0077e4.REFERENCE, Optional.empty(), V.a, C0061c0.a));
    }

    public void forEach(Consumer consumer) {
        consumer.getClass();
        x0(new C0127n0(consumer, false));
    }

    @Override // j$.util.stream.Stream
    public final AbstractC0074e1 g0(j$.util.function.A a) {
        a.getClass();
        return new N(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n, a);
    }

    @Override // j$.util.stream.Stream
    public final Object i(j$.util.function.y yVar, BiConsumer biConsumer, BiConsumer biConsumer2) {
        yVar.getClass();
        biConsumer.getClass();
        biConsumer2.getClass();
        return x0(new C0192z2(EnumC0077e4.REFERENCE, biConsumer2, biConsumer, yVar));
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public final Iterator mo66iterator() {
        return j$.util.L.i(spliterator());
    }

    @Override // j$.util.stream.Stream
    public final U j0(j$.util.function.z zVar) {
        zVar.getClass();
        return new K(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n, zVar);
    }

    @Override // j$.util.stream.Stream
    public final Object[] l(j$.util.function.m mVar) {
        return AbstractC0182x2.l(y0(mVar), mVar).q(mVar);
    }

    @Override // j$.util.stream.Stream
    public final Stream limit(long j) {
        if (j >= 0) {
            return B3.i(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.Stream
    public final IntStream m(ToIntFunction toIntFunction) {
        toIntFunction.getClass();
        return new M(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n, toIntFunction);
    }

    @Override // j$.util.stream.Stream
    public final Object m0(Object obj, j$.util.function.b bVar) {
        bVar.getClass();
        return x0(new C0192z2(EnumC0077e4.REFERENCE, bVar, bVar, obj));
    }

    @Override // j$.util.stream.Stream
    public final Optional max(Comparator comparator) {
        comparator.getClass();
        return t(new C0039a(comparator, 0));
    }

    @Override // j$.util.stream.Stream
    public final Optional min(Comparator comparator) {
        comparator.getClass();
        return t(new C0039a(comparator, 1));
    }

    @Override // j$.util.stream.Stream
    public final Stream n(Function function) {
        function.getClass();
        return new C0052a3(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n, function, 0);
    }

    @Override // j$.util.stream.Stream
    public final Stream o(Function function) {
        function.getClass();
        return new C0052a3(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, function, 1);
    }

    @Override // j$.util.stream.Stream
    public final Stream skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.i(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted() {
        return new M3(this);
    }

    @Override // j$.util.stream.Stream
    public final Optional t(j$.util.function.b bVar) {
        bVar.getClass();
        return (Optional) x0(new D2(EnumC0077e4.REFERENCE, bVar));
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final AbstractC0156s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0182x2.d(j, mVar);
    }

    @Override // j$.util.stream.Stream
    public final Object[] toArray() {
        W2 w2 = W2.a;
        return AbstractC0182x2.l(y0(w2), w2).q(w2);
    }

    @Override // j$.util.stream.AbstractC0084g
    public AbstractC0084g unordered() {
        return !C0() ? this : new Z2(this, this, EnumC0077e4.REFERENCE, EnumC0071d4.r);
    }

    @Override // j$.util.stream.AbstractC0060c
    final A1 z0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0182x2.e(abstractC0187y2, uVar, z, mVar);
    }

    @Override // j$.util.stream.Stream
    public final Stream sorted(Comparator comparator) {
        return new M3(this, comparator);
    }
}
