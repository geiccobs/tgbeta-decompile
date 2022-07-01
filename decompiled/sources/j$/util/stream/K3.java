package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class K3 extends J0 {
    public K3(AbstractC0060c abstractC0060c) {
        super(abstractC0060c, EnumC0077e4.INT_VALUE, EnumC0071d4.q | EnumC0071d4.o);
    }

    @Override // j$.util.stream.AbstractC0060c
    public A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0071d4.SORTED.d(abstractC0187y2.s0())) {
            return abstractC0187y2.p0(uVar, false, mVar);
        }
        int[] iArr = (int[]) ((AbstractC0176w1) abstractC0187y2.p0(uVar, true, mVar)).e();
        Arrays.sort(iArr);
        return new C0063c2(iArr);
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        abstractC0124m3.getClass();
        return EnumC0071d4.SORTED.d(i) ? abstractC0124m3 : EnumC0071d4.SIZED.d(i) ? new P3(abstractC0124m3) : new H3(abstractC0124m3);
    }
}
