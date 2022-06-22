package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class U2 implements N4 {
    private final EnumC0077e4 a;

    public U2(EnumC0077e4 enumC0077e4) {
        this.a = enumC0077e4;
    }

    public abstract S2 a();

    @Override // j$.util.stream.N4
    public /* synthetic */ int b() {
        return 0;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        return ((S2) new V2(this, abstractC0187y2, uVar).invoke()).get();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        S2 a = a();
        AbstractC0060c abstractC0060c = (AbstractC0060c) abstractC0187y2;
        a.getClass();
        abstractC0060c.n0(abstractC0060c.v0(a), uVar);
        return a.get();
    }
}
