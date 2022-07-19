package j$.util.stream;

import j$.wrappers.C0219j0;
/* renamed from: j$.util.stream.h1 */
/* loaded from: classes2.dex */
class C0098h1 extends AbstractC0110j1 implements AbstractC0124l3 {
    final /* synthetic */ EnumC0116k1 c;
    final /* synthetic */ C0219j0 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0098h1(EnumC0116k1 enumC0116k1, C0219j0 c0219j0) {
        super(enumC0116k1);
        this.c = enumC0116k1;
        this.d = c0219j0;
    }

    @Override // j$.util.stream.AbstractC0110j1, j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
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
        AbstractC0140o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
