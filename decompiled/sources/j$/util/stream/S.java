package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class S extends T {
    public S(AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i) {
        super(abstractC0061c, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
