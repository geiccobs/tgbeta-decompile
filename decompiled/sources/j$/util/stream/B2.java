package j$.util.stream;
/* loaded from: classes2.dex */
class B2 extends U2 {
    final /* synthetic */ j$.util.function.d b;
    final /* synthetic */ double c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public B2(EnumC0077e4 enumC0077e4, j$.util.function.d dVar, double d) {
        super(enumC0077e4);
        this.b = dVar;
        this.c = d;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        return new C2(this.c, this.b);
    }
}
