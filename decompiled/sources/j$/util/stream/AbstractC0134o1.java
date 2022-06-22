package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.u;
import j$.wrappers.C0213j0;
/* renamed from: j$.util.stream.o1 */
/* loaded from: classes2.dex */
public abstract /* synthetic */ class AbstractC0134o1 {
    public static void a(AbstractC0106j3 abstractC0106j3, Double d) {
        if (!Q4.a) {
            abstractC0106j3.accept(d.doubleValue());
        } else {
            Q4.a(abstractC0106j3.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(AbstractC0112k3 abstractC0112k3, Integer num) {
        if (!Q4.a) {
            abstractC0112k3.accept(num.intValue());
        } else {
            Q4.a(abstractC0112k3.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(AbstractC0118l3 abstractC0118l3, Long l) {
        if (!Q4.a) {
            abstractC0118l3.accept(l.longValue());
        } else {
            Q4.a(abstractC0118l3.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(AbstractC0124m3 abstractC0124m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(AbstractC0124m3 abstractC0124m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(AbstractC0124m3 abstractC0124m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(AbstractC0191z1 abstractC0191z1, j$.util.function.m mVar) {
        if (Q4.a) {
            Q4.a(abstractC0191z1.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (abstractC0191z1.count() >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        } else {
            Object[] objArr = (Object[]) mVar.apply((int) abstractC0191z1.count());
            abstractC0191z1.i(objArr, 0);
            return objArr;
        }
    }

    public static void h(AbstractC0166u1 abstractC0166u1, Double[] dArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0166u1.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
            throw null;
        }
        double[] dArr2 = (double[]) abstractC0166u1.e();
        for (int i2 = 0; i2 < dArr2.length; i2++) {
            dArr[i + i2] = Double.valueOf(dArr2[i2]);
        }
    }

    public static void i(AbstractC0176w1 abstractC0176w1, Integer[] numArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0176w1.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            throw null;
        }
        int[] iArr = (int[]) abstractC0176w1.e();
        for (int i2 = 0; i2 < iArr.length; i2++) {
            numArr[i + i2] = Integer.valueOf(iArr[i2]);
        }
    }

    public static void j(AbstractC0186y1 abstractC0186y1, Long[] lArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0186y1.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
            throw null;
        }
        long[] jArr = (long[]) abstractC0186y1.e();
        for (int i2 = 0; i2 < jArr.length; i2++) {
            lArr[i + i2] = Long.valueOf(jArr[i2]);
        }
    }

    public static void k(AbstractC0166u1 abstractC0166u1, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            abstractC0166u1.g((j$.util.function.f) consumer);
        } else if (!Q4.a) {
            ((j$.util.t) abstractC0166u1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0166u1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(AbstractC0176w1 abstractC0176w1, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            abstractC0176w1.g((j$.util.function.l) consumer);
        } else if (!Q4.a) {
            ((u.a) abstractC0176w1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0176w1.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(AbstractC0186y1 abstractC0186y1, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            abstractC0186y1.g((j$.util.function.q) consumer);
        } else if (!Q4.a) {
            ((j$.util.v) abstractC0186y1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0186y1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static AbstractC0166u1 n(AbstractC0166u1 abstractC0166u1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0166u1.count()) {
            return abstractC0166u1;
        }
        long j3 = j2 - j;
        j$.util.t tVar = (j$.util.t) abstractC0166u1.mo69spliterator();
        AbstractC0140p1 j4 = AbstractC0182x2.j(j3);
        j4.n(j3);
        for (int i = 0; i < j && tVar.k(C0161t1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && tVar.k(j4); i2++) {
        }
        j4.m();
        return j4.mo70a();
    }

    public static AbstractC0176w1 o(AbstractC0176w1 abstractC0176w1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0176w1.count()) {
            return abstractC0176w1;
        }
        long j3 = j2 - j;
        u.a aVar = (u.a) abstractC0176w1.mo69spliterator();
        AbstractC0146q1 p = AbstractC0182x2.p(j3);
        p.n(j3);
        for (int i = 0; i < j && aVar.g(C0171v1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && aVar.g(p); i2++) {
        }
        p.m();
        return p.mo70a();
    }

    public static AbstractC0186y1 p(AbstractC0186y1 abstractC0186y1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0186y1.count()) {
            return abstractC0186y1;
        }
        long j3 = j2 - j;
        j$.util.v vVar = (j$.util.v) abstractC0186y1.mo69spliterator();
        AbstractC0151r1 q = AbstractC0182x2.q(j3);
        q.n(j3);
        for (int i = 0; i < j && vVar.i(C0181x1.a); i++) {
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
        AbstractC0156s1 d = AbstractC0182x2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; i < j && mo69spliterator.b(C0128n1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && mo69spliterator.b(d); i2++) {
        }
        d.m();
        return d.mo70a();
    }

    public static U r(j$.util.t tVar, boolean z) {
        return new P(tVar, EnumC0071d4.c(tVar), z);
    }

    public static IntStream s(u.a aVar, boolean z) {
        return new I0(aVar, EnumC0071d4.c(aVar), z);
    }

    public static AbstractC0074e1 t(j$.util.v vVar, boolean z) {
        return new C0050a1(vVar, EnumC0071d4.c(vVar), z);
    }

    public static N4 u(j$.wrappers.E e, EnumC0110k1 enumC0110k1) {
        e.getClass();
        enumC0110k1.getClass();
        return new C0116l1(EnumC0077e4.DOUBLE_VALUE, enumC0110k1, new C0132o(enumC0110k1, e));
    }

    public static N4 v(j$.wrappers.V v, EnumC0110k1 enumC0110k1) {
        v.getClass();
        enumC0110k1.getClass();
        return new C0116l1(EnumC0077e4.INT_VALUE, enumC0110k1, new C0132o(enumC0110k1, v));
    }

    public static N4 w(C0213j0 c0213j0, EnumC0110k1 enumC0110k1) {
        c0213j0.getClass();
        enumC0110k1.getClass();
        return new C0116l1(EnumC0077e4.LONG_VALUE, enumC0110k1, new C0132o(enumC0110k1, c0213j0));
    }

    public static N4 x(Predicate predicate, EnumC0110k1 enumC0110k1) {
        predicate.getClass();
        enumC0110k1.getClass();
        return new C0116l1(EnumC0077e4.REFERENCE, enumC0110k1, new C0132o(enumC0110k1, predicate));
    }

    public static Stream y(j$.util.u uVar, boolean z) {
        uVar.getClass();
        return new C0058b3(uVar, EnumC0071d4.c(uVar), z);
    }
}
