package j$.util.stream;
/* renamed from: j$.util.stream.l1 */
/* loaded from: classes2.dex */
public final class C0121l1 implements N4 {
    private final EnumC0082e4 a;
    final EnumC0115k1 b;
    final j$.util.function.y c;

    public C0121l1(EnumC0082e4 enumC0082e4, EnumC0115k1 enumC0115k1, j$.util.function.y yVar) {
        this.a = enumC0082e4;
        this.b = enumC0115k1;
        this.c = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumC0076d4.u | EnumC0076d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        return (Boolean) new C0127m1(this, abstractC0192y2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        AbstractC0109j1 abstractC0109j1 = (AbstractC0109j1) this.c.get();
        AbstractC0065c abstractC0065c = (AbstractC0065c) abstractC0192y2;
        abstractC0109j1.getClass();
        abstractC0065c.n0(abstractC0065c.v0(abstractC0109j1), uVar);
        return Boolean.valueOf(abstractC0109j1.b);
    }
}
