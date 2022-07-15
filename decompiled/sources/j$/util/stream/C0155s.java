package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.s */
/* loaded from: classes2.dex */
class C0155s extends AbstractC0065c3 {
    public C0155s(AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i) {
        super(abstractC0061c, enumC0078e4, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    A1 E0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0072d4.DISTINCT.d(abstractC0188y2.s0())) {
            return abstractC0188y2.p0(uVar, false, mVar);
        }
        if (EnumC0072d4.ORDERED.d(abstractC0188y2.s0())) {
            return L0(abstractC0188y2, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new C0128n0(new C0133o(atomicBoolean, concurrentHashMap), false).c(abstractC0188y2, uVar);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    @Override // j$.util.stream.AbstractC0061c
    j$.util.u F0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        return EnumC0072d4.DISTINCT.d(abstractC0188y2.s0()) ? abstractC0188y2.w0(uVar) : EnumC0072d4.ORDERED.d(abstractC0188y2.s0()) ? ((E1) L0(abstractC0188y2, uVar)).mo69spliterator() : new C0126m4(abstractC0188y2.w0(uVar));
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        return EnumC0072d4.DISTINCT.d(i) ? abstractC0125m3 : EnumC0072d4.SORTED.d(i) ? new C0145q(this, abstractC0125m3) : new r(this, abstractC0125m3);
    }

    A1 L0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        C0139p c0139p = C0139p.a;
        C0121m c0121m = C0121m.a;
        return new E1((Collection) new C0193z2(EnumC0078e4.REFERENCE, C0127n.a, c0121m, c0139p).c(abstractC0188y2, uVar));
    }
}
