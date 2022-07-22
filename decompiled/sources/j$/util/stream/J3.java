package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class J3 extends Q {
    public J3(AbstractC0065c abstractC0065c) {
        super(abstractC0065c, EnumC0082e4.DOUBLE_VALUE, EnumC0076d4.q | EnumC0076d4.o);
    }

    @Override // j$.util.stream.AbstractC0065c
    public A1 E0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.m mVar) {
        if (EnumC0076d4.SORTED.d(abstractC0192y2.s0())) {
            return abstractC0192y2.p0(uVar, false, mVar);
        }
        double[] dArr = (double[]) ((AbstractC0171u1) abstractC0192y2.p0(uVar, true, mVar)).e();
        Arrays.sort(dArr);
        return new T1(dArr);
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        return EnumC0076d4.SORTED.d(i) ? abstractC0129m3 : EnumC0076d4.SIZED.d(i) ? new O3(abstractC0129m3) : new G3(abstractC0129m3);
    }
}
