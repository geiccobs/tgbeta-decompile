package j$.util.stream;

import j$.util.Comparator$CC;
import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class M3 extends AbstractC0064c3 {
    private final boolean l;
    private final Comparator m;

    public M3(AbstractC0060c abstractC0060c) {
        super(abstractC0060c, EnumC0077e4.REFERENCE, EnumC0071d4.q | EnumC0071d4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    public M3(AbstractC0060c abstractC0060c, Comparator comparator) {
        super(abstractC0060c, EnumC0077e4.REFERENCE, EnumC0071d4.q | EnumC0071d4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    @Override // j$.util.stream.AbstractC0060c
    public A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (!EnumC0071d4.SORTED.d(abstractC0187y2.s0()) || !this.l) {
            Object[] q = abstractC0187y2.p0(uVar, true, mVar).q(mVar);
            Arrays.sort(q, this.m);
            return new D1(q);
        }
        return abstractC0187y2.p0(uVar, false, mVar);
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        abstractC0124m3.getClass();
        return (!EnumC0071d4.SORTED.d(i) || !this.l) ? EnumC0071d4.SIZED.d(i) ? new R3(abstractC0124m3, this.m) : new N3(abstractC0124m3, this.m) : abstractC0124m3;
    }
}
