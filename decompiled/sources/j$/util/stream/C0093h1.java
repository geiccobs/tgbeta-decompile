package j$.util.stream;

import j$.wrappers.C0214j0;
/* renamed from: j$.util.stream.h1 */
/* loaded from: classes2.dex */
class C0093h1 extends AbstractC0105j1 implements AbstractC0119l3 {
    final /* synthetic */ EnumC0111k1 c;
    final /* synthetic */ C0214j0 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0093h1(EnumC0111k1 enumC0111k1, C0214j0 c0214j0) {
        super(enumC0111k1);
        this.c = enumC0111k1;
        this.d = c0214j0;
    }

    @Override // j$.util.stream.AbstractC0105j1, j$.util.stream.AbstractC0125m3, j$.util.stream.AbstractC0119l3, j$.util.function.q
    public void accept(long j) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(j);
            z = this.c.a;
            if (b != z) {
                return;
            }
            this.a = true;
            z2 = this.c.b;
            this.b = z2;
        }
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractC0135o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
