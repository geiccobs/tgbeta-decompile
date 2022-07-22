package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class U2 implements N4 {
    private final EnumC0082e4 a;

    public U2(EnumC0082e4 enumC0082e4) {
        this.a = enumC0082e4;
    }

    public abstract S2 a();

    @Override // j$.util.stream.N4
    public /* synthetic */ int b() {
        return 0;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        return ((S2) new V2(this, abstractC0192y2, uVar).invoke()).get();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        S2 a = a();
        AbstractC0065c abstractC0065c = (AbstractC0065c) abstractC0192y2;
        a.getClass();
        abstractC0065c.n0(abstractC0065c.v0(a), uVar);
        return a.get();
    }
}
