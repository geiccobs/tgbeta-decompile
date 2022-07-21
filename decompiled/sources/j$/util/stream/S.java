package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class S extends T {
    public S(AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i) {
        super(abstractC0066c, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
