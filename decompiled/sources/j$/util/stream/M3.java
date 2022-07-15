package j$.util.stream;

import j$.util.Comparator$CC;
import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class M3 extends AbstractC0065c3 {
    private final boolean l;
    private final Comparator m;

    public M3(AbstractC0061c abstractC0061c) {
        super(abstractC0061c, EnumC0078e4.REFERENCE, EnumC0072d4.q | EnumC0072d4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    public M3(AbstractC0061c abstractC0061c, Comparator comparator) {
        super(abstractC0061c, EnumC0078e4.REFERENCE, EnumC0072d4.q | EnumC0072d4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    @Override // j$.util.stream.AbstractC0061c
    public A1 E0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (!EnumC0072d4.SORTED.d(abstractC0188y2.s0()) || !this.l) {
            Object[] q = abstractC0188y2.p0(uVar, true, mVar).q(mVar);
            Arrays.sort(q, this.m);
            return new D1(q);
        }
        return abstractC0188y2.p0(uVar, false, mVar);
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        return (!EnumC0072d4.SORTED.d(i) || !this.l) ? EnumC0072d4.SIZED.d(i) ? new R3(abstractC0125m3, this.m) : new N3(abstractC0125m3, this.m) : abstractC0125m3;
    }
}
