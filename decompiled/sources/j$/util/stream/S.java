package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class S extends T {
    public S(AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i) {
        super(abstractC0065c, i);
    }

    @Override // j$.util.stream.AbstractC0065c
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
