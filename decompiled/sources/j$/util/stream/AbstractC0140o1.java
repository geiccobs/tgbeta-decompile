package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.u;
import j$.wrappers.C0219j0;
/* renamed from: j$.util.stream.o1 */
/* loaded from: classes2.dex */
public abstract /* synthetic */ class AbstractC0140o1 {
    public static void a(AbstractC0112j3 abstractC0112j3, Double d) {
        if (!Q4.a) {
            abstractC0112j3.accept(d.doubleValue());
        } else {
            Q4.a(abstractC0112j3.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            throw null;
        }
    }

    public static void b(AbstractC0118k3 abstractC0118k3, Integer num) {
        if (!Q4.a) {
            abstractC0118k3.accept(num.intValue());
        } else {
            Q4.a(abstractC0118k3.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            throw null;
        }
    }

    public static void c(AbstractC0124l3 abstractC0124l3, Long l) {
        if (!Q4.a) {
            abstractC0124l3.accept(l.longValue());
        } else {
            Q4.a(abstractC0124l3.getClass(), "{0} calling Sink.OfLong.accept(Long)");
            throw null;
        }
    }

    public static void d(AbstractC0130m3 abstractC0130m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void e(AbstractC0130m3 abstractC0130m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static void f(AbstractC0130m3 abstractC0130m3) {
        throw new IllegalStateException("called wrong accept method");
    }

    public static Object[] g(AbstractC0197z1 abstractC0197z1, j$.util.function.m mVar) {
        if (Q4.a) {
            Q4.a(abstractC0197z1.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (abstractC0197z1.count() >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        } else {
            Object[] objArr = (Object[]) mVar.apply((int) abstractC0197z1.count());
            abstractC0197z1.i(objArr, 0);
            return objArr;
        }
    }

    public static void h(AbstractC0172u1 abstractC0172u1, Double[] dArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0172u1.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
            throw null;
        }
        double[] dArr2 = (double[]) abstractC0172u1.e();
        for (int i2 = 0; i2 < dArr2.length; i2++) {
            dArr[i + i2] = Double.valueOf(dArr2[i2]);
        }
    }

    public static void i(AbstractC0182w1 abstractC0182w1, Integer[] numArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0182w1.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            throw null;
        }
        int[] iArr = (int[]) abstractC0182w1.e();
        for (int i2 = 0; i2 < iArr.length; i2++) {
            numArr[i + i2] = Integer.valueOf(iArr[i2]);
        }
    }

    public static void j(AbstractC0192y1 abstractC0192y1, Long[] lArr, int i) {
        if (Q4.a) {
            Q4.a(abstractC0192y1.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
            throw null;
        }
        long[] jArr = (long[]) abstractC0192y1.e();
        for (int i2 = 0; i2 < jArr.length; i2++) {
            lArr[i + i2] = Long.valueOf(jArr[i2]);
        }
    }

    public static void k(AbstractC0172u1 abstractC0172u1, Consumer consumer) {
        if (consumer instanceof j$.util.function.f) {
            abstractC0172u1.g((j$.util.function.f) consumer);
        } else if (!Q4.a) {
            ((j$.util.t) abstractC0172u1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0172u1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void l(AbstractC0182w1 abstractC0182w1, Consumer consumer) {
        if (consumer instanceof j$.util.function.l) {
            abstractC0182w1.g((j$.util.function.l) consumer);
        } else if (!Q4.a) {
            ((u.a) abstractC0182w1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0182w1.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void m(AbstractC0192y1 abstractC0192y1, Consumer consumer) {
        if (consumer instanceof j$.util.function.q) {
            abstractC0192y1.g((j$.util.function.q) consumer);
        } else if (!Q4.a) {
            ((j$.util.v) abstractC0192y1.mo69spliterator()).forEachRemaining(consumer);
        } else {
            Q4.a(abstractC0192y1.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static AbstractC0172u1 n(AbstractC0172u1 abstractC0172u1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0172u1.count()) {
            return abstractC0172u1;
        }
        long j3 = j2 - j;
        j$.util.t tVar = (j$.util.t) abstractC0172u1.mo69spliterator();
        AbstractC0146p1 j4 = AbstractC0188x2.j(j3);
        j4.n(j3);
        for (int i = 0; i < j && tVar.k(C0167t1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && tVar.k(j4); i2++) {
        }
        j4.m();
        return j4.mo70a();
    }

    public static AbstractC0182w1 o(AbstractC0182w1 abstractC0182w1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0182w1.count()) {
            return abstractC0182w1;
        }
        long j3 = j2 - j;
        u.a aVar = (u.a) abstractC0182w1.mo69spliterator();
        AbstractC0152q1 p = AbstractC0188x2.p(j3);
        p.n(j3);
        for (int i = 0; i < j && aVar.g(C0177v1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && aVar.g(p); i2++) {
        }
        p.m();
        return p.mo70a();
    }

    public static AbstractC0192y1 p(AbstractC0192y1 abstractC0192y1, long j, long j2, j$.util.function.m mVar) {
        if (j == 0 && j2 == abstractC0192y1.count()) {
            return abstractC0192y1;
        }
        long j3 = j2 - j;
        j$.util.v vVar = (j$.util.v) abstractC0192y1.mo69spliterator();
        AbstractC0157r1 q = AbstractC0188x2.q(j3);
        q.n(j3);
        for (int i = 0; i < j && vVar.i(C0187x1.a); i++) {
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
        AbstractC0162s1 d = AbstractC0188x2.d(j3, mVar);
        d.n(j3);
        for (int i = 0; i < j && mo69spliterator.b(C0134n1.a); i++) {
        }
        for (int i2 = 0; i2 < j3 && mo69spliterator.b(d); i2++) {
        }
        d.m();
        return d.mo70a();
    }

    public static U r(j$.util.t tVar, boolean z) {
        return new P(tVar, EnumC0077d4.c(tVar), z);
    }

    public static IntStream s(u.a aVar, boolean z) {
        return new I0(aVar, EnumC0077d4.c(aVar), z);
    }

    public static AbstractC0080e1 t(j$.util.v vVar, boolean z) {
        return new C0056a1(vVar, EnumC0077d4.c(vVar), z);
    }

    public static N4 u(j$.wrappers.E e, EnumC0116k1 enumC0116k1) {
        e.getClass();
        enumC0116k1.getClass();
        return new C0122l1(EnumC0083e4.DOUBLE_VALUE, enumC0116k1, new C0138o(enumC0116k1, e));
    }

    public static N4 v(j$.wrappers.V v, EnumC0116k1 enumC0116k1) {
        v.getClass();
        enumC0116k1.getClass();
        return new C0122l1(EnumC0083e4.INT_VALUE, enumC0116k1, new C0138o(enumC0116k1, v));
    }

    public static N4 w(C0219j0 c0219j0, EnumC0116k1 enumC0116k1) {
        c0219j0.getClass();
        enumC0116k1.getClass();
        return new C0122l1(EnumC0083e4.LONG_VALUE, enumC0116k1, new C0138o(enumC0116k1, c0219j0));
    }

    public static N4 x(Predicate predicate, EnumC0116k1 enumC0116k1) {
        predicate.getClass();
        enumC0116k1.getClass();
        return new C0122l1(EnumC0083e4.REFERENCE, enumC0116k1, new C0138o(enumC0116k1, predicate));
    }

    public static Stream y(j$.util.u uVar, boolean z) {
        uVar.getClass();
        return new C0064b3(uVar, EnumC0077d4.c(uVar), z);
    }
}
