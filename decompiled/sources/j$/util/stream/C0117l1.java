package j$.util.stream;
/* renamed from: j$.util.stream.l1 */
/* loaded from: classes2.dex */
public final class C0117l1 implements N4 {
    private final EnumC0078e4 a;
    final EnumC0111k1 b;
    final j$.util.function.y c;

    public C0117l1(EnumC0078e4 enumC0078e4, EnumC0111k1 enumC0111k1, j$.util.function.y yVar) {
        this.a = enumC0078e4;
        this.b = enumC0111k1;
        this.c = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumC0072d4.u | EnumC0072d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        return (Boolean) new C0123m1(this, abstractC0188y2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        AbstractC0105j1 abstractC0105j1 = (AbstractC0105j1) this.c.get();
        AbstractC0061c abstractC0061c = (AbstractC0061c) abstractC0188y2;
        abstractC0105j1.getClass();
        abstractC0061c.n0(abstractC0061c.v0(abstractC0105j1), uVar);
        return Boolean.valueOf(abstractC0105j1.b);
    }
}
