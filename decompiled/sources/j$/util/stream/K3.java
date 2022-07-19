package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class K3 extends J0 {
    public K3(AbstractC0061c abstractC0061c) {
        super(abstractC0061c, EnumC0078e4.INT_VALUE, EnumC0072d4.q | EnumC0072d4.o);
    }

    @Override // j$.util.stream.AbstractC0061c
    public A1 E0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0072d4.SORTED.d(abstractC0188y2.s0())) {
            return abstractC0188y2.p0(uVar, false, mVar);
        }
        int[] iArr = (int[]) ((AbstractC0177w1) abstractC0188y2.p0(uVar, true, mVar)).e();
        Arrays.sort(iArr);
        return new C0064c2(iArr);
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        return EnumC0072d4.SORTED.d(i) ? abstractC0125m3 : EnumC0072d4.SIZED.d(i) ? new P3(abstractC0125m3) : new H3(abstractC0125m3);
    }
}
