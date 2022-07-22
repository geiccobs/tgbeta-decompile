package j$.util.stream;

import j$.util.Comparator$CC;
import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class M3 extends AbstractC0069c3 {
    private final boolean l;
    private final Comparator m;

    public M3(AbstractC0065c abstractC0065c) {
        super(abstractC0065c, EnumC0082e4.REFERENCE, EnumC0076d4.q | EnumC0076d4.o);
        this.l = true;
        this.m = Comparator$CC.a();
    }

    public M3(AbstractC0065c abstractC0065c, Comparator comparator) {
        super(abstractC0065c, EnumC0082e4.REFERENCE, EnumC0076d4.q | EnumC0076d4.p);
        this.l = false;
        comparator.getClass();
        this.m = comparator;
    }

    @Override // j$.util.stream.AbstractC0065c
    public A1 E0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (!EnumC0076d4.SORTED.d(abstractC0192y2.s0()) || !this.l) {
            Object[] q = abstractC0192y2.p0(uVar, true, mVar).q(mVar);
            Arrays.sort(q, this.m);
            return new D1(q);
        }
        return abstractC0192y2.p0(uVar, false, mVar);
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        return (!EnumC0076d4.SORTED.d(i) || !this.l) ? EnumC0076d4.SIZED.d(i) ? new R3(abstractC0129m3, this.m) : new N3(abstractC0129m3, this.m) : abstractC0129m3;
    }
}
