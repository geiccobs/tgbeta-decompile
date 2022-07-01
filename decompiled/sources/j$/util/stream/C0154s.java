package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.s */
/* loaded from: classes2.dex */
class C0154s extends AbstractC0064c3 {
    public C0154s(AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i) {
        super(abstractC0060c, enumC0077e4, i);
    }

    @Override // j$.util.stream.AbstractC0060c
    A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0071d4.DISTINCT.d(abstractC0187y2.s0())) {
            return abstractC0187y2.p0(uVar, false, mVar);
        }
        if (EnumC0071d4.ORDERED.d(abstractC0187y2.s0())) {
            return L0(abstractC0187y2, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new C0127n0(new C0132o(atomicBoolean, concurrentHashMap), false).c(abstractC0187y2, uVar);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    @Override // j$.util.stream.AbstractC0060c
    j$.util.u F0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        return EnumC0071d4.DISTINCT.d(abstractC0187y2.s0()) ? abstractC0187y2.w0(uVar) : EnumC0071d4.ORDERED.d(abstractC0187y2.s0()) ? ((E1) L0(abstractC0187y2, uVar)).mo69spliterator() : new C0125m4(abstractC0187y2.w0(uVar));
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        abstractC0124m3.getClass();
        return EnumC0071d4.DISTINCT.d(i) ? abstractC0124m3 : EnumC0071d4.SORTED.d(i) ? new C0144q(this, abstractC0124m3) : new r(this, abstractC0124m3);
    }

    A1 L0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        C0138p c0138p = C0138p.a;
        C0120m c0120m = C0120m.a;
        return new E1((Collection) new C0192z2(EnumC0077e4.REFERENCE, C0126n.a, c0120m, c0138p).c(abstractC0187y2, uVar));
    }
}
