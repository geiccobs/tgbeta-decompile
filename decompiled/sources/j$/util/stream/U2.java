package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class U2 implements N4 {
    private final EnumC0083e4 a;

    public U2(EnumC0083e4 enumC0083e4) {
        this.a = enumC0083e4;
    }

    public abstract S2 a();

    @Override // j$.util.stream.N4
    public /* synthetic */ int b() {
        return 0;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        return ((S2) new V2(this, abstractC0193y2, uVar).invoke()).get();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        S2 a = a();
        AbstractC0066c abstractC0066c = (AbstractC0066c) abstractC0193y2;
        a.getClass();
        abstractC0066c.n0(abstractC0066c.v0(a), uVar);
        return a.get();
    }
}
