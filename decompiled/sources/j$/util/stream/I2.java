package j$.util.stream;

import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
class I2 extends U2 {
    final /* synthetic */ j$.util.function.b b;
    final /* synthetic */ BiConsumer c;
    final /* synthetic */ j$.util.function.y d;
    final /* synthetic */ j$.wrappers.J0 e;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public I2(EnumC0077e4 enumC0077e4, j$.util.function.b bVar, BiConsumer biConsumer, j$.util.function.y yVar, j$.wrappers.J0 j0) {
        super(enumC0077e4);
        this.b = bVar;
        this.c = biConsumer;
        this.d = yVar;
        this.e = j0;
    }

    @Override // j$.util.stream.U2
    public S2 a() {
        return new J2(this.d, this.c, this.b);
    }

    @Override // j$.util.stream.U2, j$.util.stream.N4
    public int b() {
        if (this.e.b().contains(EnumC0090h.UNORDERED)) {
            return EnumC0071d4.r;
        }
        return 0;
    }
}
