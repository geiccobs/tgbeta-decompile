package j$.util.stream;
/* renamed from: j$.util.stream.l1 */
/* loaded from: classes2.dex */
public final class C0122l1 implements N4 {
    private final EnumC0083e4 a;
    final EnumC0116k1 b;
    final j$.util.function.y c;

    public C0122l1(EnumC0083e4 enumC0083e4, EnumC0116k1 enumC0116k1, j$.util.function.y yVar) {
        this.a = enumC0083e4;
        this.b = enumC0116k1;
        this.c = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumC0077d4.u | EnumC0077d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        return (Boolean) new C0128m1(this, abstractC0193y2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        AbstractC0110j1 abstractC0110j1 = (AbstractC0110j1) this.c.get();
        AbstractC0066c abstractC0066c = (AbstractC0066c) abstractC0193y2;
        abstractC0110j1.getClass();
        abstractC0066c.n0(abstractC0066c.v0(abstractC0110j1), uVar);
        return Boolean.valueOf(abstractC0110j1.b);
    }
}
