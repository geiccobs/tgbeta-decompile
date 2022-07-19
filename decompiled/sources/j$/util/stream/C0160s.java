package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* renamed from: j$.util.stream.s */
/* loaded from: classes2.dex */
class C0160s extends AbstractC0070c3 {
    public C0160s(AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i) {
        super(abstractC0066c, enumC0083e4, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    A1 E0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0077d4.DISTINCT.d(abstractC0193y2.s0())) {
            return abstractC0193y2.p0(uVar, false, mVar);
        }
        if (EnumC0077d4.ORDERED.d(abstractC0193y2.s0())) {
            return L0(abstractC0193y2, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new C0133n0(new C0138o(atomicBoolean, concurrentHashMap), false).c(abstractC0193y2, uVar);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    @Override // j$.util.stream.AbstractC0066c
    j$.util.u F0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        return EnumC0077d4.DISTINCT.d(abstractC0193y2.s0()) ? abstractC0193y2.w0(uVar) : EnumC0077d4.ORDERED.d(abstractC0193y2.s0()) ? ((E1) L0(abstractC0193y2, uVar)).mo69spliterator() : new C0131m4(abstractC0193y2.w0(uVar));
    }

    @Override // j$.util.stream.AbstractC0066c
    public AbstractC0130m3 H0(int i, AbstractC0130m3 abstractC0130m3) {
        abstractC0130m3.getClass();
        return EnumC0077d4.DISTINCT.d(i) ? abstractC0130m3 : EnumC0077d4.SORTED.d(i) ? new C0150q(this, abstractC0130m3) : new r(this, abstractC0130m3);
    }

    A1 L0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        C0144p c0144p = C0144p.a;
        C0126m c0126m = C0126m.a;
        return new E1((Collection) new C0198z2(EnumC0083e4.REFERENCE, C0132n.a, c0126m, c0144p).c(abstractC0193y2, uVar));
    }
}
