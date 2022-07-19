package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class U2 implements N4 {
    private final EnumC0078e4 a;

    public U2(EnumC0078e4 enumC0078e4) {
        this.a = enumC0078e4;
    }

    public abstract S2 a();

    @Override // j$.util.stream.N4
    public /* synthetic */ int b() {
        return 0;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        return ((S2) new V2(this, abstractC0188y2, uVar).invoke()).get();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        S2 a = a();
        AbstractC0061c abstractC0061c = (AbstractC0061c) abstractC0188y2;
        a.getClass();
        abstractC0061c.n0(abstractC0061c.v0(a), uVar);
        return a.get();
    }
}
