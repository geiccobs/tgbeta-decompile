package j$.util.stream;
/* renamed from: j$.util.stream.v3 */
/* loaded from: classes2.dex */
public class C0178v3 extends AbstractC0061b1 {
    final /* synthetic */ long l;
    final /* synthetic */ long m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0178v3(AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, long j, long j2) {
        super(abstractC0065c, enumC0082e4, i);
        this.l = j;
        this.m = j2;
    }

    @Override // j$.util.stream.AbstractC0065c
    A1 E0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.m mVar) {
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            return AbstractC0187x2.h(abstractC0192y2, B3.b(abstractC0192y2.r0(), uVar, this.l, this.m), true);
        }
        return !EnumC0076d4.ORDERED.d(abstractC0192y2.s0()) ? AbstractC0187x2.h(this, N0((j$.util.v) abstractC0192y2.w0(uVar), this.l, this.m, q0), true) : (A1) new A3(this, abstractC0192y2, uVar, mVar, this.l, this.m).invoke();
    }

    @Override // j$.util.stream.AbstractC0065c
    j$.util.u F0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        long d;
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 > 0 && uVar.hasCharacteristics(16384)) {
            j$.util.v vVar = (j$.util.v) abstractC0192y2.w0(uVar);
            long j = this.l;
            d = B3.d(j, this.m);
            return new y4(vVar, j, d);
        }
        return !EnumC0076d4.ORDERED.d(abstractC0192y2.s0()) ? N0((j$.util.v) abstractC0192y2.w0(uVar), this.l, this.m, q0) : ((A1) new A3(this, abstractC0192y2, uVar, C0168t3.a, this.l, this.m).invoke()).mo69spliterator();
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        return new C0173u3(this, abstractC0129m3);
    }

    j$.util.v N0(j$.util.v vVar, long j, long j2, long j3) {
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
        return new G4(vVar, j5, j4);
    }
}
