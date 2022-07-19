package j$.util.stream;
/* loaded from: classes2.dex */
class L2 extends U2 {
    final /* synthetic */ j$.util.function.j b;
    final /* synthetic */ int c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L2(EnumC0078e4 enumC0078e4, j$.util.function.j jVar, int i) {
        super(enumC0078e4);
        this.b = jVar;
        this.c = i;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        return new M2(this.c, this.b);
    }
}
