package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class L3 extends AbstractC0057b1 {
    public L3(AbstractC0061c abstractC0061c) {
        super(abstractC0061c, EnumC0078e4.LONG_VALUE, EnumC0072d4.q | EnumC0072d4.o);
    }

    @Override // j$.util.stream.AbstractC0061c
    public A1 E0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0072d4.SORTED.d(abstractC0188y2.s0())) {
            return abstractC0188y2.p0(uVar, false, mVar);
        }
        long[] jArr = (long[]) ((AbstractC0187y1) abstractC0188y2.p0(uVar, true, mVar)).e();
        Arrays.sort(jArr);
        return new C0118l2(jArr);
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        return EnumC0072d4.SORTED.d(i) ? abstractC0125m3 : EnumC0072d4.SIZED.d(i) ? new Q3(abstractC0125m3) : new I3(abstractC0125m3);
    }
}
