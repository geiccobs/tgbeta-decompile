package j$.util.stream;
/* renamed from: j$.util.stream.p3 */
/* loaded from: classes2.dex */
public class C0142p3 extends AbstractC0064c3 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0142p3(AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, long j, long j2) {
        super(abstractC0060c, enumC0077e4, i);
        this.l = j;
        this.m = j2;
    }

    @Override // j$.util.stream.AbstractC0060c
    A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            return AbstractC0182x2.e(abstractC0187y2, B3.b(abstractC0187y2.r0(), uVar, this.l, this.m), true, mVar);
        }
        return !EnumC0071d4.ORDERED.d(abstractC0187y2.s0()) ? AbstractC0182x2.e(this, L0(abstractC0187y2.w0(uVar), this.l, this.m, q0), true, mVar) : (A1) new A3(this, abstractC0187y2, uVar, mVar, this.l, this.m).invoke();
    }

    @Override // j$.util.stream.AbstractC0060c
    j$.util.u F0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        long d;
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            j$.util.u w0 = abstractC0187y2.w0(uVar);
            long j = this.l;
            d = B3.d(j, this.m);
            return new C4(w0, j, d);
        }
        return !EnumC0071d4.ORDERED.d(abstractC0187y2.s0()) ? L0(abstractC0187y2.w0(uVar), this.l, this.m, q0) : ((A1) new A3(this, abstractC0187y2, uVar, C0130n3.a, this.l, this.m).invoke()).mo69spliterator();
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        return new C0136o3(this, abstractC0124m3);
    }

    j$.util.u L0(j$.util.u uVar, long j, long j2, long j3) {
        long j4;
        long j5;
        if (j <= j3) {
            long j6 = j3 - j;
            j4 = j2 >= 0 ? Math.min(j2, j6) : j6;
            j5 = 0;
        } else {
            j5 = j;
            j4 = j2;
        }
        return new I4(uVar, j5, j4);
    }
}
