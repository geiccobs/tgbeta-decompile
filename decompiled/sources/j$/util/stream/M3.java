package j$.util.stream;

import j$.util.Comparator$CC;
import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class M3 extends AbstractC0070c3 {
    private final boolean l;
    private final Comparator m;

    public M3(AbstractC0066c abstractC0066c) {
        super(abstractC0066c, EnumC0083e4.REFERENCE, EnumC0077d4.q | EnumC0077d4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    public M3(AbstractC0066c abstractC0066c, Comparator comparator) {
        super(abstractC0066c, EnumC0083e4.REFERENCE, EnumC0077d4.q | EnumC0077d4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    @Override // j$.util.stream.AbstractC0066c
    public A1 E0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (!EnumC0077d4.SORTED.d(abstractC0193y2.s0()) || !this.l) {
            Object[] q = abstractC0193y2.p0(uVar, true, mVar).q(mVar);
            Arrays.sort(q, this.m);
            return new D1(q);
        }
        return abstractC0193y2.p0(uVar, false, mVar);
    }

    @Override // j$.util.stream.AbstractC0066c
    public AbstractC0130m3 H0(int i, AbstractC0130m3 abstractC0130m3) {
        abstractC0130m3.getClass();
        return (!EnumC0077d4.SORTED.d(i) || !this.l) ? EnumC0077d4.SIZED.d(i) ? new R3(abstractC0130m3, this.m) : new N3(abstractC0130m3, this.m) : abstractC0130m3;
    }
}
