package j$.util.stream;
/* renamed from: j$.util.stream.i1 */
/* loaded from: classes2.dex */
class C0103i1 extends AbstractC0109j1 implements AbstractC0111j3 {
    final /* synthetic */ EnumC0115k1 c;
    final /* synthetic */ j$.wrappers.E d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0103i1(EnumC0115k1 enumC0115k1, j$.wrappers.E e) {
        super(enumC0115k1);
        this.c = enumC0115k1;
        this.d = e;
    }

    @Override // j$.util.stream.AbstractC0109j1, j$.util.stream.AbstractC0129m3
    public void accept(double d) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(d);
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
    public /* synthetic */ void accept(Double d) {
        AbstractC0139o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
