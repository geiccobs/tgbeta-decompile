package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class J3 extends Q {
    public J3(AbstractC0060c abstractC0060c) {
        super(abstractC0060c, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.q | EnumC0071d4.o);
    }

    @Override // j$.util.stream.AbstractC0060c
    public A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0071d4.SORTED.d(abstractC0187y2.s0())) {
            return abstractC0187y2.p0(uVar, false, mVar);
        }
        double[] dArr = (double[]) ((AbstractC0166u1) abstractC0187y2.p0(uVar, true, mVar)).e();
        Arrays.sort(dArr);
        return new T1(dArr);
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        abstractC0124m3.getClass();
        return EnumC0071d4.SORTED.d(i) ? abstractC0124m3 : EnumC0071d4.SIZED.d(i) ? new O3(abstractC0124m3) : new G3(abstractC0124m3);
    }
}
