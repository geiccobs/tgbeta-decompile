package j$.util.stream;
/* renamed from: j$.util.stream.l1 */
/* loaded from: classes2.dex */
public final class C0116l1 implements N4 {
    private final EnumC0077e4 a;
    final EnumC0110k1 b;
    final j$.util.function.y c;

    public C0116l1(EnumC0077e4 enumC0077e4, EnumC0110k1 enumC0110k1, j$.util.function.y yVar) {
        this.a = enumC0077e4;
        this.b = enumC0110k1;
        this.c = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumC0071d4.u | EnumC0071d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        return (Boolean) new C0122m1(this, abstractC0187y2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        AbstractC0104j1 abstractC0104j1 = (AbstractC0104j1) this.c.get();
        AbstractC0060c abstractC0060c = (AbstractC0060c) abstractC0187y2;
        abstractC0104j1.getClass();
        abstractC0060c.n0(abstractC0060c.v0(abstractC0104j1), uVar);
        return Boolean.valueOf(abstractC0104j1.b);
    }
}
