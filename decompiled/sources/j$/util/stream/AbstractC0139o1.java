package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.u;
import j$.wrappers.C0218j0;
/* renamed from: j$.util.stream.o1 */
/* loaded from: classes2.dex */
public abstract /* synthetic */ class AbstractC0139o1 {
    public static void a(AbstractC0111j3 abstractC0111j3, Double d) {
        if (!Q4.a) {
            abstractC0111j3.accept(d.doubleValue());
        } else {
            Q4.a(abstractC0111j3.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(AbstractC0117k3 abstractC0117k3, Integer num) {
        if (!Q4.a) {
            abstractC0117k3.accept(num.intValue());
        } else {
            Q4.a(abstractC0117k3.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(AbstractC0123l3 abstractC0123l3, Long l) {
        if (!Q4.a) {
            abstractC0123l3.accept(l.longValue());
        } else {
            Q4.a(abstractC0123l3.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(AbstractC0129m3 abstractC0129m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(AbstractC0129m3 abstractC0129m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(AbstractC0129m3 abstractC0129m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(AbstractC0196z1 abstractC0196z1, j$.util.function.m mVar) {
        if (Q4.a) {
            Q4.a(abstractC0196z1.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (abstractC0196z1.count() >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        } else {
            Object[] objArr = (Object[]) mVar.apply((int) abstractC0196z1.count());
            abstractC0196z1.i(objArr, 0);
            return objArr;
        }
    }

    public static void h(AbstractC0171u1 abstractC0171u1, Double[] dArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0171u1.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
            throw null;
        }
        double[] dArr2 = (double[]) abstractC0171u1.e();
        for (int i2 = 0; i2 < dArr2.length; i2++) {
            dArr[i + i2] = Double.valueOf(dArr2[i2]);
        }
    }

    public static void i(AbstractC0181w1 abstractC0181w1, Integer[] numArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0181w1.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            throw null;
        }
        int[] iArr = (int[]) abstractC0181w1.e();
        for (int i2 = 0; i2 < iArr.length; i2++) {
            numArr[i + i2] = Integer.valueOf(iArr[i2]);
        }
    }

    public static void j(AbstractC0191y1 abstractC0191y1, Long[] lArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0191y1.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
            throw null;
        }
        long[] jArr = (long[]) abstractC0191y1.e();
        for (int i2 = 0; i2 < jArr.length; i2++) {
            lArr[i + i2] = Long.valueOf(jArr[i2]);
        }
    }

    public static void k(AbstractC0171u1 abstractC0171u1, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            abstractC0171u1.g((j$.util.function.f) consumer);
        } else if (!Q4.a) {
            ((j$.util.t) abstractC0171u1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0171u1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(AbstractC0181w1 abstractC0181w1, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            abstractC0181w1.g((j$.util.function.l) consumer);
        } else if (!Q4.a) {
            ((u.a) abstractC0181w1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0181w1.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(AbstractC0191y1 abstractC0191y1, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            abstractC0191y1.g((j$.util.function.q) consumer);
        } else if (!Q4.a) {
            ((j$.util.v) abstractC0191y1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0191y1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static AbstractC0171u1 n(AbstractC0171u1 abstractC0171u1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0171u1.count()) {
            return abstractC0171u1;
        }
        long j3 = j2 - j;
        j$.util.t tVar = (j$.util.t) abstractC0171u1.mo69spliterator();
        AbstractC0145p1 j4 = AbstractC0187x2.j(j3);
        j4.n(j3);
        for (int i = 0; i < j && tVar.k(C0166t1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && tVar.k(j4); i2++) {
        }
        j4.m();
        return j4.mo70a();
    }

    public static AbstractC0181w1 o(AbstractC0181w1 abstractC0181w1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0181w1.count()) {
            return abstractC0181w1;
        }
        long j3 = j2 - j;
        u.a aVar = (u.a) abstractC0181w1.mo69spliterator();
        AbstractC0151q1 p = AbstractC0187x2.p(j3);
        p.n(j3);
        for (int i = 0; i < j && aVar.g(C0176v1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && aVar.g(p); i2++) {
        }
        p.m();
        return p.mo70a();
    }

    public static AbstractC0191y1 p(AbstractC0191y1 abstractC0191y1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0191y1.count()) {
            return abstractC0191y1;
        }
        long j3 = j2 - j;
        j$.util.v vVar = (j$.util.v) abstractC0191y1.mo69spliterator();
        AbstractC0156r1 q = AbstractC0187x2.q(j3);
        q.n(j3);
        for (int i = 0; i < j && vVar.i(C0186x1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && vVar.i(q); i2++) {
        }
        q.m();
        return q.mo70a();
    }

    public static A1 q(A1 a1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == a1.count()) {
            return a1;
        }
        j$.util.u mo69spliterator = a1.mo69spliterator();
        long j3 = j2 - j;
        AbstractC0161s1 d = AbstractC0187x2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; i < j && mo69spliterator.b(C0133n1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && mo69spliterator.b(d); i2++) {
        }
        d.m();
        return d.mo70a();
    }

    public static U r(j$.util.t tVar, boolean z) {
        return new P(tVar, EnumC0076d4.c(tVar), z);
    }

    public static IntStream s(u.a aVar, boolean z) {
        return new I0(aVar, EnumC0076d4.c(aVar), z);
    }

    public static AbstractC0079e1 t(j$.util.v vVar, boolean z) {
        return new C0055a1(vVar, EnumC0076d4.c(vVar), z);
    }

    public static N4 u(j$.wrappers.E e, EnumC0115k1 enumC0115k1) {
        e.getClass();
        enumC0115k1.getClass();
        return new C0121l1(EnumC0082e4.DOUBLE_VALUE, enumC0115k1, new C0137o(enumC0115k1, e));
    }

    public static N4 v(j$.wrappers.V v, EnumC0115k1 enumC0115k1) {
        v.getClass();
        enumC0115k1.getClass();
        return new C0121l1(EnumC0082e4.INT_VALUE, enumC0115k1, new C0137o(enumC0115k1, v));
    }

    public static N4 w(C0218j0 c0218j0, EnumC0115k1 enumC0115k1) {
        c0218j0.getClass();
        enumC0115k1.getClass();
        return new C0121l1(EnumC0082e4.LONG_VALUE, enumC0115k1, new C0137o(enumC0115k1, c0218j0));
    }

    public static N4 x(Predicate predicate, EnumC0115k1 enumC0115k1) {
        predicate.getClass();
        enumC0115k1.getClass();
        return new C0121l1(EnumC0082e4.REFERENCE, enumC0115k1, new C0137o(enumC0115k1, predicate));
    }

    public static Stream y(j$.util.u uVar, boolean z) {
        uVar.getClass();
        return new C0063b3(uVar, EnumC0076d4.c(uVar), z);
    }
}
