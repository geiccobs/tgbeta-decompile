package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.s */
/* loaded from: classes2.dex */
class C0159s extends AbstractC0069c3 {
    public C0159s(AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i) {
        super(abstractC0065c, enumC0082e4, i);
    }

    @Override // j$.util.stream.AbstractC0065c
    A1 E0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0076d4.DISTINCT.d(abstractC0192y2.s0())) {
            return abstractC0192y2.p0(uVar, false, mVar);
        }
        if (EnumC0076d4.ORDERED.d(abstractC0192y2.s0())) {
            return L0(abstractC0192y2, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new C0132n0(new C0137o(atomicBoolean, concurrentHashMap), false).c(abstractC0192y2, uVar);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    @Override // j$.util.stream.AbstractC0065c
    j$.util.u F0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        return EnumC0076d4.DISTINCT.d(abstractC0192y2.s0()) ? abstractC0192y2.w0(uVar) : EnumC0076d4.ORDERED.d(abstractC0192y2.s0()) ? ((E1) L0(abstractC0192y2, uVar)).mo69spliterator() : new C0130m4(abstractC0192y2.w0(uVar));
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        return EnumC0076d4.DISTINCT.d(i) ? abstractC0129m3 : EnumC0076d4.SORTED.d(i) ? new C0149q(this, abstractC0129m3) : new r(this, abstractC0129m3);
    }

    A1 L0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        C0143p c0143p = C0143p.a;
        C0125m c0125m = C0125m.a;
        return new E1((Collection) new C0197z2(EnumC0082e4.REFERENCE, C0131n.a, c0125m, c0143p).c(abstractC0192y2, uVar));
    }
}
