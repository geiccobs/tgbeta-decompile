package j$.util.stream;
/* renamed from: j$.util.stream.g1 */
/* loaded from: classes2.dex */
class C0086g1 extends AbstractC0104j1 implements AbstractC0112k3 {
    final /* synthetic */ EnumC0110k1 c;
    final /* synthetic */ j$.wrappers.V d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0086g1(EnumC0110k1 enumC0110k1, j$.wrappers.V v) {
        super(enumC0110k1);
        this.c = enumC0110k1;
        this.d = v;
    }

    @Override // j$.util.stream.AbstractC0104j1, j$.util.stream.AbstractC0124m3
    public void accept(int i) {
        boolean z;
        boolean z2;
        if (!this.a) {
            boolean b = this.d.b(i);
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
    public /* synthetic */ void accept(Integer num) {
        AbstractC0134o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
